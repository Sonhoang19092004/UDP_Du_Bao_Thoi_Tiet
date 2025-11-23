package com.weather.client.model;

import java.util.Map;

public class WeatherData {
    private CurrentWeather current;
    private HourlyForecast[] hourly;
    private DailyForecast[] daily;
    private String city;
    private String timezone;

    public CurrentWeather getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeather current) {
        this.current = current;
    }

    public HourlyForecast[] getHourly() {
        return hourly;
    }

    public void setHourly(HourlyForecast[] hourly) {
        this.hourly = hourly;
    }

    public DailyForecast[] getDaily() {
        return daily;
    }

    public void setDaily(DailyForecast[] daily) {
        this.daily = daily;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public static class CurrentWeather {
        private double temp;
        private double feelsLike;
        private int humidity;
        private double pressure;
        private double windSpeed;
        private int windDeg;
        private Double windGust;
        private long timestamp;
        private WeatherCondition weather;
        private TempRange tempRange;
        private double uvi;
        private int visibility;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(double feelsLike) {
            this.feelsLike = feelsLike;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }

        public int getWindDeg() {
            return windDeg;
        }

        public void setWindDeg(int windDeg) {
            this.windDeg = windDeg;
        }

        public Double getWindGust() {
            return windGust;
        }

        public void setWindGust(Double windGust) {
            this.windGust = windGust;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public WeatherCondition getWeather() {
            return weather;
        }

        public void setWeather(WeatherCondition weather) {
            this.weather = weather;
        }

        public TempRange getTempRange() {
            return tempRange;
        }

        public void setTempRange(TempRange tempRange) {
            this.tempRange = tempRange;
        }

        public double getUvi() {
            return uvi;
        }

        public void setUvi(double uvi) {
            this.uvi = uvi;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }
    }

    public static class HourlyForecast {
        private long timestamp;
        private double temp;
        private double pop;
        private WeatherCondition weather;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getPop() {
            return pop;
        }

        public void setPop(double pop) {
            this.pop = pop;
        }

        public WeatherCondition getWeather() {
            return weather;
        }

        public void setWeather(WeatherCondition weather) {
            this.weather = weather;
        }
    }

    public static class DailyForecast {
        private long timestamp;
        private double tempMin;
        private double tempMax;
        private double pop;
        private int humidity;
        private double rain;
        private WeatherCondition weather;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public double getTempMin() {
            return tempMin;
        }

        public void setTempMin(double tempMin) {
            this.tempMin = tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        public void setTempMax(double tempMax) {
            this.tempMax = tempMax;
        }

        public double getPop() {
            return pop;
        }

        public void setPop(double pop) {
            this.pop = pop;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getRain() {
            return rain;
        }

        public void setRain(double rain) {
            this.rain = rain;
        }

        public WeatherCondition getWeather() {
            return weather;
        }

        public void setWeather(WeatherCondition weather) {
            this.weather = weather;
        }
    }

    public static class WeatherCondition {
        private String main;
        private String description;
        private String icon;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class TempRange {
        private double min;
        private double max;

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }
    }
}

