package com.weather.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.weather.server.model.WeatherResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WeatherAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherAPIClient.class);
    
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String GEOCODING_URL = "http://api.openweathermap.org/geo/1.0/direct";
    
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public WeatherAPIClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Get coordinates for a city name
     */
    public double[] getCityCoordinates(String cityName) throws IOException {
        String url = GEOCODING_URL + "?q=" + cityName + "&limit=1&appid=" + apiKey;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("Geocoding API error: {} - {}", response.code(), errorBody);
                
                // Better error messages
                if (response.code() == 401) {
                    throw new IOException("Invalid API key. Please check your OpenWeatherMap API key.");
                } else if (response.code() == 429) {
                    throw new IOException("API rate limit exceeded. Please try again later.");
                } else {
                    throw new IOException("Geocoding API error (" + response.code() + "): " + errorBody);
                }
            }

            String responseBody = response.body().string();
            if (responseBody == null || responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                throw new IOException("City not found: " + cityName);
            }

            // Parse JSON array
            GeocodingResponse[] results = gson.fromJson(responseBody, GeocodingResponse[].class);
            if (results == null || results.length == 0) {
                throw new IOException("City not found: " + cityName);
            }

            return new double[]{results[0].lat, results[0].lon};
        } catch (JsonSyntaxException e) {
            logger.error("Error parsing geocoding response", e);
            throw new IOException("Invalid response from geocoding API", e);
        }
    }

    /**
     * Get weather data for coordinates
     */
    public WeatherResponse getWeatherData(double lat, double lon) throws IOException {
        String url = BASE_URL + "/onecall?lat=" + lat + "&lon=" + lon + 
                     "&exclude=minutely,alerts&units=metric&appid=" + apiKey;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("Weather API error: {} - {}", response.code(), errorBody);
                
                // Better error messages
                if (response.code() == 401) {
                    throw new IOException("Invalid API key. Please check your OpenWeatherMap API key.");
                } else if (response.code() == 429) {
                    throw new IOException("API rate limit exceeded. Please try again later.");
                } else if (response.code() == 404) {
                    throw new IOException("Weather data not found for this location.");
                } else {
                    throw new IOException("Weather API error (" + response.code() + "): " + errorBody);
                }
            }

            String responseBody = response.body().string();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new IOException("Empty response from weather API");
            }

            try {
                return gson.fromJson(responseBody, WeatherResponse.class);
            } catch (JsonSyntaxException e) {
                logger.error("Error parsing weather response", e);
                throw new IOException("Invalid response from weather API", e);
            }
        }
    }

    /**
     * Get weather data for a city name
     */
    public WeatherResponse getWeatherData(String cityName) throws IOException {
        double[] coordinates = getCityCoordinates(cityName);
        return getWeatherData(coordinates[0], coordinates[1]);
    }

    /**
     * Geocoding response model
     */
    private static class GeocodingResponse {
        String name;
        double lat;
        double lon;
        String country;
    }
}

