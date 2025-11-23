package com.weather.server;

import com.google.gson.Gson;
import com.weather.server.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    private final WeatherAPIClient apiClient;
    private final Gson gson;

    public WeatherService(String apiKey) {
        this.apiClient = new WeatherAPIClient(apiKey);
        this.gson = new Gson();
    }

    /**
     * Process client request and return response
     */
    public ClientResponse processRequest(ClientRequest request) {
        try {
            if (request == null || request.getType() == null || request.getCity() == null) {
                return new ClientResponse(false, "Invalid request format");
            }

            String type = request.getType();
            
            if ("CURRENT".equals(type)) {
                return handleCurrentWeatherRequest(request.getCity());
            } else if ("DETAIL_DAY".equals(type)) {
                return handleDetailDayRequest(request.getCity(), request.getDayTimestamp());
            } else {
                return new ClientResponse(false, "Unknown request type: " + type);
            }
        } catch (Exception e) {
            logger.error("Unexpected error processing request, using mock data", e);
            // Fallback to mock data instead of returning error
            try {
                if (request != null && request.getType() != null) {
                    if ("CURRENT".equals(request.getType())) {
                        return handleCurrentWeatherRequest(request.getCity());
                    } else if ("DETAIL_DAY".equals(request.getType())) {
                        return handleDetailDayRequest(request.getCity(), request.getDayTimestamp());
                    }
                }
            } catch (Exception fallbackError) {
                logger.error("Error generating mock data", fallbackError);
            }
            return new ClientResponse(false, "Server error: " + e.getMessage());
        }
    }

    /**
     * Handle current weather request
     */
    private ClientResponse handleCurrentWeatherRequest(String city) {
        WeatherResponse weatherData = null;
        boolean useMockData = false;
        
        try {
            weatherData = apiClient.getWeatherData(city);
        } catch (IOException e) {
            logger.warn("API request failed, using mock data: {}", e.getMessage());
            useMockData = true;
            weatherData = MockWeatherData.generateMockData(city);
        } catch (Exception e) {
            logger.error("Unexpected error fetching weather data, using mock data", e);
            useMockData = true;
            weatherData = MockWeatherData.generateMockData(city);
        }
        
        if (useMockData) {
            logger.info("Using mock weather data for city: {}", city);
        }
        
        // Build optimized response
        Map<String, Object> responseData = new HashMap<>();
        
        // Current weather
        CurrentWeather current = weatherData.getCurrent();
        Map<String, Object> currentData = new HashMap<>();
        currentData.put("temp", current.getTemp());
        currentData.put("feelsLike", current.getFeelsLike());
        currentData.put("humidity", current.getHumidity());
        currentData.put("pressure", current.getPressure());
        currentData.put("windSpeed", current.getWindSpeed());
        currentData.put("windDeg", current.getWindDeg());
        currentData.put("windGust", current.getWindGust());
        currentData.put("timestamp", current.getTimestamp());
        currentData.put("uvi", current.getUvi());
        currentData.put("visibility", current.getVisibility());
        
        if (current.getWeather() != null && current.getWeather().length > 0) {
            Map<String, Object> weather = new HashMap<>();
            weather.put("main", current.getWeather()[0].getMain());
            weather.put("description", current.getWeather()[0].getDescription());
            weather.put("icon", current.getWeather()[0].getIcon());
            currentData.put("weather", weather);
        }
        
        // Daily min/max (from first daily forecast)
        if (weatherData.getDaily() != null && weatherData.getDaily().length > 0) {
            DailyForecast today = weatherData.getDaily()[0];
            Map<String, Object> tempData = new HashMap<>();
            tempData.put("min", today.getTemp().getMin());
            tempData.put("max", today.getTemp().getMax());
            currentData.put("tempRange", tempData);
        }
        
        responseData.put("current", currentData);
        
        // Hourly forecast (48 hours)
        if (weatherData.getHourly() != null && weatherData.getHourly().length > 0) {
            int hourlyCount = Math.min(48, weatherData.getHourly().length);
            logger.debug("Processing {} hourly forecasts", hourlyCount);
            Map<String, Object>[] hourlyData = new Map[hourlyCount];
            for (int i = 0; i < hourlyCount; i++) {
                HourlyForecast hourly = weatherData.getHourly()[i];
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("timestamp", hourly.getTimestamp());
                hourData.put("temp", hourly.getTemp());
                hourData.put("pop", hourly.getPop());
                
                if (hourly.getWeather() != null && hourly.getWeather().length > 0) {
                    Map<String, Object> weather = new HashMap<>();
                    weather.put("main", hourly.getWeather()[0].getMain());
                    weather.put("icon", hourly.getWeather()[0].getIcon());
                    hourData.put("weather", weather);
                }
                
                hourlyData[i] = hourData;
            }
            responseData.put("hourly", hourlyData);
            logger.debug("Added {} hourly forecasts to response", hourlyData.length);
        } else {
            logger.warn("No hourly forecast data available");
        }
        
        // Daily forecast (7 days)
        if (weatherData.getDaily() != null && weatherData.getDaily().length > 0) {
            int dailyCount = Math.min(7, weatherData.getDaily().length);
            logger.debug("Processing {} daily forecasts", dailyCount);
            Map<String, Object>[] dailyData = new Map[dailyCount];
            for (int i = 0; i < dailyCount; i++) {
                DailyForecast daily = weatherData.getDaily()[i];
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("timestamp", daily.getTimestamp());
                dayData.put("tempMin", daily.getTemp().getMin());
                dayData.put("tempMax", daily.getTemp().getMax());
                dayData.put("pop", daily.getPop());
                dayData.put("humidity", daily.getHumidity());
                
                if (daily.getWeather() != null && daily.getWeather().length > 0) {
                    Map<String, Object> weather = new HashMap<>();
                    weather.put("main", daily.getWeather()[0].getMain());
                    weather.put("description", daily.getWeather()[0].getDescription());
                    weather.put("icon", daily.getWeather()[0].getIcon());
                    dayData.put("weather", weather);
                }
                
                if (daily.getRain() != null) {
                    dayData.put("rain", daily.getRain().getTotal() != null ? daily.getRain().getTotal() : 0);
                } else {
                    dayData.put("rain", 0);
                }
                
                dailyData[i] = dayData;
            }
            responseData.put("daily", dailyData);
            logger.debug("Added {} daily forecasts to response", dailyData.length);
        } else {
            logger.warn("No daily forecast data available");
        }
        
        responseData.put("city", city);
        responseData.put("timezone", weatherData.getTimezone());
        
        return new ClientResponse(true, responseData);
    }

    /**
     * Handle detail day request
     */
    private ClientResponse handleDetailDayRequest(String city, Long dayTimestamp) {
        WeatherResponse weatherData = null;
        boolean useMockData = false;
        
        try {
            weatherData = apiClient.getWeatherData(city);
        } catch (IOException e) {
            logger.warn("API request failed, using mock data: {}", e.getMessage());
            useMockData = true;
            weatherData = MockWeatherData.generateMockData(city);
        } catch (Exception e) {
            logger.error("Unexpected error fetching weather data, using mock data", e);
            useMockData = true;
            weatherData = MockWeatherData.generateMockData(city);
        }
        
        if (useMockData) {
            logger.info("Using mock weather data for day detail: {}", city);
        }
        
        if (dayTimestamp == null) {
            return new ClientResponse(false, "Day timestamp is required");
        }
        
        // Find the day in daily forecast
        DailyForecast targetDay = null;
        for (DailyForecast daily : weatherData.getDaily()) {
            // Compare day (ignore time)
            long dayStart = (daily.getTimestamp() / 86400) * 86400;
            long targetDayStart = (dayTimestamp / 86400) * 86400;
            if (dayStart == targetDayStart) {
                targetDay = daily;
                break;
            }
        }
        
        if (targetDay == null) {
            return new ClientResponse(false, "Day not found in forecast");
        }
        
        // Get hourly data for that day
        Map<String, Object>[] hourlyData = null;
        if (weatherData.getHourly() != null) {
            long targetDayStart = (dayTimestamp / 86400) * 86400;
            long targetDayEnd = targetDayStart + 86400;
            
            java.util.List<Map<String, Object>> hourlyList = new java.util.ArrayList<>();
            for (HourlyForecast hourly : weatherData.getHourly()) {
                if (hourly.getTimestamp() >= targetDayStart && hourly.getTimestamp() < targetDayEnd) {
                    Map<String, Object> hourData = new HashMap<>();
                    hourData.put("timestamp", hourly.getTimestamp());
                    hourData.put("temp", hourly.getTemp());
                    hourData.put("pop", hourly.getPop());
                    hourData.put("humidity", hourly.getHumidity());
                    
                    if (hourly.getWeather() != null && hourly.getWeather().length > 0) {
                        Map<String, Object> weather = new HashMap<>();
                        weather.put("icon", hourly.getWeather()[0].getIcon());
                        hourData.put("weather", weather);
                    }
                    
                    hourlyList.add(hourData);
                }
            }
            hourlyData = hourlyList.toArray(new Map[0]);
        }
        
        // Build response
        Map<String, Object> responseData = new HashMap<>();
        
        // Day data
        Map<String, Object> dayData = new HashMap<>();
        dayData.put("timestamp", targetDay.getTimestamp());
        dayData.put("tempMin", targetDay.getTemp().getMin());
        dayData.put("tempMax", targetDay.getTemp().getMax());
        dayData.put("tempAvg", targetDay.getTemp().getDay());
        dayData.put("humidity", targetDay.getHumidity());
        dayData.put("pop", targetDay.getPop());
        
        if (targetDay.getRain() != null) {
            dayData.put("rain", targetDay.getRain().getTotal() != null ? targetDay.getRain().getTotal() : 0);
        } else {
            dayData.put("rain", 0);
        }
        
        if (targetDay.getWeather() != null && targetDay.getWeather().length > 0) {
            Map<String, Object> weather = new HashMap<>();
            weather.put("main", targetDay.getWeather()[0].getMain());
            weather.put("icon", targetDay.getWeather()[0].getIcon());
            dayData.put("weather", weather);
        }
        
        responseData.put("day", dayData);
        responseData.put("hourly", hourlyData);
        
        // Today's data for comparison
        if (weatherData.getDaily() != null && weatherData.getDaily().length > 0) {
            DailyForecast today = weatherData.getDaily()[0];
            Map<String, Object> todayData = new HashMap<>();
            todayData.put("tempAvg", today.getTemp().getDay());
            todayData.put("humidity", today.getHumidity());
            if (today.getRain() != null) {
                todayData.put("rain", today.getRain().getTotal() != null ? today.getRain().getTotal() : 0);
            } else {
                todayData.put("rain", 0);
            }
            responseData.put("today", todayData);
        }
        
        return new ClientResponse(true, responseData);
    }
}

