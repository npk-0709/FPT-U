<?php
// core/GraphQL.php

class GraphQL
{
    private $db;
    private $schema;
    private $resolvers = [];

    public function __construct($db)
    {
        $this->db = $db;
        $this->initializeResolvers();
    }

    private function initializeResolvers()
    {
        $this->resolvers = [
            'Query' => [
                'login' => [new UserResolver($this->db), 'login'],
                'me' => [new UserResolver($this->db), 'me'],
                'users' => [new UserResolver($this->db), 'getUsers'],
                'user' => [new UserResolver($this->db), 'getUser'],

                'products' => [new ProductResolver($this->db), 'getProducts'],
                'product' => [new ProductResolver($this->db), 'getProduct'],
                'productVariants' => [new ProductResolver($this->db), 'getProductVariants'],

                'orders' => [new OrderResolver($this->db), 'getOrders'],
                'order' => [new OrderResolver($this->db), 'getOrder'],
                'customers' => [new OrderResolver($this->db), 'getCustomers'],

                'materials' => [new MaterialResolver($this->db), 'getMaterials'],
                'material' => [new MaterialResolver($this->db), 'getMaterial'],
                'suppliers' => [new MaterialResolver($this->db), 'getSuppliers'],

                'productionOrders' => [new ProductionResolver($this->db), 'getProductionOrders'],
                'productionOrder' => [new ProductionResolver($this->db), 'getProductionOrder'],

                'warehouses' => [new WarehouseResolver($this->db), 'getWarehouses'],
                'materialStock' => [new WarehouseResolver($this->db), 'getMaterialStock'],
                'productStock' => [new WarehouseResolver($this->db), 'getProductStock'],
            ],
            'Mutation' => [
                'createUser' => [new UserResolver($this->db), 'createUser'],
                'updateUser' => [new UserResolver($this->db), 'updateUser'],
                'deleteUser' => [new UserResolver($this->db), 'deleteUser'],

                'createProduct' => [new ProductResolver($this->db), 'createProduct'],
                'updateProduct' => [new ProductResolver($this->db), 'updateProduct'],
                'deleteProduct' => [new ProductResolver($this->db), 'deleteProduct'],

                'createOrder' => [new OrderResolver($this->db), 'createOrder'],
                'updateOrder' => [new OrderResolver($this->db), 'updateOrder'],
                'approveOrder' => [new OrderResolver($this->db), 'approveOrder'],

                'createMaterial' => [new MaterialResolver($this->db), 'createMaterial'],
                'updateMaterial' => [new MaterialResolver($this->db), 'updateMaterial'],

                'createProductionOrder' => [new ProductionResolver($this->db), 'createProductionOrder'],
                'updateProductionStatus' => [new ProductionResolver($this->db), 'updateProductionStatus'],

                'createMaterialTransaction' => [new WarehouseResolver($this->db), 'createMaterialTransaction'],
                'createProductTransaction' => [new WarehouseResolver($this->db), 'createProductTransaction'],
            ]
        ];
    }

    public function execute($query, $variables = [], $context = [], $operationName = null)
    {
        try {
            $parsed = $this->parseQuery($query);
            $operation = $this->getOperation($parsed, $operationName);

            if ($operation['type'] === 'query') {
                $result = $this->executeQuery($operation, $variables, $context);
            } elseif ($operation['type'] === 'mutation') {
                $result = $this->executeMutation($operation, $variables, $context);
            } else {
                throw new Exception('Unsupported operation type');
            }

            return ['data' => $result];
        } catch (Exception $e) {
            return [
                'errors' => [
                    [
                        'message' => $e->getMessage(),
                        'path' => []
                    ]
                ]
            ];
        }
    }

    private function parseQuery($query)
    {
        $query = trim($query);

        // Simple parser - extract operation type and fields
        preg_match('/^(query|mutation)\s*(\w*)\s*(\([^)]*\))?\s*{(.+)}$/s', $query, $matches);

        if (!$matches) {
            throw new Exception('Invalid GraphQL query format');
        }

        return [
            'type' => $matches[1],
            'name' => $matches[2] ?: null,
            'variables' => $matches[3] ?? '',
            'body' => trim($matches[4])
        ];
    }

    private function getOperation($parsed, $operationName)
    {
        return [
            'type' => $parsed['type'],
            'name' => $parsed['name'],
            'fields' => $this->parseFields($parsed['body'])
        ];
    }

    private function parseFields($body)
    {
        $fields = [];
        $body = trim($body);

        // Split by top-level fields
        preg_match_all('/(\w+)\s*(\([^)]*\))?\s*({[^}]+})?/', $body, $matches, PREG_SET_ORDER);

        foreach ($matches as $match) {
            $fieldName = $match[1];
            $args = isset($match[2]) ? $this->parseArguments($match[2]) : [];
            $subFields = isset($match[3]) ? $this->parseSubFields($match[3]) : [];

            $fields[$fieldName] = [
                'args' => $args,
                'fields' => $subFields
            ];
        }

        return $fields;
    }

    private function parseArguments($argsString)
    {
        $args = [];
        $argsString = trim($argsString, '()');

        if (empty($argsString)) {
            return $args;
        }

        preg_match_all('/(\w+):\s*(\$?\w+|"[^"]*"|\d+|true|false|null)/', $argsString, $matches, PREG_SET_ORDER);

        foreach ($matches as $match) {
            $key = $match[1];
            $value = $match[2];

            // Handle variable references
            if (substr($value, 0, 1) === '$') {
                $args[$key] = ['variable' => substr($value, 1)];
            } else {
                $args[$key] = $this->parseValue($value);
            }
        }

        return $args;
    }

    private function parseSubFields($fieldsString)
    {
        $fieldsString = trim($fieldsString, '{}');
        $fields = array_filter(array_map('trim', explode("\n", $fieldsString)));
        return $fields;
    }

    private function parseValue($value)
    {
        $value = trim($value);

        if ($value === 'null') return null;
        if ($value === 'true') return true;
        if ($value === 'false') return false;
        if (is_numeric($value)) return $value + 0;
        if (preg_match('/^"(.*)"$/', $value, $m)) return $m[1];

        return $value;
    }

    private function executeQuery($operation, $variables, $context)
    {
        return $this->resolveFields('Query', $operation['fields'], $variables, $context);
    }

    private function executeMutation($operation, $variables, $context)
    {
        return $this->resolveFields('Mutation', $operation['fields'], $variables, $context);
    }

    private function resolveFields($type, $fields, $variables, $context)
    {
        $result = [];

        foreach ($fields as $fieldName => $fieldData) {
            if (!isset($this->resolvers[$type][$fieldName])) {
                throw new Exception("Unknown field: $fieldName");
            }

            $resolver = $this->resolvers[$type][$fieldName];
            $args = $this->resolveArguments($fieldData['args'], $variables);

            $result[$fieldName] = call_user_func($resolver, $args, $context);
        }

        return $result;
    }

    private function resolveArguments($args, $variables)
    {
        $resolved = [];

        foreach ($args as $key => $value) {
            if (is_array($value) && isset($value['variable'])) {
                $varName = $value['variable'];
                if (!isset($variables[$varName])) {
                    throw new Exception("Variable \$$varName not provided");
                }
                $resolved[$key] = $variables[$varName];
            } else {
                $resolved[$key] = $value;
            }
        }

        return $resolved;
    }
}
