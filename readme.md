# GraphQL API Documentation - Hệ thống Quản lý Sản xuất Thời trang

## Cấu trúc thư mục

```
/api
├── index.php                 # Entry point
├── config/
│   ├── database.php         # Database connection
│   └── config.php           # Configuration constants
├── core/
│   ├── GraphQL.php          # GraphQL engine
│   └── Auth.php             # Authentication
├── utils/
│   └── JWTHandler.php       # JWT utilities
└── resolvers/
    ├── UserResolver.php
    ├── ProductResolver.php
    ├── OrderResolver.php
    ├── MaterialResolver.php
    ├── ProductionResolver.php
    └── WarehouseResolver.php
```

## Cài đặt

1. **Cấu hình database** trong `config/database.php`
2. **Tạo database** từ file SQL đã cung cấp
3. **Thay đổi JWT_SECRET** trong `config/config.php`
4. **Tạo dữ liệu mẫu** (roles, permissions, user admin)

```sql
-- Tạo roles
INSERT INTO Roles (role_name, description) VALUES
('Admin', 'Quản trị viên hệ thống'),
('Manager', 'Quản lý sản xuất'),
('Staff', 'Nhân viên'),
('Viewer', 'Chỉ xem');

-- Tạo permissions
INSERT INTO Permissions (permission_name, permission_code) VALUES
('View Users', 'user.view'),
('Create Users', 'user.create'),
('Edit Users', 'user.edit'),
('Delete Users', 'user.delete'),
('View Products', 'product.view'),
('Create Products', 'product.create'),
('Edit Products', 'product.edit'),
('Delete Products', 'product.delete'),
('View Orders', 'order.view'),
('Create Orders', 'order.create'),
('Edit Orders', 'order.edit'),
('Approve Orders', 'order.approve'),
('View Production', 'production.view'),
('Create Production', 'production.create'),
('Edit Production', 'production.edit'),
('View Warehouse', 'warehouse.view'),
('Manage Warehouse', 'warehouse.manage');

-- Gán quyền cho Admin (role_id = 1)
INSERT INTO Role_Permissions (role_id, permission_id)
SELECT 1, permission_id FROM Permissions;

-- Tạo user admin
INSERT INTO Users (username, password_hash, full_name, email, role_id) VALUES
('admin', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Administrator', 'admin@example.com', 1);
-- Password: password
```

---

## Test Queries

### 1. Authentication

#### Login
```graphql
mutation {
  login(username: "admin", password: "password") {
    token
    user {
      user_id
      username
      full_name
      email
      role_name
    }
  }
}
```

#### Get Current User
```graphql
query {
  me {
    user_id
    username
    full_name
    email
    phone
    role_name
    last_login
  }
}
```
**Headers:** `Authorization: Bearer YOUR_TOKEN`

---

### 2. User Management

#### Get All Users
```graphql
query {
  users(limit: 10, offset: 0, search: "admin") {
    users {
      user_id
      username
      full_name
      email
      role_name
      is_active
      created_at
    }
    total
    limit
    offset
  }
}
```

#### Get User by ID
```graphql
query {
  user(user_id: 1) {
    user_id
    username
    full_name
    email
    phone
    role_name
    role_description
    is_active
  }
}
```

#### Create User
```graphql
mutation {
  createUser(
    username: "nguyen_van_a"
    password: "Password123!"
    full_name: "Nguyễn Văn A"
    email: "nguyenvana@example.com"
    phone: "0901234567"
    role_id: 2
  ) {
    user_id
    message
  }
}
```

#### Update User
```graphql
mutation {
  updateUser(
    user_id: 2
    full_name: "Nguyễn Văn B"
    email: "nguyenvanb@example.com"
    phone: "0909876543"
  ) {
    user_id
    message
  }
}
```

---

### 3. Product Management

#### Get All Products
```graphql
query {
  products(limit: 20, offset: 0, search: "áo") {
    products {
      product_id
      product_code
      product_name
      category_name
      base_price
      gender
      season
      brand
      is_active
      variant_count
    }
    total
  }
}
```

#### Get Product Detail
```graphql
query {
  product(product_id: 1) {
    product_id
    product_code
    product_name
    description
    base_price
    gender
    season
    brand
    category_name
    variants {
      variant_id
      sku
      size
      color_name
      price
      stock_quantity
    }
    images {
      image_id
      image_url
      image_type
    }
  }
}
```

#### Create Product
```graphql
mutation {
  createProduct(
    product_code: "AO-001"
    product_name: "Áo Sơ Mi Nam Trắng"
    category_id: 1
    description: "Áo sơ mi nam cao cấp"
    base_price: 350000
    gender: "Nam"
    season: "Quanh Năm"
    brand: "Fashion House"
    variants: [
      {
        sku: "AO-001-M-WHITE"
        size: "M"
        color_name: "Trắng"
        color_code: "#FFFFFF"
        price: 350000
        stock_quantity: 100
      },
      {
        sku: "AO-001-L-WHITE"
        size: "L"
        color_name: "Trắng"
        color_code: "#FFFFFF"
        price: 350000
        stock_quantity: 100
      }
    ]
  ) {
    product_id
    message
  }
}
```

#### Get Product Variants
```graphql
query {
  productVariants(product_id: 1) {
    variant_id
    sku
    size
    color_name
    price
    stock_quantity
    product_name
  }
}
```

---

### 4. Order Management

#### Get All Orders
```graphql
query {
  orders(limit: 10, offset: 0, status: "Approved") {
    orders {
      order_id
      order_code
      customer_name
      order_type
      order_status
      order_date
      required_date
      final_amount
      payment_status
      item_count
    }
    total
  }
}
```

#### Get Order Detail
```graphql
query {
  order(order_id: 1) {
    order_id
    order_code
    customer_name
    customer_phone
    order_status
    order_date
    required_date
    total_amount
    discount_amount
    tax_amount
    shipping_fee
    final_amount
    payment_status
    shipping_address
    items {
      order_item_id
      product_name
      sku
      size
      color_name
      quantity
      unit_price
      total_price
    }
  }
}
```

#### Create Order
```graphql
mutation {
  createOrder(
    customer_id: 1
    order_type: "Retail"
    required_date: "2025-01-15"
    shipping_address: "123 Đường ABC, Q1, TP.HCM"
    discount_amount: 50000
    shipping_fee: 30000
    items: [
      {
        product_id: 1
        variant_id: 1
        quantity: 2
        unit_price: 350000
      },
      {
        product_id: 2
        variant_id: 3
        quantity: 1
        unit_price: 450000
      }
    ]
  ) {
    order_id
    order_code
    message
  }
}
```

#### Approve Order
```graphql
mutation {
  approveOrder(order_id: 1) {
    order_id
    message
  }
}
```

#### Get Customers
```graphql
query {
  customers(limit: 20, search: "nguyen") {
    customers {
      customer_id
      customer_name
      customer_type
      phone
      email
      address
    }
  }
}
```

---

### 5. Material Management

#### Get Materials
```graphql
query {
  materials(limit: 50) {
    materials {
      material_id
      material_code
      material_name
      category_name
      unit
      color
      unit_price
      current_stock
    }
  }
}
```

#### Get Material Detail
```graphql
query {
  material(material_id: 1) {
    material_id
    material_code
    material_name
    category_name
    unit
    color
    unit_price
    min_stock_level
    max_stock_level
    current_stock
  }
}
```

#### Create Material
```graphql
mutation {
  createMaterial(
    material_code: "VAI-001"
    material_name: "Vải Cotton 100%"
    category_id: 1
    unit: "mét"
    color: "Trắng"
    unit_price: 85000
    current_stock: 500
  ) {
    material_id
    message
  }
}
```

---

### 6. Production Management

#### Get Production Orders
```graphql
query {
  productionOrders(limit: 20, status: "In_Progress") {
    production_orders {
      production_order_id
      production_code
      product_name
      sku
      size
      color_name
      quantity_planned
      quantity_produced
      production_status
      priority
      planned_start_date
      planned_end_date
    }
  }
}
```

#### Get Production Order Detail
```graphql
query {
  productionOrder(production_order_id: 1) {
    production_order_id
    production_code
    product_name
    quantity_planned
    quantity_produced
    quantity_defective
    production_status
    stages {
      progress_id
      stage_name
      status
      quantity_processed
      start_time
      end_time
      assigned_to_name
    }
  }
}
```

#### Create Production Order
```graphql
mutation {
  createProductionOrder(
    order_id: 1
    product_id: 1
    variant_id: 1
    quantity_planned: 100
    priority: "High"
    planned_start_date: "2025-01-10"
    planned_end_date: "2025-01-20"
    notes: "Ưu tiên sản xuất"
  ) {
    production_order_id
    production_code
    message
  }
}
```

---

### 7. Warehouse Management

#### Get Warehouses
```graphql
query {
  warehouses {
    warehouses {
      warehouse_id
      warehouse_code
      warehouse_name
      warehouse_type
      address
      manager_name
    }
  }
}
```

#### Get Material Stock
```graphql
query {
  materialStock(warehouse_id: 1) {
    material_stock {
      stock_id
      material_name
      material_code
      unit
      quantity
      reserved_quantity
      available_quantity
    }
  }
}
```

#### Get Product Stock
```graphql
query {
  productStock(warehouse_id: 1) {
    product_stock {
      stock_id
      product_name
      sku
      size
      color_name
      quantity
      reserved_quantity
      available_quantity
    }
  }
}
```

#### Create Material Transaction (Nhập kho)
```graphql
mutation {
  createMaterialTransaction(
    warehouse_id: 1
    material_id: 1
    transaction_type: "IN"
    quantity: 100
    unit_price: 85000
    reference_type: "Purchase_Order"
    reference_id: 1
    notes: "Nhập từ nhà cung cấp ABC"
  ) {
    message
  }
}
```

#### Create Product Transaction (Xuất kho)
```graphql
mutation {
  createProductTransaction(
    warehouse_id: 1
    product_id: 1
    variant_id: 1
    transaction_type: "OUT"
    quantity: 10
    reference_type: "Sales_Order"
    reference_id: 1
    notes: "Xuất hàng cho đơn #ORD-001"
  ) {
    message
  }
}
```

---

## Error Handling

API trả về lỗi theo format:

```json
{
  "errors": [
    {
      "message": "Authentication required",
      "path": []
    }
  ]
}
```

### Common Errors:
- `Authentication required` - Cần đăng nhập
- `Permission denied: xxx required` - Không có quyền
- `Invalid credentials` - Sai username/password
- `Token has expired` - Token hết hạn
- `Invalid token format` - Token không hợp lệ

---

## Testing với Postman/Insomnia

**Endpoint:** `http://localhost/api/index.php`

**Method:** POST

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

**Body (raw JSON):**
```json
{
  "query": "query { me { user_id username full_name } }",
  "variables": {}
}
```

---

## Security Best Practices

1. **Luôn dùng HTTPS** trong production
2. **Thay đổi JWT_SECRET** thành chuỗi phức tạp
3. **Implement rate limiting** để chống spam
4. **Validate input** ở tất cả mutations
5. **Log tất cả actions** vào System_Logs
6. **Backup database** thường xuyên
7. **Update dependencies** định kỳ

---

## Performance Tips

1. **Index database** cho các trường thường query
2. **Cache JWT validation** results
3. **Implement DataLoader** pattern để tránh N+1 queries
4. **Pagination** cho tất cả danh sách lớn
5. **Lazy load** relationships khi cần