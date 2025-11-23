package com.weather.client.model;

public class DayDetailData {
    private DayData day;
    private HourlyData[] hourly;
    private TodayData today;

    public DayData getDay() {
        return day;
    }

    public void setDay(DayData day) {
        this.day = day;
    }

    public HourlyData[] getHourly() {
        return hourly;
    }

    public void setHourly(HourlyData[] hourly) {
        this.hourly = hourly;
    }

    public TodayData getToday() {
        return today;
    }

    public void setToday(TodayData today) {
        this.today = today;
    }

    public static class DayData {
        private long timestamp;
        private double tempMin;
        private double tempMax;
        private double tempAvg;
        private int humidity;
        private double pop;
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

        public double getTempAvg() {
            return tempAvg;
        }

        public void setTempAvg(double tempAvg) {
            this.tempAvg = tempAvg;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getPop() {
            return pop;
        }

        public void setPop(double pop) {
            this.pop = pop;
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

    public static class HourlyData {
        private long timestamp;
        private double temp;
        private double pop;
        private int humidity;
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

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public WeatherCondition getWeather() {
            return weather;
        }

        public void setWeather(WeatherCondition weather) {
            this.weather = weather;
        }
    }

    public static class TodayData {
        private double tempAvg;
        private int humidity;
        private double rain;

        public double getTempAvg() {
            return tempAvg;
        }

        public void setTempAvg(double tempAvg) {
            this.tempAvg = tempAvg;
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
    }

    public static class WeatherCondition {
        private String main;
        private String icon;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}

