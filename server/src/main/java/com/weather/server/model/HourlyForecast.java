package com.weather.server.model;

import com.google.gson.annotations.SerializedName;

public class HourlyForecast {
    @SerializedName("dt")
    private long timestamp;
    
    private double temp;
    
    @SerializedName("feels_like")
    private double feelsLike;
    
    private int humidity;
    private double pressure;
    private double uvi;
    
    @SerializedName("wind_speed")
    private double windSpeed;
    
    @SerializedName("wind_deg")
    private int windDeg;
    
    @SerializedName("wind_gust")
    private Double windGust;
    
    private WeatherCondition[] weather;
    
    private Clouds clouds;
    private Rain rain;
    private Snow snow;
    
    @SerializedName("pop")
    private double pop; // Probability of precipitation

    // Getters and Setters
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

    public double getUvi() {
        return uvi;
    }

    public void setUvi(double uvi) {
        this.uvi = uvi;
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

    public WeatherCondition[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherCondition[] weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public double getPop() {
        return pop;
    }

    public void setPop(double pop) {
        this.pop = pop;
    }

    public static class WeatherCondition {
        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

    public static class Clouds {
        private int all;

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }
    }

    public static class Rain {
        @SerializedName("1h")
        private Double oneHour;

        public Double getOneHour() {
            return oneHour;
        }

        public void setOneHour(Double oneHour) {
            this.oneHour = oneHour;
        }
    }

    public static class Snow {
        @SerializedName("1h")
        private Double oneHour;

        public Double getOneHour() {
            return oneHour;
        }

        public void setOneHour(Double oneHour) {
            this.oneHour = oneHour;
        }
    }
}

