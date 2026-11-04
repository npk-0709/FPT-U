# IOT LORA 433MHZ SYSTEM - TECHNICAL REPORT SUMMARY
**Project:** Smart Agriculture Irrigation & Water Tank Station (Node 2) & Central Gateway (ESP32-S3)  
**Microcontrollers:** ESP8266 NodeMCU (Node 2) & ESP32-S3-WROOM-1 (Gateway)  
**Transceiver:** LoRa Ra-01 SX1278 (433MHz, SyncWord: 0xF3)

---

## 1. NODE 2 WIRING & HARDWARE CONFIGURATION

### Detailed Wiring Table (Node 2)
| No. | Component | Component Pin | Target Connection Pin (ESP32 / Power) | Wire Color | Technical Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **ESP8266 NodeMCU** | `VIN` / `VV` | `5V (+)` Power Rail | Dark Red | Main 5V DC board power input |
| | | `G` | Common Ground (`GND`) | Black | System common ground |
| | | `3V` | `3.3V` Sensor Power Rail | Bright Red | Regulated 3.3V logic power |
| **2** | **BME280 Sensor** | `VIN` / `GND` / `SCL` / `SDA` | `3V` / `G` / `D1` / `D2` | Red / Black / Yellow / Green | I2C Temperature & Pressure monitoring |
| **3** | **Soil Moisture** | `VCC` / `GND` / `AO` | `3V` / `G` / `A0` | Red / Black / Orange | Analog soil moisture measurement |
| **4** | **Relay Module (x2)** | `DC+` / `DC-` / `IN2` / `IN3` | `5V` / `G` / `D0` / `D4` | Red / Black / Brown / Pink | Root irrigation & misting pump control |
| **5** | **18650 Battery** | `+` / `-` | `B+` / `B-` (TP4056) | Red / Black | 3.3V - 4.2V energy storage |
| **6** | **TP4056 Charger** | `IN+`/`IN-`/`OUT+`/`OUT-` | Solar Panel & 5V Rail | Red / Black | Battery charging & power management |
| **7** | **Solar Panel** | `+` (Red) / `-` (Black) | `IN+` / `IN-` (TP4056) | Red / Black | Renewable energy harvesting |
| **8** | **5V Water Pump (x2)** | Red (`+`) / Black (`-`) | Relay `NO` & Ground | Blue / Black | 5V DC miniature water pumps |
| **9** | **Water Level Sensor**| `+` / `-` / `S` | `3V` / `G` / `RX` (GPIO3) | Red / Black / Purple | Tank empty/full level detection |
| **10**| **Raindrops Sensor** | `VCC` / `GND` / `DO` | `3V` / `G` / `D3` (GPIO0) | Red / Black / Cyan | Rain detection interlock |
| **11**| **LoRa Ra-01 SX1278** | `3.3V`/`GND`/`SCK`/`MISO`/`MOSI`/`NSS`/`RST` | `3V`/`G`/`D5`/`D6`/`D7`/`D8`/`RST` | Multi-color | SPI communication interface |
| **12**| **Alarm Buzzer 2** | `+` (VCC) / `-` (GND) | `TX` (GPIO1) / `G` | Amber / Black | Local emergency audio alert |

---

## 2. GATEWAY ESP32-S3 WIRING CONFIGURATION

### Detailed Wiring Table (Gateway)
| No. | Component | Component Pin | Target Connection (ESP32-S3) | Wire Color | Technical Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **ESP32-S3-WROOM-1** | `5V` / `GND` / `3V3` | External Power & LoRa Rail | Red / Black | Central gateway microcontroller |
| **2** | **LoRa Ra-01 SX1278** | `3.3V` / `GND` / `SCK` / `MISO` / `MOSI` / `NSS` / `RST` / `DIO0` | `3V3`/`GND`/`12`/`13`/`11`/`10`/`9`/`4` | Multi-color | Gateway RF transceiver (SPI protocol) |
| **3** | **Rain Warning LED** | Anode (`+`) / Cathode (`-`) | GPIO7 / `GND` | Teal / Black | Visual rain status indicator |
| **4** | **Gateway Buzzer** | `+` (VCC) / `-` (GND) | GPIO8 / `GND` | Amber / Black | Central emergency flood/drought buzzer |

---

## 3. SYSTEM OPERATIONAL SCENARIOS (10 SUCCESSFUL USE CASES)

### Part I: Node 2 Local Operations & Protections
1. **Soil Moisture-Based Root Irrigation:** Automatically activates Pump 2 when soil moisture drops below $55\%$ ($SOIL\_START\_IRRIG$) and stops when moisture reaches or exceeds $70\%$ ($SOIL\_STOP\_IRRIG$).
2. **Temperature-Based Misting:** Activates Pump 3 when ambient temperature exceeds $33.0^\circ\text{C}$ and soil is not fully saturated, shutting off when temperature drops $\le 30.0^\circ\text{C}$.
3. **Dry-Run Protection Lock:** Triggers when water level sensor falls below $20\%$ ($RX = LOW$), locking out both pumps (`dryRunLocked = true`) and beeping every 5 seconds to prevent pump burnout.
4. **Rain & Storm Interlock:** Instantly halts irrigation pumps when rain is detected ($D3 = LOW$) or storm pre-warnings are received from the gateway.
5. **Pump Stall & Pipeline Failure Protection:** Detects water blockages or broken pipes if Pump 2 runs for 3 minutes without increasing soil moisture by at least $3\%$, locking the system and triggering continuous alarms.

### Part II: Gateway Central Operations & Monitoring
6. **Real-Time Flood Alert (Node 1):** Activates gateway sirens, LED indicators, and pushes instant Telegram alerts when water levels reach or exceed $80\%$.
7. **Storm & Low Pressure Warning:** Analyzes low barometric pressure ($< 1002.0\text{ hPa}$) coupled with rain and high water levels to issue flash flood risk warnings.
8. **Node Watchdog & Offline Detection:** Monitors node heartbeat timeouts ($> 60\text{ seconds}$), updates database online status, and alerts users via Telegram if solar/hardware power fails.
9. **Cloud Sync & Web Override:** Periodically synchronizes telemetry data via HTTP POST JSON to the PHP Web Server (`api.php`) and receives manual control commands (`MANUAL`/`AUTO`) for downlink transmission.
10. **Local Visual & Audio Indicators:** Updates rain warning LEDs and gateway buzzers in real-time based on incoming LoRa packets without relying solely on cloud connectivity.