<?php
// resolvers/OrderResolver.php

class OrderResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function getOrders($args, $context) {
        $this->auth->requirePermission($context, PERM_ORDER_VIEW);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        $status = $args['status'] ?? null;
        $customer_id = $args['customer_id'] ?? null;
        
        $query = "
            SELECT o.*, c.customer_name, c.phone as customer_phone,
                   u1.full_name as created_by_name,
                   u2.full_name as approved_by_name
            FROM Orders o
            JOIN Customers c ON o.customer_id = c.customer_id
            LEFT JOIN Users u1 ON o.created_by = u1.user_id
            LEFT JOIN Users u2 ON o.approved_by = u2.user_id
            WHERE 1=1
        ";
        
        $params = [];
        $types = "";
        
        if ($status) {
            $query .= " AND o.order_status = ?";
            $params[] = $status;
            $types .= "s";
        }
        
        if ($customer_id) {
            $query .= " AND o.customer_id = ?";
            $params[] = $customer_id;
            $types .= "i";
        }
        
        $query .= " ORDER BY o.order_date DESC LIMIT ? OFFSET ?";
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
            // Get order items count
            $itemQuery = "SELECT COUNT(*) as count FROM Order_Items WHERE order_id = ?";
            $itemStmt = $this->db->prepare($itemQuery);
            $itemStmt->bind_param("i", $row['order_id']);
            $itemStmt->execute();
            $row['item_count'] = $itemStmt->get_result()->fetch_assoc()['count'];
            
            $orders[] = $row;
        }
        
        // Get total count
        $countQuery = "SELECT COUNT(*) as total FROM Orders WHERE 1=1";
        $countParams = [];
        $countTypes = "";
        
        if ($status) {
            $countQuery .= " AND order_status = ?";
            $countParams[] = $status;
            $countTypes .= "s";
        }
        
        if ($customer_id) {
            $countQuery .= " AND customer_id = ?";
            $countParams[] = $customer_id;
            $countTypes .= "i";
        }
        
        $countStmt = $this->db->prepare($countQuery);
        if ($countParams) {
            $countStmt->bind_param($countTypes, ...$countParams);
        }
        $countStmt->execute();
        $total = $countStmt->get_result()->fetch_assoc()['total'];
        
        return [
            'orders' => $orders,
            'total' => $total,
            'limit' => $limit,
            'offset' => $offset
        ];
    }
    
    public function getOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_ORDER_VIEW);
        
        if (!isset($args['order_id'])) {
            throw new Exception('order_id required');
        }
        
        $query = "
            SELECT o.*, c.customer_name, c.customer_type, c.phone, c.email, c.address,
                   u1.full_name as created_by_name,
                   u2.full_name as approved_by_name
            FROM Orders o
            JOIN Customers c ON o.customer_id = c.customer_id
            LEFT JOIN Users u1 ON o.created_by = u1.user_id
            LEFT JOIN Users u2 ON o.approved_by = u2.user_id
            WHERE o.order_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['order_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('Order not found');
        }
        
        $order = $result->fetch_assoc();
        
        // Get order items
        $itemQuery = "
            SELECT oi.*, p.product_name, p.product_code,
                   pv.sku, pv.size, pv.color_name
            FROM Order_Items oi
            JOIN Products p ON oi.product_id = p.product_id
            LEFT JOIN Product_Variants pv ON oi.variant_id = pv.variant_id
            WHERE oi.order_id = ?
        ";
        
        $itemStmt = $this->db->prepare($itemQuery);
        $itemStmt->bind_param("i", $args['order_id']);
        $itemStmt->execute();
        $itemResult = $itemStmt->get_result();
        
        $order['items'] = [];
        while ($item = $itemResult->fetch_assoc()) {
            $order['items'][] = $item;
        }
        
        return $order;
    }
    
    public function getCustomers($args, $context) {
        $this->auth->requireAuth($context);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        $search = $args['search'] ?? null;
        
        $query = "
            SELECT * FROM Customers WHERE is_active = TRUE
        ";
        
        $params = [];
        $types = "";
        
        if ($search) {
            $query .= " AND (customer_name LIKE ? OR phone LIKE ? OR email LIKE ?)";
            $searchParam = "%$search%";
            $params[] = $searchParam;
            $params[] = $searchParam;
            $params[] = $searchParam;
            $types .= "sss";
        }
        
        $query .= " ORDER BY customer_name LIMIT ? OFFSET ?";
        $params[] = $limit;
        $params[] = $offset;
        $types .= "ii";
        
        $stmt = $this->db->prepare($query);
        if ($params) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        
        $customers = [];
        while ($row = $result->fetch_assoc()) {
            $customers[] = $row;
        }
        
        return ['customers' => $customers];
    }
    
    public function createOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_ORDER_CREATE);
        $user = $this->auth->requireAuth($context);
        
        if (!isset($args['customer_id']) || !isset($args['items'])) {
            throw new Exception('customer_id and items required');
        }
        
        $this->db->begin_transaction();
        
        try {
            // Calculate totals
            $totalAmount = 0;
            foreach ($args['items'] as $item) {
                $totalAmount += $item['quantity'] * $item['unit_price'];
            }
            
            $discountAmount = $args['discount_amount'] ?? 0;
            $taxAmount = $args['tax_amount'] ?? 0;
            $shippingFee = $args['shipping_fee'] ?? 0;
            $finalAmount = $totalAmount - $discountAmount + $taxAmount + $shippingFee;
            
            // Generate order code
            $orderCode = 'ORD' . date('Ymd') . sprintf('%04d', rand(1, 9999));
            
            // Insert order
            $orderQuery = "
                INSERT INTO Orders (
                    order_code, customer_id, order_type, order_status,
                    required_date, total_amount, discount_amount, tax_amount,
                    shipping_fee, final_amount, shipping_address, notes, created_by
                ) VALUES (?, ?, ?, 'Draft', ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ";
            
            $stmt = $this->db->prepare($orderQuery);
            $orderType = $args['order_type'] ?? 'Retail';
            $stmt->bind_param(
                "sissddddssi",
                $orderCode,
                $args['customer_id'],
                $orderType,
                $args['required_date'] ?? null,
                $totalAmount,
                $discountAmount,
                $taxAmount,
                $shippingFee,
                $finalAmount,
                $args['shipping_address'] ?? null,
                $args['notes'] ?? null,
                $user['user_id']
            );
            
            if (!$stmt->execute()) {
                throw new Exception('Failed to create order');
            }
            
            $orderId = $stmt->insert_id;
            
            // Insert order items
            $itemQuery = "
                INSERT INTO Order_Items (
                    order_id, product_id, variant_id, quantity,
                    unit_price, discount_percentage, discount_amount, total_price
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ";
            
            foreach ($args['items'] as $item) {
                $itemStmt = $this->db->prepare($itemQuery);
                $itemTotal = $item['quantity'] * $item['unit_price'];
                $itemDiscount = $item['discount_amount'] ?? 0;
                $itemDiscountPct = $item['discount_percentage'] ?? 0;
                
                $itemStmt->bind_param(
                    "iiiddddd",
                    $orderId,
                    $item['product_id'],
                    $item['variant_id'] ?? null,
                    $item['quantity'],
                    $item['unit_price'],
                    $itemDiscountPct,
                    $itemDiscount,
                    $itemTotal - $itemDiscount
                );
                
                if (!$itemStmt->execute()) {
                    throw new Exception('Failed to add order item');
                }
            }
            
            $this->db->commit();
            
            return [
                'order_id' => $orderId,
                'order_code' => $orderCode,
                'message' => 'Order created successfully'
            ];
            
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }
    
    public function updateOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_ORDER_EDIT);
        
        if (!isset($args['order_id'])) {
            throw new Exception('order_id required');
        }
        
        $updates = [];
        $params = [];
        $types = "";
        
        $allowedFields = [
            'order_status', 'required_date', 'shipping_date', 'delivery_date',
            'shipping_address', 'notes', 'payment_status', 'payment_method'
        ];
        
        foreach ($allowedFields as $field) {
            if (isset($args[$field])) {
                $updates[] = "$field = ?";
                $params[] = $args[$field];
                $types .= "s";
            }
        }
        
        if (empty($updates)) {
            throw new Exception('No fields to update');
        }
        
        $params[] = $args['order_id'];
        $types .= "i";
        
        $query = "UPDATE Orders SET " . implode(', ', $updates) . " WHERE order_id = ?";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param($types, ...$params);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to update order');
        }
        
        return [
            'order_id' => $args['order_id'],
            'message' => 'Order updated successfully'
        ];
    }
    
    public function approveOrder($args, $context) {
        $this->auth->requirePermission($context, PERM_ORDER_APPROVE);
        $user = $this->auth->requireAuth($context);
        
        if (!isset($args['order_id'])) {
            throw new Exception('order_id required');
        }
        
        $query = "
            UPDATE Orders 
            SET order_status = 'Approved', 
                approved_by = ?, 
                approved_date = NOW()
            WHERE order_id = ? AND order_status = 'Pending_Approval'
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ii", $user['user_id'], $args['order_id']);
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to approve order');
        }
        
        if ($stmt->affected_rows === 0) {
            throw new Exception('Order not found or already approved');
        }
        
        return [
            'order_id' => $args['order_id'],
            'message' => 'Order approved successfully'
        ];
    }
}