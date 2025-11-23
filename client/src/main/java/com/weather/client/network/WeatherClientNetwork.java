package com.weather.client.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.weather.client.model.DayDetailData;
import com.weather.client.model.WeatherData;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class WeatherClientNetwork {
    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_HOST = "localhost";
    private static final int TIMEOUT_MS = 5000; // Reduced from 10s to 5s
    private static final int MAX_RETRIES = 2; // Reduced from 3 to 2
    private static final int BUFFER_SIZE = 16384; // Increased for larger responses

    private final String serverHost;
    private final int serverPort;
    private final Gson gson;

    public WeatherClientNetwork() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public WeatherClientNetwork(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.gson = new Gson();
    }

    /**
     * Request current weather for a city
     */
    public WeatherData requestWeather(String city) throws IOException {
        String requestJson = gson.toJson(new Request("CURRENT", city, null));
        String responseJson = sendRequest(requestJson);
        
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (!response.success) {
            throw new IOException(response.error != null ? response.error : "Unknown error");
        }
        
        // Parse response data
        return parseWeatherData(response.data);
    }

    /**
     * Request day detail
     */
    public DayDetailData requestDayDetail(String city, long dayTimestamp) throws IOException {
        String requestJson = gson.toJson(new Request("DETAIL_DAY", city, dayTimestamp));
        String responseJson = sendRequest(requestJson);
        
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (!response.success) {
            throw new IOException(response.error != null ? response.error : "Unknown error");
        }
        
        // Parse response data
        return parseDayDetailData(response.data);
    }

    private String sendRequest(String requestJson) throws IOException {
        byte[] requestData = requestJson.getBytes(StandardCharsets.UTF_8);
        
        IOException lastException = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_MS);
                
                InetAddress serverAddress = InetAddress.getByName(serverHost);
                DatagramPacket requestPacket = new DatagramPacket(
                    requestData, requestData.length, serverAddress, serverPort
                );
                
                socket.send(requestPacket);
                
                // Receive response
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                
                String response = new String(
                    responsePacket.getData(), 0, responsePacket.getLength(), StandardCharsets.UTF_8
                );
                
                return response;
            } catch (SocketTimeoutException e) {
                lastException = new IOException("Request timeout", e);
                // Retry with shorter delay
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(200 * (attempt + 1)); // Faster retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted", ie);
                    }
                }
            } catch (IOException e) {
                lastException = e;
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(200 * (attempt + 1)); // Faster retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted", ie);
                    }
                }
            }
        }
        
        throw lastException != null ? lastException : new IOException("Failed after " + MAX_RETRIES + " attempts");
    }

    @SuppressWarnings("unchecked")
    private WeatherData parseWeatherData(Object data) {
        if (data == null) {
            return null;
        }
        
        try {
            String json = gson.toJson(data);
            java.util.Map<String, Object> map = gson.fromJson(json, java.util.Map.class);
            
            WeatherData weatherData = new WeatherData();
            weatherData.setCity((String) map.get("city"));
            weatherData.setTimezone((String) map.get("timezone"));
            
            // Parse current
            if (map.containsKey("current")) {
                java.util.Map<String, Object> currentMap = (java.util.Map<String, Object>) map.get("current");
                WeatherData.CurrentWeather current = parseCurrentWeather(currentMap);
                weatherData.setCurrent(current);
            }
            
            // Parse hourly - handle both array and list
            if (map.containsKey("hourly")) {
                Object hourlyObj = map.get("hourly");
                java.util.List<Object> hourlyList;
                if (hourlyObj instanceof java.util.List) {
                    hourlyList = (java.util.List<Object>) hourlyObj;
                } else if (hourlyObj instanceof Object[]) {
                    hourlyList = java.util.Arrays.asList((Object[]) hourlyObj);
                } else {
                    hourlyList = new java.util.ArrayList<>();
                }
                
                if (!hourlyList.isEmpty()) {
                    java.util.List<WeatherData.HourlyForecast> validHourly = new java.util.ArrayList<>();
                    for (Object item : hourlyList) {
                        if (item instanceof java.util.Map) {
                            WeatherData.HourlyForecast hourly = parseHourlyForecast((java.util.Map<String, Object>) item);
                            if (hourly != null && hourly.getTimestamp() > 0) {
                                validHourly.add(hourly);
                            }
                        }
                    }
                    if (!validHourly.isEmpty()) {
                        weatherData.setHourly(validHourly.toArray(new WeatherData.HourlyForecast[0]));
                    }
                }
            }
            
            // Parse daily - handle both array and list
            if (map.containsKey("daily")) {
                Object dailyObj = map.get("daily");
                java.util.List<Object> dailyList;
                if (dailyObj instanceof java.util.List) {
                    dailyList = (java.util.List<Object>) dailyObj;
                } else if (dailyObj instanceof Object[]) {
                    dailyList = java.util.Arrays.asList((Object[]) dailyObj);
                } else {
                    dailyList = new java.util.ArrayList<>();
                }
                
                if (!dailyList.isEmpty()) {
                    java.util.List<WeatherData.DailyForecast> validDaily = new java.util.ArrayList<>();
                    for (Object item : dailyList) {
                        if (item instanceof java.util.Map) {
                            WeatherData.DailyForecast daily = parseDailyForecast((java.util.Map<String, Object>) item);
                            if (daily != null && daily.getTimestamp() > 0) {
                                validDaily.add(daily);
                            }
                        }
                    }
                    if (!validDaily.isEmpty()) {
                        weatherData.setDaily(validDaily.toArray(new WeatherData.DailyForecast[0]));
                    }
                }
            }
            
            // Validate and ensure data completeness
            if (weatherData.getCurrent() == null) {
                System.err.println("WARNING: No current weather data");
                return null;
            }
            
            // Ensure hourly data exists
            if (weatherData.getHourly() == null || weatherData.getHourly().length == 0) {
                System.err.println("WARNING: No hourly forecast data");
                // Don't return null, just log warning
            } else {
                System.out.println("✓ Parsed " + weatherData.getHourly().length + " hourly forecasts");
            }
            
            // Ensure daily data exists
            if (weatherData.getDaily() == null || weatherData.getDaily().length == 0) {
                System.err.println("WARNING: No daily forecast data");
                // Don't return null, just log warning
            } else {
                System.out.println("✓ Parsed " + weatherData.getDaily().length + " daily forecasts");
            }
            
            return weatherData;
        } catch (Exception e) {
            System.err.println("Error parsing weather data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherData.CurrentWeather parseCurrentWeather(java.util.Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        
        WeatherData.CurrentWeather current = new WeatherData.CurrentWeather();
        current.setTemp(getDouble(map, "temp"));
        current.setFeelsLike(getDouble(map, "feelsLike"));
        current.setHumidity(getInt(map, "humidity"));
        current.setPressure(getDouble(map, "pressure"));
        current.setWindSpeed(getDouble(map, "windSpeed"));
        current.setWindDeg(getInt(map, "windDeg"));
        current.setWindGust(getDoubleOrNull(map, "windGust"));
        current.setTimestamp(getLong(map, "timestamp"));
        current.setUvi(getDouble(map, "uvi"));
        current.setVisibility(getInt(map, "visibility"));
        
        // Validate and fix values
        if (current.getHumidity() < 0) current.setHumidity(0);
        if (current.getHumidity() > 100) current.setHumidity(100);
        if (current.getUvi() < 0) current.setUvi(0);
        if (current.getVisibility() < 0) current.setVisibility(0);
        if (current.getWindDeg() < 0) current.setWindDeg(0);
        if (current.getWindDeg() >= 360) current.setWindDeg(current.getWindDeg() % 360);
        
        if (map.containsKey("weather")) {
            Object weatherObj = map.get("weather");
            if (weatherObj instanceof java.util.Map) {
                current.setWeather(parseWeatherCondition((java.util.Map<String, Object>) weatherObj));
            }
        }
        
        if (map.containsKey("tempRange")) {
            Object tempRangeObj = map.get("tempRange");
            if (tempRangeObj instanceof java.util.Map) {
                java.util.Map<String, Object> tempRangeMap = (java.util.Map<String, Object>) tempRangeObj;
                WeatherData.TempRange tempRange = new WeatherData.TempRange();
                tempRange.setMin(getDouble(tempRangeMap, "min"));
                tempRange.setMax(getDouble(tempRangeMap, "max"));
                // Ensure min <= max
                if (tempRange.getMin() > tempRange.getMax()) {
                    double temp = tempRange.getMin();
                    tempRange.setMin(tempRange.getMax());
                    tempRange.setMax(temp);
                }
                current.setTempRange(tempRange);
            }
        }
        
        return current;
    }

    @SuppressWarnings("unchecked")
    private WeatherData.HourlyForecast parseHourlyForecast(java.util.Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        
        WeatherData.HourlyForecast hourly = new WeatherData.HourlyForecast();
        hourly.setTimestamp(getLong(map, "timestamp"));
        hourly.setTemp(getDouble(map, "temp"));
        hourly.setPop(getDouble(map, "pop"));
        
        // Ensure pop is between 0 and 1
        if (hourly.getPop() < 0) hourly.setPop(0);
        if (hourly.getPop() > 1) hourly.setPop(1);
        
        if (map.containsKey("weather")) {
            Object weatherObj = map.get("weather");
            if (weatherObj instanceof java.util.Map) {
                hourly.setWeather(parseWeatherCondition((java.util.Map<String, Object>) weatherObj));
            }
        }
        
        // Validate required fields
        if (hourly.getTimestamp() <= 0 || Double.isNaN(hourly.getTemp())) {
            return null;
        }
        
        return hourly;
    }

    @SuppressWarnings("unchecked")
    private WeatherData.DailyForecast parseDailyForecast(java.util.Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        
        WeatherData.DailyForecast daily = new WeatherData.DailyForecast();
        daily.setTimestamp(getLong(map, "timestamp"));
        daily.setTempMin(getDouble(map, "tempMin"));
        daily.setTempMax(getDouble(map, "tempMax"));
        daily.setPop(getDouble(map, "pop"));
        daily.setHumidity(getInt(map, "humidity"));
        daily.setRain(getDouble(map, "rain"));
        
        // Ensure pop is between 0 and 1
        if (daily.getPop() < 0) daily.setPop(0);
        if (daily.getPop() > 1) daily.setPop(1);
        
        // Ensure humidity is between 0 and 100
        if (daily.getHumidity() < 0) daily.setHumidity(0);
        if (daily.getHumidity() > 100) daily.setHumidity(100);
        
        // Ensure min <= max
        if (daily.getTempMin() > daily.getTempMax()) {
            double temp = daily.getTempMin();
            daily.setTempMin(daily.getTempMax());
            daily.setTempMax(temp);
        }
        
        if (map.containsKey("weather")) {
            Object weatherObj = map.get("weather");
            if (weatherObj instanceof java.util.Map) {
                daily.setWeather(parseWeatherCondition((java.util.Map<String, Object>) weatherObj));
            }
        }
        
        // Validate required fields
        if (daily.getTimestamp() <= 0 || Double.isNaN(daily.getTempMin()) || Double.isNaN(daily.getTempMax())) {
            return null;
        }
        
        return daily;
    }

    @SuppressWarnings("unchecked")
    private WeatherData.WeatherCondition parseWeatherCondition(java.util.Map<String, Object> map) {
        WeatherData.WeatherCondition weather = new WeatherData.WeatherCondition();
        weather.setMain((String) map.get("main"));
        weather.setDescription((String) map.get("description"));
        weather.setIcon((String) map.get("icon"));
        return weather;
    }

    @SuppressWarnings("unchecked")
    private DayDetailData parseDayDetailData(Object data) {
        if (data == null) {
            return null;
        }
        
        try {
            String json = gson.toJson(data);
            java.util.Map<String, Object> map = gson.fromJson(json, java.util.Map.class);
            
            DayDetailData detailData = new DayDetailData();
            
            // Parse day
            if (map.containsKey("day")) {
                detailData.setDay(parseDayData((java.util.Map<String, Object>) map.get("day")));
            }
            
            // Parse hourly
            if (map.containsKey("hourly")) {
                java.util.List<Object> hourlyList = (java.util.List<Object>) map.get("hourly");
                DayDetailData.HourlyData[] hourly = new DayDetailData.HourlyData[hourlyList.size()];
                for (int i = 0; i < hourlyList.size(); i++) {
                    hourly[i] = parseHourlyData((java.util.Map<String, Object>) hourlyList.get(i));
                }
                detailData.setHourly(hourly);
            }
            
            // Parse today
            if (map.containsKey("today")) {
                detailData.setToday(parseTodayData((java.util.Map<String, Object>) map.get("today")));
            }
            
            return detailData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private DayDetailData.DayData parseDayData(java.util.Map<String, Object> map) {
        DayDetailData.DayData day = new DayDetailData.DayData();
        day.setTimestamp(getLong(map, "timestamp"));
        day.setTempMin(getDouble(map, "tempMin"));
        day.setTempMax(getDouble(map, "tempMax"));
        day.setTempAvg(getDouble(map, "tempAvg"));
        day.setHumidity(getInt(map, "humidity"));
        day.setPop(getDouble(map, "pop"));
        day.setRain(getDouble(map, "rain"));
        
        if (map.containsKey("weather")) {
            day.setWeather(parseDayDetailWeatherCondition((java.util.Map<String, Object>) map.get("weather")));
        }
        
        return day;
    }

    @SuppressWarnings("unchecked")
    private DayDetailData.HourlyData parseHourlyData(java.util.Map<String, Object> map) {
        DayDetailData.HourlyData hourly = new DayDetailData.HourlyData();
        hourly.setTimestamp(getLong(map, "timestamp"));
        hourly.setTemp(getDouble(map, "temp"));
        hourly.setPop(getDouble(map, "pop"));
        hourly.setHumidity(getInt(map, "humidity"));
        
        if (map.containsKey("weather")) {
            hourly.setWeather(parseDayDetailWeatherCondition((java.util.Map<String, Object>) map.get("weather")));
        }
        
        return hourly;
    }

    @SuppressWarnings("unchecked")
    private DayDetailData.TodayData parseTodayData(java.util.Map<String, Object> map) {
        DayDetailData.TodayData today = new DayDetailData.TodayData();
        today.setTempAvg(getDouble(map, "tempAvg"));
        today.setHumidity(getInt(map, "humidity"));
        today.setRain(getDouble(map, "rain"));
        return today;
    }

    @SuppressWarnings("unchecked")
    private DayDetailData.WeatherCondition parseDayDetailWeatherCondition(java.util.Map<String, Object> map) {
        DayDetailData.WeatherCondition weather = new DayDetailData.WeatherCondition();
        weather.setMain((String) map.get("main"));
        weather.setIcon((String) map.get("icon"));
        return weather;
    }

    // Helper methods
    private double getDouble(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    private Double getDoubleOrNull(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private int getInt(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private long getLong(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    // Request/Response models
    private static class Request {
        String type;
        String city;
        Long dayTimestamp;

        Request(String type, String city, Long dayTimestamp) {
            this.type = type;
            this.city = city;
            this.dayTimestamp = dayTimestamp;
        }
    }

    private static class Response {
        boolean success;
        String error;
        Object data;
    }
}

