<?php

// resolvers/ProductionResolver.php

class ProductionResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function getProductionOrders($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCTION_VIEW);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        $status = $args['status'] ?? null;
        
        $query = "
            SELECT po.*, p.product_name, p.product_code,
                   pv.sku, pv.size, pv.color_name,
                   o.order_code,
                   u.full_name as created_by_name
            FROM Production_Orders po
            JOIN Products p ON po.product_id = p.product_id
            LEFT JOIN Product_Variants pv ON po.variant_id = pv.variant_id
            LEFT JOIN Orders o ON po.order_id = o.order_id
            LEFT JOIN Users u ON po.created_by = u.user_id
            WHERE 1=1
        ";
        
        $params = [];
        $types = "";
        
        if ($status) {
            $query .= " AND po.production_status = ?";
            $params[] = $status;
            $types .= "s";
        }
        
        $query .= " ORDER BY po.created_at DESC LIMIT ? OFFSET ?";
        $params[] = $limit;
        $params[] = $offset;
        $types .= "ii";
        
        $stmt = $this->db->prepare($query);
        if ($params) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        
        $orders = [];
        while ($row = $result->fetch_assoc()) {
            $orders[] = $row;
        }
        
        return ['production_orders' => $orders];
    }
    
    public function getProductionOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCTION_VIEW);
        
        $query = "
            SELECT po.*, p.product_name, p.product_code,
                   pv.sku, pv.size, pv.color_name
            FROM Production_Orders po
            JOIN Products p ON po.product_id = p.product_id
            LEFT JOIN Product_Variants pv ON po.variant_id = pv.variant_id
            WHERE po.production_order_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['production_order_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('Production order not found');
        }
        
        $order = $result->fetch_assoc();
        
        // Get stage progress
        $stageQuery = "
            SELECT psp.*, ps.stage_name, u.full_name as assigned_to_name
            FROM Production_Stage_Progress psp
            JOIN Production_Stages ps ON psp.stage_id = ps.stage_id
            LEFT JOIN Users u ON psp.assigned_to = u.user_id
            WHERE psp.production_order_id = ?
            ORDER BY ps.stage_order
        ";
        
        $stageStmt = $this->db->prepare($stageQuery);
        $stageStmt->bind_param("i", $args['production_order_id']);
        $stageStmt->execute();
        $stageResult = $stageStmt->get_result();
        
        $order['stages'] = [];
        while ($stage = $stageResult->fetch_assoc()) {
            $order['stages'][] = $stage;
        }
        
        return $order;
    }
    
    public function createProductionOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCTION_CREATE);
        $user = $this->auth->requireAuth($context);
        
        $productionCode = 'PRO' . date('Ymd') . sprintf('%04d', rand(1, 9999));
        
        $query = "
            INSERT INTO Production_Orders (
                production_code, order_id, product_id, variant_id,
                quantity_planned, priority, planned_start_date, planned_end_date,
                notes, created_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "siiiissssi",
            $productionCode,
            $args['order_id'] ?? null,
            $args['product_id'],
            $args['variant_id'] ?? null,
            $args['quantity_planned'],
            $args['priority'] ?? 'Normal',
            $args['planned_start_date'] ?? null,
            $args['planned_end_date'] ?? null,
            $args['notes'] ?? null,
            $user['user_id']
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to create production order');
        }
        
        return [
            'production_order_id' => $stmt->insert_id,
            'production_code' => $productionCode,
            'message' => 'Production order created successfully'
        ];
    }
    
    public function updateProductionStatus($args, $context) {
        $this->auth->requirePermission($context, PERM_PRODUCTION_EDIT);
        
        $query = "
            UPDATE Production_Orders 
            SET production_status = ?,
                quantity_produced = ?,
                actual_start_date = ?,
                actual_end_date = ?
            WHERE production_order_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "sissi",
            $args['production_status'],
            $args['quantity_produced'] ?? null,
            $args['actual_start_date'] ?? null,
            $args['actual_end_date'] ?? null,
            $args['production_order_id']
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to update production status');
        }
        
        return ['message' => 'Production status updated successfully'];
    }
}
