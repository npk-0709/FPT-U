<?php

// config/config.php

define('JWT_SECRET', 'doanhnhanfu01');
define('JWT_ALGORITHM', 'HS256');
define('JWT_EXPIRY', 86400); // 24 hours

define('PAGINATION_DEFAULT_LIMIT', 20);
define('PAGINATION_MAX_LIMIT', 100);

// Permission codes
define('PERM_USER_VIEW', 'user.view');
define('PERM_USER_CREATE', 'user.create');
define('PERM_USER_EDIT', 'user.edit');
define('PERM_USER_DELETE', 'user.delete');

define('PERM_PRODUCT_VIEW', 'product.view');
define('PERM_PRODUCT_CREATE', 'product.create');
define('PERM_PRODUCT_EDIT', 'product.edit');
define('PERM_PRODUCT_DELETE', 'product.delete');

define('PERM_ORDER_VIEW', 'order.view');
define('PERM_ORDER_CREATE', 'order.create');
define('PERM_ORDER_EDIT', 'order.edit');
define('PERM_ORDER_APPROVE', 'order.approve');

define('PERM_PRODUCTION_VIEW', 'production.view');
define('PERM_PRODUCTION_CREATE', 'production.create');
define('PERM_PRODUCTION_EDIT', 'production.edit');

define('PERM_WAREHOUSE_VIEW', 'warehouse.view');
define('PERM_WAREHOUSE_MANAGE', 'warehouse.manage');

define('PERM_REPORT_VIEW', 'report.view');
define('PERM_REPORT_GENERATE', 'report.generate');