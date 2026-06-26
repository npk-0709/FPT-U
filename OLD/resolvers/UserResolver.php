<?php
// resolvers/UserResolver.php

class UserResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function login($args, $context) {
        if (!isset($args['username']) || !isset($args['password'])) {
            throw new Exception('Username and password required');
        }
        
        return $this->auth->login($args['username'], $args['password']);
    }
    
    public function me($args, $context) {
        $user = $this->auth->requireAuth($context);
        
        $query = "
            SELECT u.user_id, u.username, u.full_name, u.email, u.phone, 
                   u.avatar_url, u.is_active, u.created_at, u.last_login,
                   r.role_name, r.description as role_description
            FROM Users u
            LEFT JOIN Roles r ON u.role_id = r.role_id
            WHERE u.user_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $user['user_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        return $result->fetch_assoc();
    }
    
    public function getUsers($args, $context) {
        $this->auth->requirePermission($context, PERM_USER_VIEW);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        $search = $args['search'] ?? null;
        
        $query = "
            SELECT u.user_id, u.username, u.full_name, u.email, u.phone,
                   u.is_active, u.created_at, u.last_login,
                   r.role_name
            FROM Users u
            LEFT JOIN Roles r ON u.role_id = r.role_id
            WHERE 1=1
        ";
        
        $params = [];
        $types = "";
        
        if ($search) {
            $query .= " AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?)";
            $searchParam = "%$search%";
            $params[] = $searchParam;
            $params[] = $searchParam;
            $params[] = $searchParam;
            $types .= "sss";
        }
        
        $query .= " ORDER BY u.created_at DESC LIMIT ? OFFSET ?";
        $params[] = $limit;
        $params[] = $offset;
        $types .= "ii";
        
        $stmt = $this->db->prepare($query);
        if ($params) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        
        $users = [];
        while ($row = $result->fetch_assoc()) {
            $users[] = $row;
        }
        
        // Get total count
        $countQuery = "SELECT COUNT(*) as total FROM Users WHERE 1=1";
        if ($search) {
            $countQuery .= " AND (username LIKE ? OR full_name LIKE ? OR email LIKE ?)";
        }
        
        $countStmt = $this->db->prepare($countQuery);
        if ($search) {
            $countStmt->bind_param("sss", $searchParam, $searchParam, $searchParam);
        }
        $countStmt->execute();
        $countResult = $countStmt->get_result();
        $total = $countResult->fetch_assoc()['total'];
        
        return [
            'users' => $users,
            'total' => $total,
            'limit' => $limit,
            'offset' => $offset
        ];
    }
    
    public function getUser($args, $context) {
        $this->auth->requirePermission($context, PERM_USER_VIEW);
        
        if (!isset($args['user_id'])) {
            throw new Exception('user_id required');
        }
        
        $query = "
            SELECT u.*, r.role_name, r.description as role_description
            FROM Users u
            LEFT JOIN Roles r ON u.role_id = r.role_id
            WHERE u.user_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['user_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('User not found');
        }
        
        $user = $result->fetch_assoc();
        unset($user['password_hash']);
        
        return $user;
    }
    
    public function createUser($args, $context) {
        $this->auth->requirePermission($context, PERM_USER_CREATE);
        
        $required = ['username', 'password', 'full_name', 'role_id'];
        foreach ($required as $field) {
            if (!isset($args[$field])) {
                throw new Exception("$field is required");
            }
        }
        
        // Check if username exists
        $checkQuery = "SELECT user_id FROM Users WHERE username = ?";
        $checkStmt = $this->db->prepare($checkQuery);
        $checkStmt->bind_param("s", $args['username']);
        $checkStmt->execute();
        if ($checkStmt->get_result()->num_rows > 0) {
            throw new Exception('Username already exists');
        }
        
        $passwordHash = password_hash($args['password'], PASSWORD_BCRYPT);
        
        $query = "
            INSERT INTO Users (username, password_hash, full_name, email, phone, role_id, avatar_url)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "ssssis",
            $args['username'],
            $passwordHash,
            $args['full_name'],
            $args['email'] ?? null,
            $args['phone'] ?? null,
            $args['role_id'],
            $args['avatar_url'] ?? null
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to create user: ' . $stmt->error);
        }
        
        return [
            'user_id' => $stmt->insert_id,
            'message' => 'User created successfully'
        ];
    }
    
    public function updateUser($args, $context) {
        $this->auth->requirePermission($context, PERM_USER_EDIT);
        
        if (!isset($args['user_id'])) {
            throw new Exception('user_id required');
        }
        
        $updates = [];
        $params = [];
        $types = "";
        
        $allowedFields = ['full_name', 'email', 'phone', 'role_id', 'avatar_url', 'is_active'];
        
        foreach ($allowedFields as $field) {
            if (isset($args[$field])) {
                $updates[] = "$field = ?";
                $params[] = $args[$field];
                $types .= $field === 'role_id' || $field === 'is_active' ? "i" : "s";
            }
        }
        
        if (isset($args['password'])) {
            $updates[] = "password_hash = ?";
            $params[] = password_hash($args['password'], PASSWORD_BCRYPT);
            $types .= "s";
        }
        
        if (empty($updates)) {
            throw new Exception('No fields to update');
        }
        
        $params[] = $args['user_id'];
        $types .= "i";
        
        $query = "UPDATE Users SET " . implode(', ', $updates) . " WHERE user_id = ?";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param($types, ...$params);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to update user: ' . $stmt->error);
        }
        
        return [
            'user_id' => $args['user_id'],
            'message' => 'User updated successfully'
        ];
    }
    
    public function deleteUser($args, $context) {
        $this->auth->requirePermission($context, PERM_USER_DELETE);
        
        if (!isset($args['user_id'])) {
            throw new Exception('user_id required');
        }
        
        // Soft delete
        $query = "UPDATE Users SET is_active = FALSE WHERE user_id = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['user_id']);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to delete user: ' . $stmt->error);
        }
        
        return [
            'user_id' => $args['user_id'],
            'message' => 'User deleted successfully'
        ];
    }
}