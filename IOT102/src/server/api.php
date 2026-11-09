<?php
/**
 * FILE: api.php
 * CHỨC NĂNG: Nhận dữ liệu từ Gateway ESP32-S3 (gồm cả Độ ẩm đất), lưu lịch sử JSON và trả lệnh điều khiển
 */
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Ho_Chi_Minh");

$stateFile = __DIR__ . "/iot_state.json";
$logFile   = __DIR__ . "/iot_history.json";

if (!file_exists($stateFile)) {
    $defaultState = [
        "updated_at" => date("Y-m-d H:i:s"),
        "node1" => [
            "temp" => 29.0, "hum" => 75.0, "pres" => 1008.2,
            "water" => 35, "soil" => 0, "rain" => 0,
            "pump1" => 0, "buzz1" => 0, "mode" => 1, "rssi" => -65, "online" => 0
        ],
        "node2" => [
            "temp" => 31.5, "hum" => 58.0, "pres" => 1008.0,
            "water" => 85, "soil" => 48, "rain" => 0,
            "pump2" => 0, "pump3" => 0, "buzz2" => 0, "mode" => 1, "rssi" => -72, "online" => 0
        ],
        "commands" => [
            "mode1" => 1, "pump1" => 0, "buzz1" => 0,
            "mode2" => 1, "pump2" => 0, "pump3" => 0, "buzz2" => 0
        ]
    ];
    file_put_contents($stateFile, json_encode($defaultState, JSON_PRETTY_PRINT));
}

$currentData = json_decode(file_get_contents($stateFile), true);
$method = $_SERVER["REQUEST_METHOD"];

if ($method === "POST") {
    $rawInput  = file_get_contents("php://input");
    $jsonInput = json_decode($rawInput, true);

    // 1. Nhận lệnh điều khiển Auto/Manual từ giao diện Web
    if (isset($jsonInput["action"]) && $jsonInput["action"] === "set_command") {
        foreach ($jsonInput["commands"] as $key => $val) {
            if (isset($currentData["commands"][$key])) {
                $currentData["commands"][$key] = intval($val);
            }
        }
        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));
        echo json_encode(["status" => "ok", "commands" => $currentData["commands"]]);
        exit;
    }

    // 2. Nhận dữ liệu cảm biến từ Gateway ESP32-S3
    if (isset($jsonInput["node1"]) && isset($jsonInput["node2"])) {
        $currentData["updated_at"] = date("Y-m-d H:i:s");
        $currentData["node1"]      = $jsonInput["node1"];
        $currentData["node2"]      = $jsonInput["node2"];

        if ($currentData["commands"]["mode1"] == 1) {
            $currentData["commands"]["pump1"] = intval($jsonInput["node1"]["pump1"]);
            $currentData["commands"]["buzz1"] = intval($jsonInput["node1"]["buzz1"]);
        }
        if ($currentData["commands"]["mode2"] == 1) {
            $currentData["commands"]["pump2"] = intval($jsonInput["node2"]["pump2"]);
            $currentData["commands"]["pump3"] = intval($jsonInput["node2"]["pump3"]);
            $currentData["commands"]["buzz2"] = intval($jsonInput["node2"]["buzz2"]);
        }

        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));

        // Lưu tối đa 30 bản ghi lịch sử gần nhất
        $history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
        if (!is_array($history)) $history = [];
        array_unshift($history, [
            "time"     => date("H:i:s"),
            "n1_water" => $jsonInput["node1"]["water"],
            "n1_temp"  => $jsonInput["node1"]["temp"],
            "n2_soil"  => isset($jsonInput["node2"]["soil"]) ? $jsonInput["node2"]["soil"] : 0,
            "n2_water" => $jsonInput["node2"]["water"],
            "n2_temp"  => $jsonInput["node2"]["temp"],
            "n2_hum"   => $jsonInput["node2"]["hum"]
        ]);
        $history = array_slice($history, 0, 30);
        file_put_contents($logFile, json_encode($history, JSON_PRETTY_PRINT));

        echo json_encode([
            "status"   => "ok",
            "commands" => $currentData["commands"]
        ]);
        exit;
    }
}

$history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
echo json_encode([
    "state"   => $currentData,
    "history" => $history
]);
?>