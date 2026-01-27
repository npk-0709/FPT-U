<?php
// index.php - GraphQL API Entry Point

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'config/database.php';
require_once 'config/config.php';
require_once 'core/GraphQL.php';
require_once 'core/Auth.php';
require_once 'utils/JWTHandler.php';

// Include all resolvers
require_once 'resolvers/UserResolver.php';
require_once 'resolvers/ProductResolver.php';
require_once 'resolvers/OrderResolver.php';
require_once 'resolvers/MaterialResolver.php';
require_once 'resolvers/ProductionResolver.php';
require_once 'resolvers/WarehouseResolver.php';

$database = new Database();
$db = $database->getConnection();

$graphql = new GraphQL($db);

try {
    $rawInput = file_get_contents('php://input');
    $input = json_decode($rawInput, true);

    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception('Invalid JSON in request body');
    }

    $query = $input['query'] ?? '';
    $variables = $input['variables'] ?? [];
    $operationName = $input['operationName'] ?? null;

    // Get auth token
    $headers = getallheaders();
    $token = null;
    if (isset($headers['Authorization'])) {
        $token = str_replace('Bearer ', '', $headers['Authorization']);
    }

    $context = [
        'db' => $db,
        'token' => $token,
        'user' => null
    ];

    // Validate token if present
    if ($token) {
        try {
            $decoded = JWTHandler::decode($token);
            $context['user'] = $decoded;
        } catch (Exception $e) {
            // Token invalid, continue without user
        }
    }

    $result = $graphql->execute($query, $variables, $context, $operationName);

    echo json_encode($result, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'errors' => [
            [
                'message' => $e->getMessage(),
                'locations' => [],
                'path' => []
            ]
        ]
    ], JSON_UNESCAPED_UNICODE);
}
