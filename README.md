# Ứng dụng Thời tiết - Weather Application

Ứng dụng thời tiết hoàn chỉnh sử dụng Java, Maven, UDP Client-Server, và Java Swing.

## Cấu trúc dự án

```
DBTT/
├── server/              # UDP Server
│   ├── pom.xml
│   └── src/main/java/com/weather/server/
│       ├── WeatherServer.java
│       ├── WeatherService.java
│       ├── WeatherAPIClient.java
│       └── model/
│           ├── CurrentWeather.java
│           ├── HourlyForecast.java
│           ├── DailyForecast.java
│           ├── WeatherResponse.java
│           ├── ClientRequest.java
│           └── ClientResponse.java
│
└── client/              # Java Swing Client
    ├── pom.xml
    └── src/main/java/com/weather/client/
        ├── WeatherClient.java
        ├── network/
        │   └── WeatherClientNetwork.java
        ├── model/
        │   ├── WeatherData.java
        │   └── DayDetailData.java
        └── ui/
            ├── MainFrame.java
            ├── SearchPanel.java
            ├── CurrentWeatherPanel.java
            ├── HourlyForecastPanel.java
            ├── DailyForecastPanel.java
            ├── DailyDetailPanel.java
            └── ChartPanel.java
```

## Yêu cầu hệ thống

- Java 11 hoặc cao hơn
- Maven 3.6+
- API Key từ OpenWeatherMap (miễn phí tại https://openweathermap.org/api)

## Cấu hình API Key

### Bước 1: Lấy API Key

1. Đăng ký tài khoản tại https://openweathermap.org/api
2. Vào phần API Keys và tạo key mới
3. Copy API key của bạn

### Bước 2: Cấu hình cho Server

Có 3 cách để cấu hình API key:

**Cách 1: Environment Variable (Khuyến nghị)**
```bash
export OPENWEATHER_API_KEY=your_api_key_here
```

**Cách 2: System Property**
```bash
java -Dopenweather.api.key=your_api_key_here -jar weather-server.jar
```

**Cách 3: Sửa code trực tiếp (không khuyến nghị)**
Sửa file `WeatherServer.java` và thêm API key vào code.

## Hướng dẫn Build và Chạy

### 1. Build Server

```bash
cd server
mvn clean package
```

File JAR sẽ được tạo tại: `server/target/weather-server-1.0.0.jar`

### 2. Chạy Server

**Với environment variable:**
```bash
export OPENWEATHER_API_KEY=your_api_key_here
cd server
java -jar target/weather-server-1.0.0.jar
```

**Với system property:**
```bash
cd server
java -Dopenweather.api.key=your_api_key_here -jar target/weather-server-1.0.0.jar
```

**Với port tùy chỉnh (mặc định 8888):**
```bash
java -Dopenweather.api.key=your_api_key_here -jar target/weather-server-1.0.0.jar 9999
```

Server sẽ chạy trên port 8888 (hoặc port bạn chỉ định) và chờ các request từ Client.

### 3. Build Client

```bash
cd client
mvn clean package
```

File JAR sẽ được tạo tại: `client/target/weather-client-1.0.0.jar`

### 4. Chạy Client

```bash
cd client
java -jar target/weather-client-1.0.0.jar
```

**Lưu ý:** Client mặc định kết nối đến `localhost:8888`. Nếu server chạy trên host/port khác, bạn cần sửa trong code `WeatherClientNetwork.java`.

## Sử dụng ứng dụng

### Giao diện chính

1. **Tìm kiếm thành phố:**
   - Nhập tên thành phố vào ô tìm kiếm (ví dụ: "Hà Nội", "Ho Chi Minh City", "London")
   - Nhấn Enter hoặc click nút tìm kiếm
   - Ứng dụng sẽ hiển thị thông tin thời tiết

2. **Xem thông tin thời tiết:**
   - **Thời tiết hiện tại:** Nhiệt độ, mô tả, gió, độ ẩm
   - **Dự báo theo giờ:** Scroll ngang để xem 24 giờ tiếp theo
   - **Dự báo 7 ngày:** Danh sách 7 ngày với nhiệt độ min-max

3. **Xem chi tiết ngày:**
   - Click vào một ngày trong danh sách 7 ngày
   - Xem biểu đồ nhiệt độ theo giờ
   - Xem khả năng mưa theo giờ
   - So sánh với ngày hôm nay

### Tính năng

- ✅ Tìm kiếm thành phố
- ✅ Hiển thị thời tiết hiện tại
- ✅ Dự báo theo giờ (24h)
- ✅ Dự báo 7 ngày
- ✅ Chi tiết ngày với biểu đồ nhiệt độ
- ✅ Khả năng mưa theo giờ
- ✅ So sánh với ngày hôm nay
- ✅ Giao diện tối đẹp mắt
- ✅ Xử lý lỗi đầy đủ

## Cấu trúc giao tiếp UDP

### Request từ Client

```json
{
  "type": "CURRENT",
  "city": "Hà Nội"
}
```

hoặc

```json
{
  "type": "DETAIL_DAY",
  "city": "Hà Nội",
  "dayTimestamp": 1700000000
}
```

### Response từ Server

```json
{
  "success": true,
  "data": {
    "city": "Hà Nội",
    "current": { ... },
    "hourly": [ ... ],
    "daily": [ ... ]
  }
}
```

hoặc

```json
{
  "success": false,
  "error": "City not found: Invalid City"
}
```

## Xử lý lỗi

Server xử lý các lỗi sau:
- Thành phố không hợp lệ
- API timeout hoặc lỗi
- UDP packet bị mất (retry 3 lần)
- JSON không hợp lệ

Client xử lý:
- Hiển thị thông báo lỗi khi không kết nối được server
- Retry tự động khi timeout
- Hiển thị lỗi khi thành phố không tìm thấy

## Troubleshooting

### Server không khởi động được

1. Kiểm tra port có bị chiếm không:
   ```bash
   netstat -an | grep 8888
   ```

2. Kiểm tra API key đã được set chưa:
   ```bash
   echo $OPENWEATHER_API_KEY
   ```

3. Kiểm tra log của server để xem lỗi chi tiết

### Client không kết nối được Server

1. Đảm bảo Server đã chạy
2. Kiểm tra firewall có chặn UDP port 8888 không
3. Kiểm tra host và port trong `WeatherClientNetwork.java`

### API trả về lỗi 401

- API key không hợp lệ hoặc chưa được kích hoạt
- Đăng nhập vào OpenWeatherMap và kiểm tra API key

### API trả về lỗi 429

- Đã vượt quá giới hạn request (free tier: 60 requests/phút)
- Đợi một chút rồi thử lại

## Phát triển thêm

### Thêm tính năng mới

1. **Thêm đơn vị nhiệt độ (Celsius/Fahrenheit):**
   - Sửa `WeatherAPIClient.java` để thêm parameter `units`
   - Cập nhật UI để hiển thị đơn vị

2. **Thêm nhiều thành phố:**
   - Lưu danh sách thành phố yêu thích
   - Thêm tab để chuyển đổi giữa các thành phố

3. **Thêm bản đồ:**
   - Tích hợp map API để hiển thị vị trí
   - Click trên map để xem thời tiết

## License

Dự án này được tạo cho mục đích học tập và demo.

## Tác giả

Ứng dụng thời tiết - Weather Application
Phiên bản: 1.0.0

