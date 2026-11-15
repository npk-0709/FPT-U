<?php
$configFile = __DIR__ . "/iot_config.json";

// Initial default parameters
$defaultConfig = [
    "TANK_EMPTY_LOCK_PCT" => 20,
    "SOIL_START_IRRIG" => 55,
    "SOIL_STOP_IRRIG" => 70,
    "TEMP_START_MIST" => 33.0,
    "TEMP_STOP_MIST" => 30.0,
    "PUMP_STALL_TIMEOUT" => 180000
];

if (!file_exists($configFile)) {
    file_put_contents($configFile, json_encode($defaultConfig, JSON_PRETTY_PRINT));
}

$message = "";
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $newConfig = [
        "TANK_EMPTY_LOCK_PCT" => intval($_POST["TANK_EMPTY_LOCK_PCT"]),
        "SOIL_START_IRRIG"    => intval($_POST["SOIL_START_IRRIG"]),
        "SOIL_STOP_IRRIG"     => intval($_POST["SOIL_STOP_IRRIG"]),
        "TEMP_START_MIST"     => floatval($_POST["TEMP_START_MIST"]),
        "TEMP_STOP_MIST"      => floatval($_POST["TEMP_STOP_MIST"]),
        "PUMP_STALL_TIMEOUT"  => intval($_POST["PUMP_STALL_TIMEOUT"])
    ];
    file_put_contents($configFile, json_encode($newConfig, JSON_PRETTY_PRINT));
    $message = "Configuration saved successfully!";
}

$config = json_decode(file_get_contents($configFile), true);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alert Threshold Configuration</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = { darkMode: 'class' }
    </script>
</head>
<body class="bg-slate-50 dark:bg-slate-900 text-slate-900 dark:text-slate-100 min-h-screen p-4 md:p-8 transition-colors duration-300">
    <div class="max-w-3xl mx-auto">
        <!-- Header -->
        <div class="flex justify-between items-center bg-white dark:bg-slate-800 p-6 rounded-2xl border border-slate-200 dark:border-slate-700 mb-6 shadow-sm">
            <div>
                <h1 class="text-2xl font-bold text-amber-600 dark:text-amber-400">SYSTEM CONFIGURATION</h1>
                <p class="text-slate-500 dark:text-slate-400 text-sm mt-1">Automatic threshold & alert settings</p>
            </div>
            <div class="flex gap-3">
                <a href="/" class="px-4 py-2 bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 rounded-lg text-sm font-medium hover:bg-slate-200 dark:hover:bg-slate-600 transition-colors border border-slate-200 dark:border-slate-600">Return to Panel</a>
                <button onclick="toggleDarkMode()" class="p-2 bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-600 transition-colors border border-slate-200 dark:border-slate-600">
                    <svg class="w-5 h-5 hidden dark:block" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                    <svg class="w-5 h-5 block dark:hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"></path></svg>
                </button>
            </div>
        </div>

        <?php if ($message): ?>
            <div class="bg-emerald-100 dark:bg-emerald-900/30 border border-emerald-400 text-emerald-700 dark:text-emerald-400 px-4 py-3 rounded-xl mb-6 font-medium">
                ✅ <?= $message ?>
            </div>
        <?php endif; ?>

        <!-- Configuration Form -->
        <form method="POST" class="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                <!-- Water & Pump Stall -->
                <div class="space-y-4">
                    <h2 class="font-bold text-lg text-emerald-600 dark:text-emerald-400 border-b border-slate-200 dark:border-slate-700 pb-2">Pump Protection</h2>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Tank empty threshold (%)</label>
                        <input type="number" name="TANK_EMPTY_LOCK_PCT" value="<?= $config['TANK_EMPTY_LOCK_PCT'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-emerald-500 outline-none">
                        <p class="text-xs text-slate-500 mt-1">Lock pump if water is below this level</p>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Pump stall timeout (ms)</label>
                        <input type="number" name="PUMP_STALL_TIMEOUT" value="<?= $config['PUMP_STALL_TIMEOUT'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-emerald-500 outline-none">
                        <p class="text-xs text-slate-500 mt-1">Default: 180000 (3 minutes)</p>
                    </div>
                </div>

                <!-- Pump 1: Base Irrigation -->
                <div class="space-y-4">
                    <h2 class="font-bold text-lg text-sky-600 dark:text-sky-400 border-b border-slate-200 dark:border-slate-700 pb-2">Base Irrigation (Pump 1)</h2>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Soil moisture to start irrigation (%)</label>
                        <input type="number" name="SOIL_START_IRRIG" value="<?= $config['SOIL_START_IRRIG'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-sky-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Soil moisture to stop irrigation (%)</label>
                        <input type="number" name="SOIL_STOP_IRRIG" value="<?= $config['SOIL_STOP_IRRIG'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-sky-500 outline-none">
                    </div>
                </div>

                <!-- Pump 2: Misting -->
                <div class="space-y-4">
                    <h2 class="font-bold text-lg text-amber-500 dark:text-amber-400 border-b border-slate-200 dark:border-slate-700 pb-2">Misting (Pump 2)</h2>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Temperature to turn ON misting (°C)</label>
                        <input type="number" step="0.1" name="TEMP_START_MIST" value="<?= $config['TEMP_START_MIST'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-amber-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Temperature to turn OFF misting (°C)</label>
                        <input type="number" step="0.1" name="TEMP_STOP_MIST" value="<?= $config['TEMP_STOP_MIST'] ?>" class="w-full bg-slate-50 dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded-lg p-2.5 text-sm focus:ring-2 focus:ring-amber-500 outline-none">
                    </div>
                </div>
            </div>
            <div class="pt-4 border-t border-slate-200 dark:border-slate-700 text-right">
                <button type="submit" class="px-6 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white font-bold rounded-lg shadow-md transition-colors">SAVE CONFIGURATION</button>
            </div>
        </form>
    </div>

    <script>
        function toggleDarkMode() {
            document.documentElement.classList.toggle('dark');
        }
        // Add a button to go to config in the index.html page:
        // You can insert <a href="config.php" class="...">Settings</a> into the HTML of the index page
    </script>
</body>
</html>