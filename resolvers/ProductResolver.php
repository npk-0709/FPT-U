<?php
// resolvers/ProductResolver.php

class ProductResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function getProducts($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_VIEW);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        $search = $args['search'] ?? null;
        $category_id = $args['category_id'] ?? null;
        
        $query = "
            SELECT p.product_id, p.product_code, p.product_name, p.description,
                   p.base_price, p.gender, p.season, p.brand, p.is_active,
                   p.created_at, p.updated_at,
                   pc.category_name,
                   u.full_name as created_by_name
            FROM Products p
            LEFT JOIN Product_Categories pc ON p.category_id = pc.category_id
            LEFT JOIN Users u ON p.created_by = u.user_id
            WHERE 1=1
        ";
        
        $params = [];
        $types = "";
        
        if ($search) {
            $query .= " AND (p.product_code LIKE ? OR p.product_name LIKE ?)";
            $searchParam = "%$search%";
            $params[] = $searchParam;
            $params[] = $searchParam;
            $types .= "ss";
        }
        
        if ($category_id) {
            $query .= " AND p.category_id = ?";
            $params[] = $category_id;
            $types .= "i";
        }
        
        $query .= " ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        $params[] = $limit;
        $params[] = $offset;
        $types .= "ii";
        
        $stmt = $this->db->prepare($query);
        if ($params) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        
        $products = [];
        while ($row = $result->fetch_assoc()) {
            // Get variant count
            $variantQuery = "SELECT COUNT(*) as count FROM Product_Variants WHERE product_id = ?";
            $variantStmt = $this->db->prepare($variantQuery);
            $variantStmt->bind_param("i", $row['product_id']);
            $variantStmt->execute();
            $variantResult = $variantStmt->get_result();
            $row['variant_count'] = $variantResult->fetch_assoc()['count'];
            
            $products[] = $row;
        }
        
        // Get total count
        $countQuery = "SELECT COUNT(*) as total FROM Products WHERE 1=1";
        $countParams = [];
        $countTypes = "";
        
        if ($search) {
            $countQuery .= " AND (product_code LIKE ? OR product_name LIKE ?)";
            $countParams[] = $searchParam;
            $countParams[] = $searchParam;
            $countTypes .= "ss";
        }
        
        if ($category_id) {
            $countQuery .= " AND category_id = ?";
            $countParams[] = $category_id;
            $countTypes .= "i";
        }
        
        $countStmt = $this->db->prepare($countQuery);
        if ($countParams) {
            $countStmt->bind_param($countTypes, ...$countParams);
        }
        $countStmt->execute();
        $total = $countStmt->get_result()->fetch_assoc()['total'];
        
        return [
            'products' => $products,
            'total' => $total,
            'limit' => $limit,
            'offset' => $offset
        ];
    }
    
    public function getProduct($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_VIEW);
        
        if (!isset($args['product_id'])) {
            throw new Exception('product_id required');
        }
        
        $query = "
            SELECT p.*, pc.category_name, u.full_name as created_by_name
            FROM Products p
            LEFT JOIN Product_Categories pc ON p.category_id = pc.category_id
            LEFT JOIN Users u ON p.created_by = u.user_id
            WHERE p.product_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['product_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('Product not found');
        }
        
        $product = $result->fetch_assoc();
        
        // Get variants
        $variantQuery = "
            SELECT * FROM Product_Variants 
            WHERE product_id = ? AND is_active = TRUE
            ORDER BY size, color_name
        ";
        $variantStmt = $this->db->prepare($variantQuery);
        $variantStmt->bind_param("i", $args['product_id']);
        $variantStmt->execute();
        $variantResult = $variantStmt->get_result();
        
        $product['variants'] = [];
        while ($variant = $variantResult->fetch_assoc()) {
            $product['variants'][] = $variant;
        }
        
        // Get images
        $imageQuery = "SELECT * FROM Product_Images WHERE product_id = ? ORDER BY display_order";
        $imageStmt = $this->db->prepare($imageQuery);
        $imageStmt->bind_param("i", $args['product_id']);
        $imageStmt->execute();
        $imageResult = $imageStmt->get_result();
        
        $product['images'] = [];
        while ($image = $imageResult->fetch_assoc()) {
            $product['images'][] = $image;
        }
        
        return $product;
    }
    
    public function getProductVariants($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_VIEW);
        
        if (!isset($args['product_id'])) {
            throw new Exception('product_id required');
        }
        
        $query = "
            SELECT pv.*, p.product_name, p.product_code
            FROM Product_Variants pv
            JOIN Products p ON pv.product_id = p.product_id
            WHERE pv.product_id = ?
            ORDER BY pv.size, pv.color_name
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['product_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $variants = [];
        while ($row = $result->fetch_assoc()) {
            $variants[] = $row;
        }
        
        return $variants;
    }
    
    public function createProduct($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_CREATE);
        $user = $this->auth->requireAuth($context);
        
        $required = ['product_code', 'product_name', 'category_id'];
        foreach ($required as $field) {
            if (!isset($args[$field])) {
                throw new Exception("$field is required");
            }
        }
        
        // Check if product code exists
        $checkQuery = "SELECT product_id FROM Products WHERE product_code = ?";
        $checkStmt = $this->db->prepare($checkQuery);
        $checkStmt->bind_param("s", $args['product_code']);
        $checkStmt->execute();
        if ($checkStmt->get_result()->num_rows > 0) {
            throw new Exception('Product code already exists');
        }
        
        $query = "
            INSERT INTO Products (product_code, product_name, category_id, description, 
                                 base_price, gender, season, brand, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "ssisdsss i",
            $args['product_code'],
            $args['product_name'],
            $args['category_id'],
            $args['description'] ?? null,
            $args['base_price'] ?? null,
            $args['gender'] ?? null,
            $args['season'] ?? null,
            $args['brand'] ?? null,
            $user['user_id']
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to create product: ' . $stmt->error);
        }
        
        $productId = $stmt->insert_id;
        
        // Create variants if provided
        if (isset($args['variants']) && is_array($args['variants'])) {
            foreach ($args['variants'] as $variant) {
                $this->createProductVariant($productId, $variant);
            }
        }
        
        return [
            'product_id' => $productId,
            'message' => 'Product created successfully'
        ];
    }
    
    private function createProductVariant($productId, $variantData) {
        $sku = $variantData['sku'];
        
        $query = "
            INSERT INTO Product_Variants (product_id, sku, size, color_name, color_code,
                                         price, weight_grams, stock_quantity, barcode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "issssdiis",
            $productId,
            $sku,
            $variantData['size'] ?? null,
            $variantData['color_name'] ?? null,
            $variantData['color_code'] ?? null,
            $variantData['price'] ?? null,
            $variantData['weight_grams'] ?? null,
            $variantData['stock_quantity'] ?? 0,
            $variantData['barcode'] ?? null
        );
        
        return $stmt->execute();
    }
    
    public function updateProduct($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_EDIT);
        
        if (!isset($args['product_id'])) {
            throw new Exception('product_id required');
        }
        
        $updates = [];
        $params = [];
        $types = "";
        
        $allowedFields = [
            'product_name', 'category_id', 'description', 'base_price',
            'gender', 'season', 'brand', 'is_active'
        ];
        
        foreach ($allowedFields as $field) {
            if (isset($args[$field])) {
                $updates[] = "$field = ?";
                $params[] = $args[$field];
                
                if (in_array($field, ['category_id', 'is_active'])) {
                    $types .= "i";
                } elseif ($field === 'base_price') {
                    $types .= "d";
                } else {
                    $types .= "s";
                }
            }
        }
        
        if (empty($updates)) {
            throw new Exception('No fields to update');
        }
        
        $params[] = $args['product_id'];
        $types .= "i";
        
        $query = "UPDATE Products SET " . implode(', ', $updates) . " WHERE product_id = ?";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param($types, ...$params);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to update product: ' . $stmt->error);
        }
        
        return [
            'product_id' => $args['product_id'],
            'message' => 'Product updated successfully'
        ];
    }
    
    public function deleteProduct($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCT_DELETE);
        
        if (!isset($args['product_id'])) {
            throw new Exception('product_id required');
        }
        
        // Soft delete
        $query = "UPDATE Products SET is_active = FALSE WHERE product_id = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['product_id']);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to delete product: ' . $stmt->error);
        }
        
        // Also soft delete variants
        $variantQuery = "UPDATE Product_Variants SET is_active = FALSE WHERE product_id = ?";
        $variantStmt = $this->db->prepare($variantQuery);
        $variantStmt->bind_param("i", $args['product_id']);
        $variantStmt->execute();
        
        return [
            'product_id' => $args['product_id'],
            'message' => 'Product deleted successfully'
        ];
    }
}