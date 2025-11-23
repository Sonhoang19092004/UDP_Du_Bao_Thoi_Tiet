<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   ỨNG DỤNG THỜI TIẾT - WEATHER APPLICATION (JAVA UDP CLIENT-SERVER)
</h2>
<div align="center">
    <p align="center">
        <img src="images/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="images/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="images/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

## 📖 1. Giới thiệu hệ thống
Ứng dụng thời tiết Client-Server sử dụng **UDP**, **Java Swing**, hỗ trợ hiển thị thời tiết hiện tại, dự báo theo giờ và 7 ngày, với giao diện tối hiện đại.

**🏗️ Kiến trúc hệ thống**:  
- **Server**: Nhận request từ Client, gọi API OpenWeather, trả dữ liệu JSON.  
- **Client**: Java Swing UI hiển thị thông tin thời tiết, biểu đồ và chi tiết theo ngày.  
- **API Key**: Sử dụng OpenWeatherMap API Key để lấy dữ liệu thời tiết.  

**🎯 Các chức năng chính**:  

**🖥️ Server**:  
- 🔌 **UDP Server**: Lắng nghe các request từ client trên port mặc định 8888  
- 🌐 **API Integration**: Gọi OpenWeather API, trả dữ liệu JSON chuẩn  
- ⚡ **Retry & Error Handling**: Tự động retry 3 lần khi packet mất, xử lý lỗi API hoặc JSON  

**💻 Client**:  
- 🔍 **Search City**: Nhập tên thành phố → hiển thị thời tiết hiện tại và dự báo  
- 📊 **Hourly & Daily Forecast**: Biểu đồ nhiệt độ theo giờ, dự báo 7 ngày  
- 🖼️ **UI tối hiện đại**: Dark theme, responsive panel  
- ⚡ **Error Handling**: Thông báo khi server không kết nối hoặc city không hợp lệ  

**⚙️ Hệ thống kỹ thuật**:  
- 🌐 **UDP Protocol**: Giao tiếp Client ↔ Server  
- 💾 **Data Models**: `CurrentWeather`, `HourlyForecast`, `DailyForecast`, `WeatherResponse`  
- 🛡️ **Error Handling**: Server và client xử lý lỗi đầy đủ, hiển thị thông báo thân thiện  
- 🎨 **Modern UI**: Java Swing với card panel, scrollable hourly forecast, chart panel  

---

## 🔧 2. Công nghệ sử dụng
- **☕ Java 11+**: Multithreading, UDP socket, data parsing  
- **🎨 Java Swing**: Giao diện hiển thị dữ liệu thời tiết, biểu đồ  
- **🌐 OpenWeatherMap API**: Lấy dữ liệu thời tiết  
- **💾 JSON Parsing**: Gson library  
- **⚙️ UDP Communication**: `DatagramSocket`, `DatagramPacket`  

---

## 🚀 3. Hình ảnh giao diện

<p align="center">
  <img src="images/current_weather_panel.png" alt="Current Weather Panel" width="700"/>
</p>
<p align="center">
  <em>Hình 1: Thời tiết hiện tại</em>
</p>

<p align="center">
  <img src="images/hourly_forecast_panel.png" alt="Hourly Forecast" width="700"/>
</p>
<p align="center">
  <em>Hình 2: Dự báo theo giờ</em>
</p>

<p align="center">
  <img src="images/daily_forecast_panel.png" alt="Daily Forecast" width="700"/>
</p>
<p align="center">
  <em>Hình 3: Dự báo 7 ngày</em>
</p>

<p align="center">
  <img src="images/daily_detail_panel.png" alt="Daily Detail" width="700"/>
</p>
<p align="center">
  <em>Hình 4: Chi tiết từng ngày với biểu đồ</em>
</p>

---

## 📝 4. Hướng dẫn cài đặt và sử dụng

### 🔧 Yêu cầu hệ thống
- **JDK 11+**  
- **Maven 3.6+**  
- **OS**: Windows / macOS / Linux  
- **API Key**: OpenWeatherMap  

### 📦 Cài đặt nhanh

```bash
# Build Server
cd server
mvn clean package
java -Dopenweather.api.key=your_api_key_here -jar target/weather-server-1.0.0.jar

# Build Client
cd client
mvn clean package
java -jar target/weather-client-1.0.0.jar

## Thông tin liên hệ  
Họ tên: Hoàng Công Sơn.  
Lớp: CNTT 16-03.  
Email: hoangcongson19092004@gmail.com.

© 2025 AIoTLab, Faculty of Information Technology, DaiNam University. All rights reserved.
