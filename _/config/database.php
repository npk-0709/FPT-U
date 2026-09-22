<?php

// config/database.php

class Database
{
    private $host = "localhost";
    private $db_name = "quanlymaymac";
    private $username = "quanlymaymac";
    private $password = "quanlymaymac";
    private $charset = "utf8mb4";
    public $conn;

    public function getConnection()
    {
        $this->conn = null;

        try {
            $this->conn = new mysqli(
                $this->host,
                $this->username,
                $this->password,
                $this->db_name
            );

            if ($this->conn->connect_error) {
                throw new Exception("Connection failed: " . $this->conn->connect_error);
            }

            $this->conn->set_charset($this->charset);
        } catch (Exception $e) {
            throw new Exception("Database connection error: " . $e->getMessage());
        }

        return $this->conn;
    }
}
