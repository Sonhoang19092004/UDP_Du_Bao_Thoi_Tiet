package com.weather.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.weather.server.model.ClientRequest;
import com.weather.server.model.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class WeatherServer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServer.class);
    
    private static final int DEFAULT_PORT = 8888;
    private static final int BUFFER_SIZE = 8192;
    
    private final int port;
    private final WeatherService weatherService;
    private final Gson gson;
    private DatagramSocket socket;
    private boolean running;

    public WeatherServer(int port, String apiKey) {
        this.port = port;
        this.weatherService = new WeatherService(apiKey);
        this.gson = new Gson();
    }

    public void start() throws SocketException {
        socket = new DatagramSocket(port);
        running = true;
        logger.info("Weather Server started on port {}", port);
        
        // Start receiving thread
        Thread receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(false);
        receiveThread.start();
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        logger.info("Weather Server stopped");
    }

    private void receiveLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // Handle request in separate thread to avoid blocking
                Thread handlerThread = new Thread(() -> handleRequest(packet));
                handlerThread.start();
                
            } catch (IOException e) {
                if (running) {
                    logger.error("Error receiving packet", e);
                }
            }
        }
    }

    private void handleRequest(DatagramPacket packet) {
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();
        
        try {
            String requestJson = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            logger.debug("Received request from {}:{} - {}", clientAddress, clientPort, requestJson);
            
            // Parse request
            ClientRequest request;
            try {
                request = gson.fromJson(requestJson, ClientRequest.class);
            } catch (JsonSyntaxException e) {
                logger.error("Invalid JSON request", e);
                sendErrorResponse(clientAddress, clientPort, "Invalid JSON format");
                return;
            }
            
            // Process request
            ClientResponse response = weatherService.processRequest(request);
            
            // Send response
            sendResponse(clientAddress, clientPort, response);
            
        } catch (Exception e) {
            logger.error("Error handling request", e);
            sendErrorResponse(clientAddress, clientPort, "Server error: " + e.getMessage());
        }
    }

    private void sendResponse(InetAddress clientAddress, int clientPort, ClientResponse response) {
        try {
            String responseJson = gson.toJson(response);
            byte[] responseData = responseJson.getBytes(StandardCharsets.UTF_8);
            
            // Split into chunks if too large
            int maxChunkSize = BUFFER_SIZE - 100; // Leave some margin
            if (responseData.length <= maxChunkSize) {
                // Single packet
                DatagramPacket responsePacket = new DatagramPacket(
                    responseData, responseData.length, clientAddress, clientPort
                );
                socket.send(responsePacket);
                logger.debug("Sent response to {}:{} ({} bytes)", clientAddress, clientPort, responseData.length);
            } else {
                // Multiple packets - send in chunks
                // For simplicity, we'll just send error if too large
                // In production, implement proper chunking protocol
                logger.warn("Response too large ({} bytes), truncating", responseData.length);
                byte[] truncated = new byte[maxChunkSize];
                System.arraycopy(responseData, 0, truncated, 0, maxChunkSize);
                DatagramPacket responsePacket = new DatagramPacket(
                    truncated, truncated.length, clientAddress, clientPort
                );
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            logger.error("Error sending response", e);
        }
    }

    private void sendErrorResponse(InetAddress clientAddress, int clientPort, String error) {
        ClientResponse response = new ClientResponse(false, error);
        sendResponse(clientAddress, clientPort, response);
    }

    public static void main(String[] args) {
        // Get API key from multiple sources (priority order)
        String apiKey = null;
        String apiKeySource = null;
        
        // 1. Try environment variable
        apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            apiKeySource = "environment variable OPENWEATHER_API_KEY";
        }
        
        // 2. Try system property
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getProperty("openweather.api.key");
            if (apiKey != null && !apiKey.isEmpty()) {
                apiKeySource = "system property openweather.api.key";
            }
        }
        
        // 3. Try .env file from project root (priority)
        if (apiKey == null || apiKey.isEmpty()) {
            try {
                // Try to find .env in project root
                // When running from server/ directory, root is one level up
                java.io.File[] envFiles = {
                    new java.io.File("../.env"),  // Root .env when running from server/
                    new java.io.File(".env"),     // Current directory .env
                    new java.io.File("../../.env") // Fallback
                };
                
                for (java.io.File envFile : envFiles) {
                    if (envFile.exists() && envFile.isFile()) {
                        logger.debug("Trying to load .env from: {}", envFile.getAbsolutePath());
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                                new java.io.FileReader(envFile))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                // Skip comments and empty lines
                                if (line.isEmpty() || line.startsWith("#")) {
                                    continue;
                                }
                                // Parse KEY=VALUE format
                                if (line.startsWith("OPENWEATHER_API_KEY=")) {
                                    apiKey = line.substring("OPENWEATHER_API_KEY=".length()).trim();
                                    // Remove quotes if present
                                    if ((apiKey.startsWith("\"") && apiKey.endsWith("\"")) ||
                                        (apiKey.startsWith("'") && apiKey.endsWith("'"))) {
                                        apiKey = apiKey.substring(1, apiKey.length() - 1);
                                    }
                                    // Skip placeholder values
                                    if (!apiKey.isEmpty() && !apiKey.equals("your-api-key") && !apiKey.equals("your-api-key-here")) {
                                        apiKeySource = ".env file (" + envFile.getAbsolutePath() + ")";
                                        logger.info("Successfully loaded API key from .env file");
                                        break;
                                    } else {
                                        apiKey = null; // Reset if placeholder
                                    }
                                }
                            }
                            if (apiKey != null && !apiKey.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Could not load .env file: {}", e.getMessage());
            }
        }
        
        // 4. Try config.properties file
        if (apiKey == null || apiKey.isEmpty()) {
            try {
                java.util.Properties props = new java.util.Properties();
                java.io.File configFile = new java.io.File("config.properties");
                if (!configFile.exists()) {
                    configFile = new java.io.File("../config.properties");
                }
                if (configFile.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(configFile)) {
                        props.load(fis);
                        apiKey = props.getProperty("openweather.api.key");
                        if (apiKey != null && !apiKey.trim().isEmpty()) {
                            apiKey = apiKey.trim();
                            apiKeySource = "config.properties file";
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not load config.properties: {}", e.getMessage());
            }
        }
        
        // 5. Use default (with warning)
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "5d7e7ce2d300b40536bbb7133b7c822f";
            apiKeySource = "default (⚠️ PLEASE SET YOUR API KEY!)";
            logger.warn("========================================");
            logger.warn("⚠️  USING DEFAULT API KEY!");
            logger.warn("⚠️  This may not work or have rate limits!");
            logger.warn("⚠️  Set OPENWEATHER_API_KEY environment variable");
            logger.warn("⚠️  Or create config.properties with: openweather.api.key=YOUR_KEY");
            logger.warn("========================================");
        }
        
        // Log API key info (masked for security)
        String maskedKey = apiKey.length() > 8 
            ? apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4)
            : "****";
        logger.info("API key loaded from: {}", apiKeySource);
        logger.info("API key (masked): {}", maskedKey);
        
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[0]);
                System.exit(1);
            }
        }
        
        WeatherServer server = new WeatherServer(port, apiKey);
        
        try {
            server.start();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.stop();
            }));
            
            logger.info("Server is running. Press Ctrl+C to stop.");
            
            // Keep main thread alive
            Thread.currentThread().join();
        } catch (SocketException e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.info("Server interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

