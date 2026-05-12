<?php

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