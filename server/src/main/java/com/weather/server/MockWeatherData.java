package com.weather.server;

import com.weather.server.model.WeatherResponse;
import com.weather.server.model.CurrentWeather;
import com.weather.server.model.HourlyForecast;
import com.weather.server.model.DailyForecast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock weather data generator for fallback when API fails
 * Provides realistic weather data for different cities
 */
public class MockWeatherData {
    
    // City-specific weather profiles
    private static class CityProfile {
        double baseTemp;
        double tempRange;
        double humidity;
        String timezone;
        double lat;
        double lon;
        int timezoneOffset;
        
        CityProfile(double baseTemp, double tempRange, double humidity, String timezone, double lat, double lon, int offset) {
            this.baseTemp = baseTemp;
            this.tempRange = tempRange;
            this.humidity = humidity;
            this.timezone = timezone;
            this.lat = lat;
            this.lon = lon;
            this.timezoneOffset = offset;
        }
    }
    
    private static final Map<String, CityProfile> CITY_PROFILES = new HashMap<>();
    
    static {
        // Vietnam cities (tropical, hot and humid)
        CITY_PROFILES.put("hanoi", new CityProfile(28.0, 8.0, 75, "Asia/Ho_Chi_Minh", 21.0285, 105.8542, 25200));
        CITY_PROFILES.put("hà nội", new CityProfile(28.0, 8.0, 75, "Asia/Ho_Chi_Minh", 21.0285, 105.8542, 25200));
        CITY_PROFILES.put("ha noi", new CityProfile(28.0, 8.0, 75, "Asia/Ho_Chi_Minh", 21.0285, 105.8542, 25200));
        CITY_PROFILES.put("ho chi minh city", new CityProfile(30.0, 5.0, 80, "Asia/Ho_Chi_Minh", 10.8231, 106.6297, 25200));
        CITY_PROFILES.put("hồ chí minh", new CityProfile(30.0, 5.0, 80, "Asia/Ho_Chi_Minh", 10.8231, 106.6297, 25200));
        CITY_PROFILES.put("ho chi minh", new CityProfile(30.0, 5.0, 80, "Asia/Ho_Chi_Minh", 10.8231, 106.6297, 25200));
        CITY_PROFILES.put("da nang", new CityProfile(29.0, 6.0, 78, "Asia/Ho_Chi_Minh", 16.0544, 108.2022, 25200));
        CITY_PROFILES.put("đà nẵng", new CityProfile(29.0, 6.0, 78, "Asia/Ho_Chi_Minh", 16.0544, 108.2022, 25200));
        CITY_PROFILES.put("hue", new CityProfile(27.5, 7.0, 77, "Asia/Ho_Chi_Minh", 16.4637, 107.5909, 25200));
        CITY_PROFILES.put("can tho", new CityProfile(30.5, 5.0, 82, "Asia/Ho_Chi_Minh", 10.0452, 105.7469, 25200));
        
        // European cities (temperate)
        CITY_PROFILES.put("london", new CityProfile(15.0, 10.0, 65, "Europe/London", 51.5074, -0.1278, 0));
        CITY_PROFILES.put("paris", new CityProfile(18.0, 12.0, 60, "Europe/Paris", 48.8566, 2.3522, 3600));
        CITY_PROFILES.put("berlin", new CityProfile(16.0, 14.0, 58, "Europe/Berlin", 52.5200, 13.4050, 3600));
        CITY_PROFILES.put("madrid", new CityProfile(22.0, 15.0, 45, "Europe/Madrid", 40.4168, -3.7038, 3600));
        CITY_PROFILES.put("rome", new CityProfile(20.0, 12.0, 55, "Europe/Rome", 41.9028, 12.4964, 3600));
        CITY_PROFILES.put("amsterdam", new CityProfile(14.0, 10.0, 70, "Europe/Amsterdam", 52.3676, 4.9041, 3600));
        CITY_PROFILES.put("vienna", new CityProfile(17.0, 13.0, 62, "Europe/Vienna", 48.2082, 16.3738, 3600));
        
        // US cities
        CITY_PROFILES.put("new york", new CityProfile(20.0, 15.0, 55, "America/New_York", 40.7128, -74.0060, -18000));
        CITY_PROFILES.put("los angeles", new CityProfile(22.0, 8.0, 50, "America/Los_Angeles", 34.0522, -118.2437, -28800));
        
        // Asian cities
        CITY_PROFILES.put("tokyo", new CityProfile(22.0, 12.0, 65, "Asia/Tokyo", 35.6762, 139.6503, 32400));
        CITY_PROFILES.put("singapore", new CityProfile(30.0, 3.0, 85, "Asia/Singapore", 1.3521, 103.8198, 28800));
        CITY_PROFILES.put("bangkok", new CityProfile(32.0, 5.0, 75, "Asia/Bangkok", 13.7563, 100.5018, 25200));
        CITY_PROFILES.put("seoul", new CityProfile(19.0, 16.0, 60, "Asia/Seoul", 37.5665, 126.9780, 32400));
        CITY_PROFILES.put("hong kong", new CityProfile(26.0, 8.0, 72, "Asia/Hong_Kong", 22.3193, 114.1694, 28800));
        CITY_PROFILES.put("mumbai", new CityProfile(32.0, 5.0, 78, "Asia/Kolkata", 19.0760, 72.8777, 19800));
        CITY_PROFILES.put("delhi", new CityProfile(30.0, 12.0, 55, "Asia/Kolkata", 28.6139, 77.2090, 19800));
        CITY_PROFILES.put("shanghai", new CityProfile(24.0, 14.0, 68, "Asia/Shanghai", 31.2304, 121.4737, 28800));
        CITY_PROFILES.put("beijing", new CityProfile(21.0, 18.0, 50, "Asia/Shanghai", 39.9042, 116.4074, 28800));
        
        // Other cities
        CITY_PROFILES.put("sydney", new CityProfile(20.0, 10.0, 60, "Australia/Sydney", -33.8688, 151.2093, 36000));
        CITY_PROFILES.put("dubai", new CityProfile(35.0, 8.0, 45, "Asia/Dubai", 25.2048, 55.2708, 14400));
        CITY_PROFILES.put("moscow", new CityProfile(12.0, 15.0, 65, "Europe/Moscow", 55.7558, 37.6173, 10800));
    }
    
    /**
     * Generate mock weather data for a city
     */
    public static WeatherResponse generateMockData(String city) {
        // Get city profile or use default (Hanoi)
        String cityKey = city.toLowerCase().trim();
        CityProfile profile = CITY_PROFILES.get(cityKey);
        if (profile == null) {
            // Try to find partial match
            for (Map.Entry<String, CityProfile> entry : CITY_PROFILES.entrySet()) {
                if (cityKey.contains(entry.getKey()) || entry.getKey().contains(cityKey)) {
                    profile = entry.getValue();
                    break;
                }
            }
            // Default to Hanoi if still not found
            if (profile == null) {
                profile = CITY_PROFILES.get("hanoi");
            }
        }
        
        WeatherResponse response = new WeatherResponse();
        response.setLat(profile.lat);
        response.setLon(profile.lon);
        response.setTimezone(profile.timezone);
        response.setTimezoneOffset(profile.timezoneOffset);
        
        // Current time
        long currentTime = System.currentTimeMillis() / 1000;
        
        // Current weather - varies by city
        CurrentWeather current = new CurrentWeather();
        double currentTemp = profile.baseTemp + (Math.random() * profile.tempRange) - (profile.tempRange / 2);
        current.setTemp(currentTemp);
        current.setFeelsLike(currentTemp + 2 + (Math.random() * 3));
        current.setHumidity((int)(profile.humidity + (Math.random() * 15) - 7));
        current.setPressure(1010 + (Math.random() * 20));
        current.setUvi(5 + (Math.random() * 4));
        current.setVisibility(8000 + (int)(Math.random() * 5000));
        current.setWindSpeed(2 + (Math.random() * 5));
        current.setWindDeg((int)(Math.random() * 360));
        current.setWindGust(3 + (Math.random() * 4));
        current.setTimestamp(currentTime);
        
        // Weather condition based on temperature and humidity
        CurrentWeather.WeatherCondition currentWeather = new CurrentWeather.WeatherCondition();
        double rainChance = Math.random();
        if (rainChance > 0.7) {
            currentWeather.setMain("Rain");
            currentWeather.setDescription("moderate rain");
            currentWeather.setIcon("10d");
            currentWeather.setId(500);
        } else if (rainChance > 0.5) {
            currentWeather.setMain("Clouds");
            currentWeather.setDescription("few clouds");
            currentWeather.setIcon("02d");
            currentWeather.setId(801);
        } else {
            currentWeather.setMain("Clear");
            currentWeather.setDescription("clear sky");
            currentWeather.setIcon("01d");
            currentWeather.setId(800);
        }
        current.setWeather(new CurrentWeather.WeatherCondition[]{currentWeather});
        
        response.setCurrent(current);
        
        // Hourly forecast (48 hours) - full data with city-specific variations
        HourlyForecast[] hourly = new HourlyForecast[48];
        for (int i = 0; i < 48; i++) {
            HourlyForecast hour = new HourlyForecast();
            hour.setTimestamp(currentTime + (i * 3600));
            
            // Temperature varies throughout the day based on city profile
            int hourOfDay = (i % 24);
            double dayNightVariation = Math.sin((hourOfDay * Math.PI) / 12) * (profile.tempRange / 2);
            double hourTemp = profile.baseTemp + dayNightVariation + (Math.random() * 3) - 1.5;
            hour.setTemp(hourTemp);
            hour.setFeelsLike(hourTemp + 2 + (Math.random() * 2));
            hour.setHumidity((int)(profile.humidity + (Math.random() * 20) - 10));
            hour.setPressure(1010 + (Math.random() * 15));
            hour.setUvi((hourOfDay >= 6 && hourOfDay <= 18) ? 4 + (Math.random() * 4) : 0);
            hour.setWindSpeed(2 + (Math.random() * 5));
            hour.setWindDeg((int)(Math.random() * 360));
            
            // Rain probability - higher in afternoon for tropical cities
            double basePop = profile.humidity > 70 ? 0.2 : 0.1;
            hour.setPop((hourOfDay >= 12 && hourOfDay <= 20) ? 
                basePop + (Math.random() * 0.5) : basePop + (Math.random() * 0.2));
            
            // Weather condition based on rain probability and time
            HourlyForecast.WeatherCondition weather = new HourlyForecast.WeatherCondition();
            boolean isDay = hourOfDay >= 6 && hourOfDay <= 18;
            if (hour.getPop() > 0.6) {
                weather.setMain("Rain");
                weather.setDescription("moderate rain");
                weather.setIcon(isDay ? "10d" : "10n");
                weather.setId(500);
            } else if (hour.getPop() > 0.4) {
                weather.setMain("Clouds");
                weather.setDescription("scattered clouds");
                weather.setIcon(isDay ? "03d" : "03n");
                weather.setId(802);
            } else if (hour.getPop() > 0.2) {
                weather.setMain("Clouds");
                weather.setDescription("few clouds");
                weather.setIcon(isDay ? "02d" : "02n");
                weather.setId(801);
            } else {
                weather.setMain("Clear");
                weather.setDescription("clear sky");
                weather.setIcon(isDay ? "01d" : "01n");
                weather.setId(800);
            }
            hour.setWeather(new HourlyForecast.WeatherCondition[]{weather});
            
            hourly[i] = hour;
        }
        response.setHourly(hourly);
        
        // Daily forecast (7 days) - full data with city-specific variations
        DailyForecast[] daily = new DailyForecast[7];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.SECOND, 0);
        
        for (int i = 0; i < 7; i++) {
            DailyForecast day = new DailyForecast();
            if (i > 0) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            day.setTimestamp(cal.getTimeInMillis() / 1000);
            
            // Temperature range based on city profile
            double dayTemp = profile.baseTemp + (Math.random() * profile.tempRange) - (profile.tempRange / 2);
            double minTemp = dayTemp - (profile.tempRange / 2) - (Math.random() * 3);
            double maxTemp = dayTemp + (profile.tempRange / 2) + (Math.random() * 2);
            
            DailyForecast.Temp temp = new DailyForecast.Temp();
            temp.setDay(dayTemp);
            temp.setMin(minTemp);
            temp.setMax(maxTemp);
            temp.setNight(minTemp + (Math.random() * 2));
            temp.setEve(dayTemp - 2 - (Math.random() * 2));
            temp.setMorn(minTemp + 3 + (Math.random() * 2));
            day.setTemp(temp);
            
            // Feels like
            DailyForecast.FeelsLike feelsLike = new DailyForecast.FeelsLike();
            feelsLike.setDay(dayTemp + 2 + (Math.random() * 2));
            feelsLike.setNight(temp.getNight() - 1);
            feelsLike.setEve(temp.getEve() + 1);
            feelsLike.setMorn(temp.getMorn() + 1);
            day.setFeelsLike(feelsLike);
            
            day.setHumidity((int)(profile.humidity + (Math.random() * 20) - 10));
            day.setPressure(1010 + (Math.random() * 15));
            day.setUvi(4 + (Math.random() * 5));
            day.setWindSpeed(2 + (Math.random() * 5));
            day.setWindDeg((int)(Math.random() * 360));
            
            // Rain probability - varies by day and city humidity
            double basePop = profile.humidity > 70 ? 0.3 : 0.15;
            day.setPop((i == 2 || i == 4 || i == 6) ? 
                basePop + 0.3 + (Math.random() * 0.3) : basePop + (Math.random() * 0.3));
            
            // Weather condition
            DailyForecast.WeatherCondition weather = new DailyForecast.WeatherCondition();
            if (day.getPop() > 0.6) {
                weather.setMain("Rain");
                weather.setDescription("moderate rain");
                weather.setIcon("10d");
                weather.setId(500);
            } else if (day.getPop() > 0.4) {
                weather.setMain("Clouds");
                weather.setDescription("scattered clouds");
                weather.setIcon("03d");
                weather.setId(802);
            } else if (day.getPop() > 0.2 || i == 1 || i == 5) {
                weather.setMain("Clouds");
                weather.setDescription("few clouds");
                weather.setIcon("02d");
                weather.setId(801);
            } else {
                weather.setMain("Clear");
                weather.setDescription("clear sky");
                weather.setIcon("01d");
                weather.setId(800);
            }
            day.setWeather(new DailyForecast.WeatherCondition[]{weather});
            
            // Rain amount if significant
            if (day.getPop() > 0.5) {
                DailyForecast.Rain rain = new DailyForecast.Rain();
                rain.setTotal(1.5 + (Math.random() * 8));
                day.setRain(rain);
            }
            
            daily[i] = day;
        }
        response.setDaily(daily);
        
        return response;
    }
}

