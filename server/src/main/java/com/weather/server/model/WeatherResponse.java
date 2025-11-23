package com.weather.server.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    private double lat;
    private double lon;
    private String timezone;
    
    @SerializedName("timezone_offset")
    private int timezoneOffset;
    
    private CurrentWeather current;
    private HourlyForecast[] hourly;
    private DailyForecast[] daily;

    // Getters and Setters
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

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
}

