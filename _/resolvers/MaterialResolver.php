<?php
// resolvers/MaterialResolver.php

class MaterialResolver {
    private $db;
    private $auth;
    
    public function __construct($db) {
        $this->db = $db;
        $this->auth = new Auth($db);
    }
    
    public function getMaterials($args, $context) {
        $this->auth->requireAuth($context);
        
        $limit = $args['limit'] ?? PAGINATION_DEFAULT_LIMIT;
        $offset = $args['offset'] ?? 0;
        
        $query = "
            SELECT m.*, mc.category_name
            FROM Materials m
            LEFT JOIN Material_Categories mc ON m.category_id = mc.category_id
            WHERE m.is_active = TRUE
            ORDER BY m.material_name
            LIMIT ? OFFSET ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ii", $limit, $offset);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $materials = [];
        while ($row = $result->fetch_assoc()) {
            $materials[] = $row;
        }
        
        return ['materials' => $materials];
    }
    
    public function getMaterial($args, $context) {
        $this->auth->requireAuth($context);
        
        $query = "
            SELECT m.*, mc.category_name
            FROM Materials m
            LEFT JOIN Material_Categories mc ON m.category_id = mc.category_id
            WHERE m.material_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $args['material_id']);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception('Material not found');
        }
        
        return $result->fetch_assoc();
    }
    
    public function getSuppliers($args, $context) {
        $this->auth->requireAuth($context);
        
        $query = "SELECT * FROM Suppliers WHERE is_active = TRUE ORDER BY supplier_name";
        $result = $this->db->query($query);
        
        $suppliers = [];
        while ($row = $result->fetch_assoc()) {
            $suppliers[] = $row;
        }
        
        return ['suppliers' => $suppliers];
    }
    
    public function createMaterial($args, $context) {
        $this->auth->requireAuth($context);
        
        $query = "
            INSERT INTO Materials (material_code, material_name, category_id, unit, 
                                  color, unit_price, current_stock)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "ssissdi",
            $args['material_code'],
            $args['material_name'],
            $args['category_id'],
            $args['unit'],
            $args['color'] ?? null,
            $args['unit_price'] ?? 0,
            $args['current_stock'] ?? 0
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to create material');
        }
        
        return [
            'material_id' => $stmt->insert_id,
            'message' => 'Material created successfully'
        ];
    }
    
    public function updateMaterial($args, $context) {
        $this->auth->requireAuth($context);
        
        $query = "
            UPDATE Materials 
            SET material_name = ?, unit_price = ?, current_stock = ?
            WHERE material_id = ?
        ";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(
            "sdii",
            $args['material_name'],
            $args['unit_price'],
            $args['current_stock'],
            $args['material_id']
        );
        
        if (!$stmt->execute()) {
            throw new Exception('Failed to update material');
        }
        
        return ['message' => 'Material updated successfully'];
    }
}
