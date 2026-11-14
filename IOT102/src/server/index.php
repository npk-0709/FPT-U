<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Weather Station</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        // Cấu hình Tailwind CSS sử dụng class cho dark mode
        tailwind.config = {
            darkMode: 'class',
        }
    </script>
</head>
<body class="bg-slate-50 dark:bg-slate-900 text-slate-900 dark:text-slate-100 min-h-screen p-4 md:p-8 transition-colors duration-300">
    <div class="max-w-5xl mx-auto">
        <!-- Header -->
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center bg-white dark:bg-slate-800 p-6 rounded-2xl border border-slate-200 dark:border-slate-700 mb-6 shadow-sm dark:shadow-lg transition-colors duration-300">
            <div>
                <h1 class="text-2xl md:text-3xl font-bold text-emerald-600 dark:text-emerald-400">TRUNG TÂM ĐIỀU HÀNH WEATHER STATION</h1>
                <p class="text-slate-500 dark:text-slate-400 text-sm mt-1">Gateway ESP32-S3 &bull; Trạm Node ESP8266 Năng Lượng Mặt Trời &bull; Hệ Thống Quan Trắc Thời Tiết</p>
            </div>
            <div class="mt-4 md:mt-0 flex flex-col md:items-end gap-3 w-full md:w-auto">
                <!-- Nút chuyển đổi Dark Mode -->
                <button onclick="toggleDarkMode()" class="flex items-center gap-2 px-3 py-1.5 bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 rounded-lg text-sm font-medium hover:bg-slate-200 dark:hover:bg-slate-600 transition-colors self-start md:self-end border border-slate-200 dark:border-slate-600">
                    <svg id="theme-icon-light" class="w-4 h-4 hidden dark:block" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                    <svg id="theme-icon-dark" class="w-4 h-4 block dark:hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"></path></svg>
                    <span id="theme-text">Chế độ tối</span>
                </button>
                
                <div class="text-right flex items-center md:block gap-2">
                    <span class="text-xs text-slate-500 dark:text-slate-400 block">Đồng bộ lần cuối:</span>
                    <span id="last-update" class="text-sm font-mono font-semibold text-amber-600 dark:text-amber-400">Đang tải...</span>
                </div>
            </div>
        </div>

        <!-- TRẠM NODE: WEATHER STATION -->
        <div class="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm dark:shadow-lg mb-8 transition-colors duration-300">
            <div class="flex justify-between items-center border-b border-slate-200 dark:border-slate-700 pb-4 mb-5">
                <div>
                    <h2 class="text-xl font-bold text-emerald-600 dark:text-emerald-400">TRẠM NODE: WEATHER STATION</h2>
                    <div class="flex items-center gap-2 mt-0.5">
                        <p class="text-xs text-slate-500 dark:text-slate-400" id="n2-rssi">Sóng LoRa: -- dBm</p>
                        <span class="text-slate-300 dark:text-slate-600">&bull;</span>
                        <span id="n2-sys-code" class="text-[11px] font-mono font-bold text-amber-600 dark:text-amber-400">STATUS: NORMAL</span>
                    </div>
                </div>
                <span id="n2-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300">KIỂM TRA...</span>
            </div>

            <!-- Thông số cảm biến Node -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
                <div class="bg-slate-50 dark:bg-slate-900/70 p-4 rounded-xl border border-emerald-200 dark:border-emerald-500/40 sm:col-span-3 transition-colors duration-300">
                    <div class="flex justify-between items-center">
                        <span class="text-xs md:text-sm text-slate-700 dark:text-slate-300 font-semibold">🌱 Độ ẩm đất canh tác </span>
                        <span id="n2-soil" class="text-2xl font-bold text-emerald-600 dark:text-emerald-400">--%</span>
                    </div>
                    <div class="w-full bg-slate-200 dark:bg-slate-700 h-3 rounded-full mt-2.5 overflow-hidden">
                        <div id="n2-soil-bar" class="bg-emerald-500 h-full rounded-full transition-all duration-300" style="width: 0%"></div>
                    </div>
                </div>

                <div class="bg-slate-50 dark:bg-slate-900/70 p-4 rounded-xl border border-slate-200 dark:border-slate-700/60 transition-colors duration-300">
                    <div class="text-xs text-slate-500 dark:text-slate-400 font-medium">Mực nước Bồn Tưới</div>
                    <div id="n2-water" class="text-2xl font-bold text-sky-600 dark:text-sky-400 mt-1">--%</div>
                </div>

                <div class="bg-slate-50 dark:bg-slate-900/70 p-4 rounded-xl border border-slate-200 dark:border-slate-700/60 transition-colors duration-300">
                    <div class="text-xs text-slate-500 dark:text-slate-400 font-medium">Trạng thái Mưa</div>
                    <div id="n2-rain" class="text-xl font-bold text-amber-500 dark:text-amber-300 mt-1">--</div>
                </div>

                <div class="bg-slate-50 dark:bg-slate-900/70 p-4 rounded-xl border border-slate-200 dark:border-slate-700/60 transition-colors duration-300">
                    <div class="text-xs text-slate-500 dark:text-slate-400 font-medium">Nhiệt độ / Ẩm KK</div>
                    <div id="n2-th" class="text-xl font-bold text-emerald-600 dark:text-emerald-300 mt-1">--°C / --%</div>
                </div>
            </div>

            <!-- Bảng điều khiển Node -->
            <div class="bg-slate-50 dark:bg-slate-900 p-4 rounded-xl border border-slate-200 dark:border-slate-700 transition-colors duration-300">
                <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
                    <span class="font-semibold text-sm text-slate-800 dark:text-slate-200">Chế độ vận hành Node:</span>
                    <div class="flex gap-2">
                        <button onclick="setCommand({mode2: 1})" id="btn-n2-auto" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">TỰ ĐỘNG (AUTO)</button>
                        <button onclick="setCommand({mode2: 0})" id="btn-n2-man" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">THỦ CÔNG (MANUAL)</button>
                    </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <button onclick="toggleDevice('pump2', 'mode2')" id="btn-pump2" class="p-3.5 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center">
                        <span>BƠM 1 (TƯỚI GỐC)</span>
                        <span id="txt-pump2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                    </button>
                    <button onclick="toggleDevice('pump3', 'mode2')" id="btn-pump3" class="p-3.5 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center">
                        <span>BƠM 2 (PHUN SƯƠNG)</span>
                        <span id="txt-pump3" class="text-xs mt-1 font-normal">Đang Tắt</span>
                    </button>
                    <button onclick="toggleDevice('buzz2', 'mode2')" id="btn-buzz2" class="p-3.5 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center">
                        <span>CÒI BÁO ĐỘNG</span>
                        <span id="txt-buzz2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                    </button>
                </div>
            </div>
        </div>

        <!-- Bảng Nhật Ký Thực Tế -->
        <div class="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm dark:shadow-lg transition-colors duration-300">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-200 mb-4">NHẬT KÝ DỮ LIỆU THU THẬP GẦN NHẤT</h3>
            <div class="overflow-x-auto border border-slate-200 dark:border-slate-700 rounded-lg">
                <table class="w-full text-left text-sm text-slate-700 dark:text-slate-300">
                    <thead class="text-xs uppercase bg-slate-50 dark:bg-slate-900 text-slate-500 dark:text-slate-400 border-b border-slate-200 dark:border-slate-700">
                        <tr>
                            <th class="p-3">Thời gian</th>
                            <th class="p-3">Độ ẩm đất (Node)</th>
                            <th class="p-3">Mực nước Bồn (Node)</th>
                            <th class="p-3">Nhiệt độ / Ẩm KK (Node)</th>
                        </tr>
                    </thead>
                    <tbody id="history-body" class="divide-y divide-slate-100 dark:divide-slate-700"></tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        let currentCommands = { mode1: 1, pump1: 0, buzz1: 0, mode2: 1, pump2: 0, pump3: 0, buzz2: 0 };

        // Hàm chuyển đổi chế độ sáng/tối
        function toggleDarkMode() {
            const html = document.documentElement;
            html.classList.toggle('dark');
            const isDark = html.classList.contains('dark');
            document.getElementById('theme-text').innerText = isDark ? "Chế độ sáng" : "Chế độ tối";
        }

        function styleButton(btnId, txtId, isActive) {
            const btn = document.getElementById(btnId);
            const txt = document.getElementById(txtId);
            if (!btn || !txt) return;
            if (isActive == 1) {
                btn.className = "p-3.5 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-emerald-500 dark:bg-emerald-600 border-emerald-600 dark:border-emerald-400 text-white shadow-md dark:shadow-lg";
                txt.innerText = "ĐANG BẬT (ON)";
            } else {
                btn.className = "p-3.5 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-white dark:bg-slate-800 border-slate-300 dark:border-slate-600 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700";
                txt.innerText = "ĐANG TẮT (OFF)";
            }
        }

        function styleModeButtons(autoBtnId, manBtnId, isAuto) {
            const autoBtn = document.getElementById(autoBtnId);
            const manBtn  = document.getElementById(manBtnId);
            if (!autoBtn || !manBtn) return;
            if (isAuto == 1) {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-sky-500 text-white shadow-sm border border-sky-600 dark:border-sky-500";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-white dark:bg-slate-800 text-slate-500 dark:text-slate-400 border border-slate-300 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-700";
            } else {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-white dark:bg-slate-800 text-slate-500 dark:text-slate-400 border border-slate-300 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-700";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-amber-500 text-white dark:text-slate-950 shadow-sm border border-amber-600 dark:border-amber-500";
            }
        }

        async function setCommand(newCmds) {
            currentCommands = { ...currentCommands, ...newCmds };
            await fetch('api.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'set_command', commands: currentCommands })
            });
            fetchDashboard();
        }

        function toggleDevice(deviceKey, modeKey) {
            const nextVal = currentCommands[deviceKey] == 1 ? 0 : 1;
            const payload = {};
            payload[modeKey]   = 0;
            payload[deviceKey] = nextVal;
            setCommand(payload);
        }

        async function fetchDashboard() {
            try {
                const res  = await fetch('api.php');
                const data = await res.json();
                const st   = data.state;
                currentCommands = st.commands;

                document.getElementById('last-update').innerText = st.updated_at;

                // Cập nhật dữ liệu từ node2 lên giao diện Trạm Node
                const soilVal = st.node2.soil !== undefined ? st.node2.soil : 0;
                document.getElementById('n2-soil').innerText       = soilVal + '%';
                document.getElementById('n2-soil-bar').style.width = soilVal + '%';
                document.getElementById('n2-water').innerText      = st.node2.water + '%';
                document.getElementById('n2-rain').innerText       = st.node2.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n2-th').innerText         = `${st.node2.temp}°C / ${st.node2.hum}%`;
                document.getElementById('n2-rssi').innerText       = `Sóng LoRa: ${st.node2.rssi} dBm`;
                document.getElementById('n2-sys-code').innerText   = `STATUS: ${st.node2.status_code || 'NORMAL'}`;

                const s2 = document.getElementById('n2-status');
                s2.innerText = st.node2.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s2.className = st.node2.online == 1
                    ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400 border border-emerald-300 dark:border-emerald-500/40'
                    : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-100 dark:bg-rose-500/20 text-rose-700 dark:text-rose-400 border border-rose-300 dark:border-rose-500/40';

                // Cập nhật trạng thái nút điều khiển
                styleModeButtons('btn-n2-auto', 'btn-n2-man', currentCommands.mode2);
                styleButton('btn-pump2', 'txt-pump2', currentCommands.pump2);
                styleButton('btn-pump3', 'txt-pump3', currentCommands.pump3);
                styleButton('btn-buzz2', 'txt-buzz2', currentCommands.buzz2);

                // Cập nhật bảng lịch sử
                const tbody = document.getElementById('history-body');
                tbody.innerHTML = (data.history || []).slice(0, 8).map(row => `
                    <tr class="hover:bg-slate-50 dark:hover:bg-slate-700/40 transition-colors">
                        <td class="p-3 font-mono text-xs text-slate-500 dark:text-slate-400">${row.time}</td>
                        <td class="p-3 font-bold text-emerald-600 dark:text-emerald-400">${row.n2_soil !== undefined ? row.n2_soil : '--'}%</td>
                        <td class="p-3 font-semibold text-sky-600 dark:text-sky-300">${row.n2_water}%</td>
                        <td class="p-3 text-slate-700 dark:text-slate-300 font-medium">${row.n2_temp}°C / ${row.n2_hum}%</td>
                    </tr>
                `).join('');
            } catch (e) {
                console.error(e);
            }
        }

        fetchDashboard();
        setInterval(fetchDashboard, 1000);
    </script>
</body>
</html>