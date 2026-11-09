<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Quản Lý IoT LoRa Đa Năng Vùng Sâu Vùng Xa</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen p-4 md:p-8">
    <div class="max-w-6xl mx-auto">
        <!-- Header -->
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center bg-slate-800 p-6 rounded-2xl border border-slate-700 mb-6 shadow-lg">
            <div>
                <h1 class="text-2xl md:text-3xl font-bold text-emerald-400">TRUNG TÂM ĐIỀU HÀNH IOT LORA 433MHz</h1>
                <p class="text-slate-400 text-sm mt-1">Gateway ESP32-S3 &bull; 2 Trạm ESP8266 Năng Lượng Mặt Trời &bull; Tích Hợp Cảm Biến Độ Ẩm Đất</p>
            </div>
            <div class="mt-4 md:mt-0 text-right">
                <span class="text-xs text-slate-400 block">Đồng bộ lần cuối:</span>
                <span id="last-update" class="text-sm font-mono text-amber-400">Đang tải...</span>
            </div>
        </div>

        <!-- 2 Node Cards -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <!-- NODE 1: TRẠM THỦY LỢI / CHỐNG NGẬP -->
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg flex flex-col justify-between">
                <div>
                    <div class="flex justify-between items-center border-b border-slate-700 pb-4 mb-4">
                        <div>
                            <h2 class="text-xl font-bold text-sky-400">TRẠM NODE 1: THỦY LỢI & CHỐNG NGẬP</h2>
                            <p class="text-xs text-slate-400" id="n1-rssi">Sóng LoRa: -- dBm</p>
                        </div>
                        <span id="n1-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-700 text-slate-300">KIỂM TRA...</span>
                    </div>

                    <!-- Thông số cảm biến Node 1 -->
                    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Mực nước Kênh/Hồ</div>
                            <div id="n1-water" class="text-2xl font-bold text-sky-400 mt-1">--%</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Trạng thái Mưa</div>
                            <div id="n1-rain" class="text-lg font-bold text-amber-300 mt-1">--</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Nhiệt độ / Ẩm KK</div>
                            <div id="n1-th" class="text-lg font-bold text-emerald-300 mt-1">--°C / --%</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60 col-span-2 sm:col-span-3">
                            <div class="text-xs text-slate-400">Áp suất khí quyển (BME280)</div>
                            <div id="n1-pres" class="text-base font-semibold text-purple-300 mt-1">-- hPa</div>
                        </div>
                    </div>
                </div>

                <!-- Bảng điều khiển Node 1 -->
                <div class="bg-slate-900 p-4 rounded-xl border border-slate-700">
                    <div class="flex justify-between items-center mb-4">
                        <span class="font-semibold text-sm">Chế độ vận hành Node 1:</span>
                        <div class="flex gap-2">
                            <button onclick="setCommand({mode1: 1})" id="btn-n1-auto" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">TỰ ĐỘNG (AUTO)</button>
                            <button onclick="setCommand({mode1: 0})" id="btn-n1-man" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">THỦ CÔNG (MANUAL)</button>
                        </div>
                    </div>
                    <div class="grid grid-cols-2 gap-3">
                        <button onclick="toggleDevice('pump1', 'mode1')" id="btn-pump1" class="p-3 rounded-xl font-bold text-sm border transition flex flex-col items-center justify-center">
                            <span>MÁY BƠM 1 (XẢ NGẬP)</span>
                            <span id="txt-pump1" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('buzz1', 'mode1')" id="btn-buzz1" class="p-3 rounded-xl font-bold text-sm border transition flex flex-col items-center justify-center">
                            <span>CÒI BÁO ĐỘNG 1</span>
                            <span id="txt-buzz1" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                    </div>
                </div>
            </div>

            <!-- NODE 2: TRẠM NÔNG NGHIỆP THÔNG MINH -->
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg flex flex-col justify-between">
                <div>
                    <div class="flex justify-between items-center border-b border-slate-700 pb-4 mb-4">
                        <div>
                            <h2 class="text-xl font-bold text-emerald-400">TRẠM NODE 2: VƯỜN NÔNG NGHIỆP</h2>
                            <p class="text-xs text-slate-400" id="n2-rssi">Sóng LoRa: -- dBm</p>
                        </div>
                        <span id="n2-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-700 text-slate-300">KIỂM TRA...</span>
                    </div>

                    <!-- Thông số cảm biến Node 2 (Có thanh tiến trình Độ ẩm đất) -->
                    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-emerald-500/40 col-span-2 sm:col-span-3">
                            <div class="flex justify-between items-center">
                                <span class="text-xs text-slate-300 font-semibold">🌱 Độ ẩm đất canh tác (Ngưỡng tưới: 35% - 65%)</span>
                                <span id="n2-soil" class="text-xl font-bold text-emerald-400">--%</span>
                            </div>
                            <div class="w-full bg-slate-700 h-2.5 rounded-full mt-2 overflow-hidden">
                                <div id="n2-soil-bar" class="bg-emerald-500 h-full rounded-full transition-all duration-300" style="width: 0%"></div>
                            </div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Mực nước Bồn Tưới</div>
                            <div id="n2-water" class="text-xl font-bold text-sky-400 mt-1">--%</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Trạng thái Mưa</div>
                            <div id="n2-rain" class="text-base font-bold text-amber-300 mt-1">--</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Nhiệt độ / Ẩm KK</div>
                            <div id="n2-th" class="text-base font-bold text-emerald-300 mt-1">--°C / --%</div>
                        </div>
                    </div>
                </div>

                <!-- Bảng điều khiển Node 2 -->
                <div class="bg-slate-900 p-4 rounded-xl border border-slate-700">
                    <div class="flex justify-between items-center mb-4">
                        <span class="font-semibold text-sm">Chế độ vận hành Node 2:</span>
                        <div class="flex gap-2">
                            <button onclick="setCommand({mode2: 1})" id="btn-n2-auto" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">TỰ ĐỘNG (AUTO)</button>
                            <button onclick="setCommand({mode2: 0})" id="btn-n2-man" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">THỦ CÔNG (MANUAL)</button>
                        </div>
                    </div>
                    <div class="grid grid-cols-3 gap-2">
                        <button onclick="toggleDevice('pump2', 'mode2')" id="btn-pump2" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>BƠM 2 (TƯỚI GỐC)</span>
                            <span id="txt-pump2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('pump3', 'mode2')" id="btn-pump3" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>BƠM 3 (PHUN SƯƠNG)</span>
                            <span id="txt-pump3" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('buzz2', 'mode2')" id="btn-buzz2" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>CÒI BÁO 2</span>
                            <span id="txt-buzz2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bảng Nhật Ký Thực Tế -->
        <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg">
            <h3 class="text-lg font-bold text-slate-200 mb-4">NHẬT KÝ DỮ LIỆU THU THẬP GẦN NHẤT</h3>
            <div class="overflow-x-auto">
                <table class="w-full text-left text-sm text-slate-300">
                    <thead class="text-xs uppercase bg-slate-900 text-slate-400">
                        <tr>
                            <th class="p-3">Thời gian</th>
                            <th class="p-3">Mực nước Hồ (Node 1)</th>
                            <th class="p-3">Nhiệt độ (Node 1)</th>
                            <th class="p-3">Độ ẩm đất (Node 2)</th>
                            <th class="p-3">Mực nước Bồn (Node 2)</th>
                            <th class="p-3">Nhiệt độ / Ẩm KK (Node 2)</th>
                        </tr>
                    </thead>
                    <tbody id="history-body" class="divide-y divide-slate-700"></tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        let currentCommands = { mode1: 1, pump1: 0, buzz1: 0, mode2: 1, pump2: 0, pump3: 0, buzz2: 0 };

        function styleButton(btnId, txtId, isActive) {
            const btn = document.getElementById(btnId);
            const txt = document.getElementById(txtId);
            if (isActive == 1) {
                btn.className = "p-3 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-emerald-600 border-emerald-400 text-white shadow-lg";
                txt.innerText = "ĐANG BẬT (ON)";
            } else {
                btn.className = "p-3 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-slate-800 border-slate-600 text-slate-300 hover:bg-slate-700";
                txt.innerText = "ĐANG TẮT (OFF)";
            }
        }

        function styleModeButtons(autoBtnId, manBtnId, isAuto) {
            const autoBtn = document.getElementById(autoBtnId);
            const manBtn  = document.getElementById(manBtnId);
            if (isAuto == 1) {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-sky-500 text-white";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
            } else {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-amber-500 text-slate-950";
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

                // Cập nhật Node 1
                document.getElementById('n1-water').innerText = st.node1.water + '%';
                document.getElementById('n1-rain').innerText  = st.node1.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n1-th').innerText    = `${st.node1.temp}°C / ${st.node1.hum}%`;
                document.getElementById('n1-pres').innerText  = `${st.node1.pres} hPa`;
                document.getElementById('n1-rssi').innerText  = `Sóng LoRa: ${st.node1.rssi} dBm`;
                const s1 = document.getElementById('n1-status');
                s1.innerText = st.node1.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s1.className = st.node1.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Cập nhật Node 2 (Gồm cả Độ ẩm đất)
                const soilVal = st.node2.soil !== undefined ? st.node2.soil : 0;
                document.getElementById('n2-soil').innerText     = soilVal + '%';
                document.getElementById('n2-soil-bar').style.width = soilVal + '%';
                document.getElementById('n2-water').innerText    = st.node2.water + '%';
                document.getElementById('n2-rain').innerText     = st.node2.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n2-th').innerText       = `${st.node2.temp}°C / ${st.node2.hum}%`;
                document.getElementById('n2-rssi').innerText     = `Sóng LoRa: ${st.node2.rssi} dBm`;
                const s2 = document.getElementById('n2-status');
                s2.innerText = st.node2.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s2.className = st.node2.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Nút điều khiển
                styleModeButtons('btn-n1-auto', 'btn-n1-man', currentCommands.mode1);
                styleModeButtons('btn-n2-auto', 'btn-n2-man', currentCommands.mode2);
                styleButton('btn-pump1', 'txt-pump1', currentCommands.pump1);
                styleButton('btn-buzz1', 'txt-buzz1', currentCommands.buzz1);
                styleButton('btn-pump2', 'txt-pump2', currentCommands.pump2);
                styleButton('btn-pump3', 'txt-pump3', currentCommands.pump3);
                styleButton('btn-buzz2', 'txt-buzz2', currentCommands.buzz2);

                // Bảng lịch sử
                const tbody = document.getElementById('history-body');
                tbody.innerHTML = (data.history || []).slice(0, 8).map(row => `
                    <tr class="hover:bg-slate-700/40">
                        <td class="p-3 font-mono text-xs">${row.time}</td>
                        <td class="p-3 font-semibold text-sky-400">${row.n1_water}%</td>
                        <td class="p-3">${row.n1_temp}°C</td>
                        <td class="p-3 font-bold text-emerald-400">${row.n2_soil !== undefined ? row.n2_soil : '--'}%</td>
                        <td class="p-3 font-semibold text-sky-300">${row.n2_water}%</td>
                        <td class="p-3">${row.n2_temp}°C / ${row.n2_hum}%</td>
                    </tr>
                `).join('');
            } catch (e) {
                console.error(e);
            }
        }

        fetchDashboard();
        setInterval(fetchDashboard, 2500);
    </script>
</body>
</html>