<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Ho_Chi_Minh");

$stateFile = __DIR__ . "/iot_state.json";
$logFile   = __DIR__ . "/iot_history.json";

// Cấu hình Telegram lấy từ Gateway của bạn
$DEFAULT_TG_TOKEN   = "8568608071:AAHDaQBp_nBVdHdrFJVl7wUahyxAO4j19yY";
$DEFAULT_TG_CHAT_ID = "1279015740";

if (!file_exists($stateFile)) {
    $defaultState = [
        "updated_at" => date("Y-m-d H:i:s"),
        "demo_lock"  => 1,
        "node1" => [
            "temp" => 29.0, "hum" => 75.0, "pres" => 1008.2,
            "water" => 35, "soil" => 0, "rain" => 0,
            "pump1" => 0, "buzz1" => 0, "mode" => 1, "rssi" => -65, "online" => 1
        ],
        "node2" => [
            "temp" => 30.0, "hum" => 60.0, "pres" => 1010.0,
            "water" => 85, "soil" => 60, "rain" => 0,
            "pump2" => 0, "pump3" => 0, "buzz2" => 0, "mode" => 1, "rssi" => -68, "online" => 1,
            "status_code" => "NORMAL"
        ],
        "commands" => [
            "mode1" => 1, "pump1" => 0, "buzz1" => 0,
            "mode2" => 1, "pump2" => 0, "pump3" => 0, "buzz2" => 0
        ]
    ];
    file_put_contents($stateFile, json_encode($defaultState, JSON_PRETTY_PRINT));
}

$currentData = json_decode(file_get_contents($stateFile), true);
if (!isset($currentData["demo_lock"])) {
    $currentData["demo_lock"] = 1;
}
if (!isset($currentData["node2"]["status_code"])) {
    $currentData["node2"]["status_code"] = "NORMAL";
}

function appendHistoryLog($logFile, $n1, $n2) {
    $history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
    if (!is_array($history)) $history = [];
    array_unshift($history, [
        "time"     => date("H:i:s"),
        "n1_water" => isset($n1["water"]) ? $n1["water"] : 35,
        "n1_temp"  => isset($n1["temp"]) ? $n1["temp"] : 29.0,
        "n2_soil"  => isset($n2["soil"]) ? $n2["soil"] : 0,
        "n2_water" => isset($n2["water"]) ? $n2["water"] : 85,
        "n2_temp"  => isset($n2["temp"]) ? $n2["temp"] : 30.0,
        "n2_hum"   => isset($n2["hum"]) ? $n2["hum"] : 60.0
    ]);
    $history = array_slice($history, 0, 30);
    file_put_contents($logFile, json_encode($history, JSON_PRETTY_PRINT));
}

function sendTelegramViaServer($botToken, $chatId, $messageText) {
    if (empty($botToken) || empty($chatId) || empty($messageText)) return false;
    $url = "https://api.telegram.org/bot" . trim($botToken) . "/sendMessage";
    $payload = json_encode([
        "chat_id" => trim($chatId),
        "text"    => $messageText
    ]);

    // Ưu tiên dùng cURL
    if (function_exists("curl_init")) {
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);
        $res  = curl_exec($ch);
        $code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);
        if ($code == 200 && $res !== false) return true;
    }

    // Dự phòng bằng file_get_contents
    $opts = [
        "http" => [
            "method"  => "POST",
            "header"  => "Content-Type: application/json\r\n",
            "content" => $payload,
            "timeout" => 5
        ],
        "ssl" => [
            "verify_peer"      => false,
            "verify_peer_name" => false
        ]
    ];
    $context = stream_context_create($opts);
    $result  = @file_get_contents($url, false, $context);
    return ($result !== false);
}

$method = $_SERVER["REQUEST_METHOD"];

if ($method === "POST") {
    $rawInput  = file_get_contents("php://input");
    $jsonInput = json_decode($rawInput, true);

    // 1. Gửi tin nhắn Telegram độc lập từ panel.html
    if (isset($jsonInput["action"]) && $jsonInput["action"] === "send_telegram") {
        $token  = !empty($jsonInput["tg_token"]) ? $jsonInput["tg_token"] : $DEFAULT_TG_TOKEN;
        $chatId = !empty($jsonInput["tg_chat_id"]) ? $jsonInput["tg_chat_id"] : $DEFAULT_TG_CHAT_ID;
        $msg    = isset($jsonInput["message"]) ? $jsonInput["message"] : "";
        $sent   = sendTelegramViaServer($token, $chatId, $msg);
        echo json_encode(["status" => "ok", "tg_sent_by_server" => $sent]);
        exit;
    }

    // 2. Nhận lệnh điều khiển Auto/Manual hoặc Bật/Tắt bơm từ index.html
    if (isset($jsonInput["action"]) && $jsonInput["action"] === "set_command") {
        foreach ($jsonInput["commands"] as $key => $val) {
            if (isset($currentData["commands"][$key])) {
                $currentData["commands"][$key] = intval($val);
            }
        }
        $currentData["node2"]["mode"]  = $currentData["commands"]["mode2"];
        $currentData["node2"]["pump2"] = $currentData["commands"]["pump2"];
        $currentData["node2"]["pump3"] = $currentData["commands"]["pump3"];
        $currentData["node2"]["buzz2"] = $currentData["commands"]["buzz2"];
        $currentData["updated_at"]     = date("Y-m-d H:i:s");

        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));
        echo json_encode(["status" => "ok", "commands" => $currentData["commands"]]);
        exit;
    }

    // 3. Nhận Kịch bản Demo từ panel.html (Cập nhật trạng thái + Gửi Telegram)
    if (isset($jsonInput["action"]) && $jsonInput["action"] === "set_demo_scenario") {
        $currentData["updated_at"] = date("Y-m-d H:i:s");

        if (isset($jsonInput["demo_lock"])) {
            $currentData["demo_lock"] = intval($jsonInput["demo_lock"]);
        }

        if (isset($jsonInput["node2"]) && is_array($jsonInput["node2"])) {
            foreach ($jsonInput["node2"] as $k => $v) {
                $currentData["node2"][$k] = $v;
            }
        }

        $currentData["commands"]["mode2"] = intval($currentData["node2"]["mode"]);
        $currentData["commands"]["pump2"] = intval($currentData["node2"]["pump2"]);
        $currentData["commands"]["pump3"] = intval($currentData["node2"]["pump3"]);
        $currentData["commands"]["buzz2"] = intval($currentData["node2"]["buzz2"]);

        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));

        if (!isset($jsonInput["skip_history"]) || !$jsonInput["skip_history"]) {
            appendHistoryLog($logFile, $currentData["node1"], $currentData["node2"]);
        }

        $tgSent = false;
        if (!empty($jsonInput["tg_message"])) {
            $token  = !empty($jsonInput["tg_token"]) ? $jsonInput["tg_token"] : $DEFAULT_TG_TOKEN;
            $chatId = !empty($jsonInput["tg_chat_id"]) ? $jsonInput["tg_chat_id"] : $DEFAULT_TG_CHAT_ID;
            $tgSent = sendTelegramViaServer($token, $chatId, $jsonInput["tg_message"]);
        }

        echo json_encode([
            "status"            => "ok",
            "state"             => $currentData,
            "commands"          => $currentData["commands"],
            "tg_sent_by_server" => $tgSent
        ]);
        exit;
    }

    // 4. Nhận dữ liệu từ Gateway (nếu có)
    if (isset($jsonInput["node2"])) {
        if (empty($currentData["demo_lock"]) || intval($currentData["demo_lock"]) === 0) {
            $currentData["updated_at"] = date("Y-m-d H:i:s");
            $currentData["node2"]      = array_merge($currentData["node2"], $jsonInput["node2"]);

            if ($currentData["commands"]["mode2"] == 1) {
                $currentData["commands"]["pump2"] = intval($currentData["node2"]["pump2"]);
                $currentData["commands"]["pump3"] = intval($currentData["node2"]["pump3"]);
                $currentData["commands"]["buzz2"] = intval($currentData["node2"]["buzz2"]);
            }

            file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));
            appendHistoryLog($logFile, $currentData["node1"], $currentData["node2"]);
        }

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