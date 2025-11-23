package com.weather.server.model;

public class ClientResponse {
    private boolean success;
    private String error;
    private Object data;

    public ClientResponse() {
    }

    public ClientResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public ClientResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

