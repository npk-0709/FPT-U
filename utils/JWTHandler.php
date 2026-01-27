<?php
// utils/JWTHandler.php

class JWTHandler {
    
    public static function encode($payload) {
        $header = json_encode(['typ' => 'JWT', 'alg' => JWT_ALGORITHM]);
        
        $payload['iat'] = time();
        $payload['exp'] = time() + JWT_EXPIRY;
        
        $base64UrlHeader = self::base64UrlEncode($header);
        $base64UrlPayload = self::base64UrlEncode(json_encode($payload));
        
        $signature = hash_hmac(
            'sha256',
            $base64UrlHeader . "." . $base64UrlPayload,
            JWT_SECRET,
            true
        );
        
        $base64UrlSignature = self::base64UrlEncode($signature);
        
        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }
    
    public static function decode($jwt) {
        $parts = explode('.', $jwt);
        
        if (count($parts) !== 3) {
            throw new Exception('Invalid token format');
        }
        
        list($base64UrlHeader, $base64UrlPayload, $base64UrlSignature) = $parts;
        
        $signature = self::base64UrlDecode($base64UrlSignature);
        $expectedSignature = hash_hmac(
            'sha256',
            $base64UrlHeader . "." . $base64UrlPayload,
            JWT_SECRET,
            true
        );
        
        if (!hash_equals($signature, $expectedSignature)) {
            throw new Exception('Invalid token signature');
        }
        
        $payload = json_decode(self::base64UrlDecode($base64UrlPayload), true);
        
        if (!isset($payload['exp']) || $payload['exp'] < time()) {
            throw new Exception('Token has expired');
        }
        
        return $payload;
    }
    
    private static function base64UrlEncode($data) {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }
    
    private static function base64UrlDecode($data) {
        return base64_decode(strtr($data, '-_', '+/'));
    }
}

// core/Auth.php

class Auth {
    private $db;
    
    public function __construct($db) {
        $this->db = $db;
    }
    
    public function requireAuth($context) {
        if (!isset($context['user'])) {
            throw new Exception('Authentication required');
        }
        return $context['user'];
    }
    
    public function requirePermission($context, $permission) {
        $user = $this->requireAuth($context);
        
        if (!$this->hasPermission($user['user_id'], $permission)) {
            throw new Exception("Permission denied: $permission required");
        }
        
        return true;
    }
    
    public function hasPermission($userId, $permissionCode) {
        $query = "
            SELECT COUNT(*) as count
            FROM Users u
            JOIN Role_Permissions rp ON u.role_id = rp.role_id
            JOIN Permissions p ON rp.permission_id = p.permission_id
            WHERE u.user_id = ? AND p.permission_code = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("is", $userId, $permissionCode);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        
        return $row['count'] > 0;
    }
    
    public function login($username, $password) {
        $query = "
            SELECT u.*, r.role_name
            FROM Users u
            LEFT JOIN Roles r ON u.role_id = r.role_id
            WHERE u.username = ? AND u.is_active = TRUE
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('Invalid credentials');
        }
        
        $user = $result->fetch_assoc();
        
        if (!password_verify($password, $user['password_hash'])) {
            throw new Exception('Invalid credentials');
        }
        
        // Update last login
        $updateQuery = "UPDATE Users SET last_login = NOW() WHERE user_id = ?";
        $updateStmt = $this->db->prepare($updateQuery);
        $updateStmt->bind_param("i", $user['user_id']);
        $updateStmt->execute();
        
        // Generate token
        $tokenPayload = [
            'user_id' => $user['user_id'],
            'username' => $user['username'],
            'role_id' => $user['role_id'],
            'role_name' => $user['role_name']
        ];
        
        $token = JWTHandler::encode($tokenPayload);
        
        return [
            'token' => $token,
            'user' => [
                'user_id' => $user['user_id'],
                'username' => $user['username'],
                'full_name' => $user['full_name'],
                'email' => $user['email'],
                'role_name' => $user['role_name']
            ]
        ];
    }
}