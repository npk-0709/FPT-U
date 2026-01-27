<?php
// resolvers/WarehouseResolver.php

class WarehouseResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function getWarehouses($args, $context) {
        $this->auth->requirePermission($context, PERM_WAREHOUSE_VIEW);
        
        $query = "
            SELECT w.*, u.full_name as manager_name
            FROM Warehouses w
            LEFT JOIN Users u ON w.manager_id = u.user_id
            WHERE w.is_active = TRUE
            ORDER BY w.warehouse_name
        ";
        
        $result = $this->db->query($query);
        
        $warehouses = [];
        while ($row = $result->fetch_assoc()) {
            $warehouses[] = $row;
        }
        
        return ['warehouses' => $warehouses];
    }
    
    public function getMaterialStock($args, $context) {
        $this->auth->requirePermission($context, PERM_WAREHOUSE_VIEW);
        
        $query = "
            SELECT ms.*, m.material_name, m.material_code, m.unit,
                   w.warehouse_name
            FROM Material_Stock ms
            JOIN Materials m ON ms.material_id = m.material_id
            JOIN Warehouses w ON ms.warehouse_id = w.warehouse_id
            WHERE ms.warehouse_id = ?
            ORDER BY m.material_name
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['warehouse_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $stock = [];
        while ($row = $result->fetch_assoc()) {
            $stock[] = $row;
        }
        
        return ['material_stock' => $stock];
    }
    
    public function getProductStock($args, $context) {
        $this->auth->requirePermission($context, PERM_WAREHOUSE_VIEW);
        
        $query = "
            SELECT ps.*, p.product_name, p.product_code,
                   pv.sku, pv.size, pv.color_name,
                   w.warehouse_name
            FROM Product_Stock ps
            JOIN Products p ON ps.product_id = p.product_id
            LEFT JOIN Product_Variants pv ON ps.variant_id = pv.variant_id
            JOIN Warehouses w ON ps.warehouse_id = w.warehouse_id
            WHERE ps.warehouse_id = ?
            ORDER BY p.product_name
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['warehouse_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $stock = [];
        while ($row = $result->fetch_assoc()) {
            $stock[] = $row;
        }
        
        return ['product_stock' => $stock];
    }
    
    public function createMaterialTransaction($args, $context) {
        $this->auth->requirePermission($context, PERM_WAREHOUSE_MANAGE);
        $user = $this->auth->requireAuth($context);
        
        $this->db->begin_transaction();
        
        try {
            // Insert transaction
            $query = "
                INSERT INTO Material_Transactions (
                    warehouse_id, material_id, transaction_type, quantity,
                    unit_price, reference_type, reference_id, notes, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ";
            
            $stmt = $this->db->prepare($query);
            $stmt->bind_param(
                "iisdsissi",
                $args['warehouse_id'],
                $args['material_id'],
                $args['transaction_type'],
                $args['quantity'],
                $args['unit_price'] ?? null,
                $args['reference_type'] ?? null,
                $args['reference_id'] ?? null,
                $args['notes'] ?? null,
                $user['user_id']
            );
            
            if (!$stmt->execute()) {
                throw new Exception('Failed to create transaction');
            }
            
            // Update stock
            $multiplier = ($args['transaction_type'] === 'IN') ? 1 : -1;
            $stockQuery = "
                INSERT INTO Material_Stock (warehouse_id, material_id, quantity)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE quantity = quantity + ?
            ";
            
            $stockStmt = $this->db->prepare($stockQuery);
            $quantityChange = $args['quantity'] * $multiplier;
            $stockStmt->bind_param("iidd", $args['warehouse_id'], $args['material_id'], $quantityChange, $quantityChange);
            
            if (!$stockStmt->execute()) {
                throw new Exception('Failed to update stock');
            }
            
            $this->db->commit();
            
            return ['message' => 'Material transaction created successfully'];
            
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }
    
    public function createProductTransaction($args, $context) {
        $this->auth->requirePermission($context, PERM_WAREHOUSE_MANAGE);
        $user = $this->auth->requireAuth($context);
        
        $this->db->begin_transaction();
        
        try {
            $query = "
                INSERT INTO Product_Transactions (
                    warehouse_id, product_id, variant_id, transaction_type,
                    quantity, unit_price, reference_type, reference_id, notes, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ";
            
            $stmt = $this->db->prepare($query);
            $stmt->bind_param(
                "iiisidsisi",
                $args['warehouse_id'],
                $args['product_id'],
                $args['variant_id'] ?? null,
                $args['transaction_type'],
                $args['quantity'],
                $args['unit_price'] ?? null,
                $args['reference_type'] ?? null,
                $args['reference_id'] ?? null,
                $args['notes'] ?? null,
                $user['user_id']
            );
            
            if (!$stmt->execute()) {
                throw new Exception('Failed to create transaction');
            }
            
            // Update stock
            $multiplier = ($args['transaction_type'] === 'IN') ? 1 : -1;
            $stockQuery = "
                INSERT INTO Product_Stock (warehouse_id, product_id, variant_id, quantity)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE quantity = quantity + ?
            ";
            
            $stockStmt = $this->db->prepare($stockQuery);
            $quantityChange = $args['quantity'] * $multiplier;
            $stockStmt->bind_param("iiiii", $args['warehouse_id'], $args['product_id'], $args['variant_id'], $quantityChange, $quantityChange);
            
            if (!$stockStmt->execute()) {
                throw new Exception('Failed to update stock');
            }
            
            $this->db->commit();
            
            return ['message' => 'Product transaction created successfully'];
            
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }
}