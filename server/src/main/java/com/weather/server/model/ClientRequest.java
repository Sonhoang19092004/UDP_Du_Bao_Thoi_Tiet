package com.weather.server.model;

public class ClientRequest {
    private String type; // "CURRENT", "DETAIL_DAY"
    private String city;
    private Long dayTimestamp; // For detail day request

    public ClientRequest() {
    }

    public ClientRequest(String type, String city) {
        this.type = type;
        this.city = city;
    }

    public ClientRequest(String type, String city, Long dayTimestamp) {
        this.type = type;
        this.city = city;
        this.dayTimestamp = dayTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getDayTimestamp() {
        return dayTimestamp;
    }

    public void setDayTimestamp(Long dayTimestamp) {
        this.dayTimestamp = dayTimestamp;
    }
}

