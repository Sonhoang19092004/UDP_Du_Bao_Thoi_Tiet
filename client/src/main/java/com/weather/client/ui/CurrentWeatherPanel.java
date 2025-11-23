package com.weather.client.ui;

import com.weather.client.model.WeatherData;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentWeatherPanel extends JPanel {
    private static final Color CARD_BG_START = new Color(30, 41, 59, 250);
    private static final Color CARD_BG_END = new Color(40, 51, 69, 250);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final int CORNER_RADIUS = 24;
    
    private JLabel cityLabel;
    private JLabel tempLabel;
    private JLabel descriptionLabel;
    private JLabel tempRangeLabel;
    private JLabel windLabel;
    private JLabel humidityLabel;
    private JLabel locationLabel;
    private JLabel weatherIconLabel;
    private JLabel uvLabel;
    private JLabel visibilityLabel;
    private JLabel pressureLabel;
    private JLabel feelsLikeLabel;
    private JLabel timeLabel;

    public CurrentWeatherPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setOpaque(false);
        setDoubleBuffered(true);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Mobile compact spacing
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(25, 0));
        mainPanel.setOpaque(false);
        
        // Left side - Text info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        // Location indicator
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        locationPanel.setOpaque(false);
        // Use HTML to ensure emoji rendering on Windows
        locationLabel = new JLabel(FontUtil.getEmojiHtml("📍", 16));
        locationLabel.setFont(FontUtil.getEmojiFont(16));
        JLabel locationText = new JLabel("Vị trí của tôi");
        locationText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        locationText.setForeground(TEXT_SECONDARY);
        locationPanel.add(locationLabel);
        locationPanel.add(locationText);
        
        // Time label
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(TEXT_SECONDARY.getRed(), TEXT_SECONDARY.getGreen(), TEXT_SECONDARY.getBlue(), 120));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // City name - mobile size (ALWAYS VISIBLE AT TOP)
        cityLabel = new JLabel("Hà Nội");
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        cityLabel.setForeground(TEXT_PRIMARY);
        cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityLabel.setVisible(true); // Ensure always visible
        cityLabel.setOpaque(false);
        cityLabel.setHorizontalAlignment(SwingConstants.LEFT);
        // Ensure it has proper size
        cityLabel.setPreferredSize(new Dimension(0, 50));
        cityLabel.setMinimumSize(new Dimension(0, 50));
        cityLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Description - mobile size
        descriptionLabel = new JLabel("Trời quang mây");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        descriptionLabel.setForeground(new Color(TEXT_SECONDARY.getRed(), TEXT_SECONDARY.getGreen(), TEXT_SECONDARY.getBlue(), 220));
        
        // Temperature - mobile size
        tempLabel = new JLabel("18°") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Gradient text effect
                GradientPaint gradient = new GradientPaint(
                    0, 0, TEXT_PRIMARY,
                    0, getHeight(), new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), 220)
                );
                g2.setPaint(gradient);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 100)); // Mobile size
        tempLabel.setForeground(TEXT_PRIMARY);
        
        // Feels like - improved styling
        feelsLikeLabel = new JLabel("Cảm giác như: 18°");
        feelsLikeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        feelsLikeLabel.setForeground(new Color(TEXT_SECONDARY.getRed(), TEXT_SECONDARY.getGreen(), TEXT_SECONDARY.getBlue(), 200));
        
        // Temp range - improved layout
        JPanel tempRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tempRangePanel.setOpaque(false);
        JLabel maxIcon = new JLabel(FontUtil.getEmojiHtml("⬆️", 18));
        maxIcon.setFont(FontUtil.getEmojiFont(18));
        tempRangeLabel = new JLabel("Cao: 22°");
        tempRangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tempRangeLabel.setForeground(new Color(255, 180, 100));
        JLabel minIcon = new JLabel(FontUtil.getEmojiHtml("⬇️", 18));
        minIcon.setFont(FontUtil.getEmojiFont(18));
        JLabel minLabel = new JLabel("Thấp: 13°");
        minLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        minLabel.setForeground(new Color(150, 200, 255));
        tempRangePanel.add(maxIcon);
        tempRangePanel.add(tempRangeLabel);
        tempRangePanel.add(Box.createHorizontalStrut(25));
        tempRangePanel.add(minIcon);
        tempRangePanel.add(minLabel);
        
        // City name - MUST be at the top, ensure it's visible and properly aligned
        cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityLabel.setPreferredSize(new Dimension(0, 50)); // Ensure height
        cityLabel.setMinimumSize(new Dimension(0, 50));
        cityLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        leftPanel.add(cityLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Location and time below city
        leftPanel.add(locationPanel);
        leftPanel.add(timeLabel);
        leftPanel.add(Box.createVerticalStrut(15)); // Mobile compact
        
        // Description
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(descriptionLabel);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(tempLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(feelsLikeLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(tempRangePanel);
        leftPanel.add(Box.createVerticalStrut(20));
        
        // Right side - Weather icon (mobile compact)
        JPanel rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Gradient background for icon area
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 130, 246, 30),
                    width, height, new Color(59, 130, 246, 10)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, width, height, 20, 20);
                
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(150, 200)); // Mobile compact
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Use HTML to ensure emoji rendering on Windows
        weatherIconLabel = new JLabel(FontUtil.getEmojiHtml("☀️", 90));
        weatherIconLabel.setFont(FontUtil.getEmojiFont(90)); // Mobile size
        weatherIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(weatherIconLabel, BorderLayout.CENTER);
        
        // Bottom - Additional info grid (mobile 2x3 compact)
        JPanel infoPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create info cards with references
        InfoCardResult windCard = createInfoCardWithRef("💨", "Gió", "9 km/h");
        windLabel = windCard.valueLabel;
        infoPanel.add(windCard.panel);
        
        InfoCardResult humidityCard = createInfoCardWithRef("💧", "Độ ẩm", "65%");
        humidityLabel = humidityCard.valueLabel;
        infoPanel.add(humidityCard.panel);
        
        InfoCardResult uvCard = createInfoCardWithRef("☀️", "UV", "5");
        uvLabel = uvCard.valueLabel;
        infoPanel.add(uvCard.panel);
        
        InfoCardResult visibilityCard = createInfoCardWithRef("👁️", "Tầm nhìn", "10 km");
        visibilityLabel = visibilityCard.valueLabel;
        infoPanel.add(visibilityCard.panel);
        
        InfoCardResult pressureCard = createInfoCardWithRef("📊", "Áp suất", "1013 hPa");
        pressureLabel = pressureCard.valueLabel;
        infoPanel.add(pressureCard.panel);
        
        // Empty cell
        infoPanel.add(new JPanel());
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInfoCard(String icon, String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Custom paint for card
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                g2.setColor(new Color(40, 51, 69, 200));
                g2.fillRoundRect(0, 0, width, height, 16, 16);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml(icon, 24));
        iconLabel.setFont(FontUtil.getEmojiFont(24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(TEXT_SECONDARY);
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(labelLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        
        panel.add(card);
        
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Clear background first to prevent ghosting
        if (getParent() != null) {
            g2.setColor(getParent().getBackground());
        } else {
            g2.setColor(new Color(15, 23, 42));
        }
        g2.fillRect(0, 0, width, height);
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, CARD_BG_START,
            width, height, CARD_BG_END
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
        
        // Shadow effect
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(2, 2, width, height, CORNER_RADIUS, CORNER_RADIUS);
        
        // Border
        g2.setColor(new Color(59, 130, 246, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, width - 2, height - 2, CORNER_RADIUS, CORNER_RADIUS);
        
        g2.dispose();
    }

    public void updateData(WeatherData data) {
        if (data == null || data.getCurrent() == null) {
            System.err.println("WARNING: Cannot update - data or current weather is null");
            return;
        }
        
        WeatherData.CurrentWeather current = data.getCurrent();
        
        // Validate data
        if (Double.isNaN(current.getTemp()) || current.getTemp() < -50 || current.getTemp() > 60) {
            System.err.println("WARNING: Invalid temperature: " + current.getTemp());
            return;
        }
        
        // Update time - ensure label exists
        if (timeLabel != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            timeLabel.setText(FontUtil.getEmojiHtml("🕐 " + timeFormat.format(new Date()), 11));
            // Use emoji font for the clock icon
            Font timeFont = FontUtil.getEmojiFont(11);
            timeLabel.setFont(timeFont);
        }
        
        // Update city - ensure label exists and always show city name at TOP
        if (cityLabel != null) {
            String cityName = data.getCity();
            if (cityName == null || cityName.trim().isEmpty()) {
                cityName = "Hà Nội"; // Default fallback
            }
            cityLabel.setText(cityName);
            cityLabel.setVisible(true); // Ensure visible
            cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cityLabel.setHorizontalAlignment(SwingConstants.LEFT);
            // Ensure proper size
            cityLabel.setPreferredSize(new Dimension(0, 50));
            // Force repaint to ensure visibility
            SwingUtilities.invokeLater(() -> {
                cityLabel.revalidate();
                cityLabel.repaint();
                // Also repaint parent to ensure visibility
                if (cityLabel.getParent() != null) {
                    cityLabel.getParent().revalidate();
                    cityLabel.getParent().repaint();
                }
            });
        } else {
            System.err.println("ERROR: cityLabel is null!");
        }
        
        // Update temperature - ensure label exists
        if (tempLabel != null) {
            tempLabel.setText(String.format(Locale.US, "%.0f°", current.getTemp()));
        }
        
        // Update feels like - ensure label exists
        if (feelsLikeLabel != null) {
            feelsLikeLabel.setText(String.format(Locale.US, "Cảm giác như: %.0f°", current.getFeelsLike()));
        }
        
        // Update weather description and icon - ensure labels exist
        if (current.getWeather() != null) {
            if (descriptionLabel != null) {
                String description = translateWeatherDescription(current.getWeather().getDescription());
                descriptionLabel.setText(description);
            }
            if (weatherIconLabel != null) {
                String icon = getWeatherIcon(current.getWeather().getIcon());
                weatherIconLabel.setText(FontUtil.getEmojiHtml(icon, 90));
            }
        }
        
        if (current.getTempRange() != null) {
            double maxTemp = current.getTempRange().getMax();
            double minTemp = current.getTempRange().getMin();
            if (!Double.isNaN(maxTemp) && !Double.isNaN(minTemp)) {
                tempRangeLabel.setText(String.format(Locale.US, "Cao: %.0f°", maxTemp));
                JPanel tempRangePanel = (JPanel) tempRangeLabel.getParent();
                for (Component comp : tempRangePanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label != tempRangeLabel && label.getText() != null && label.getText().startsWith("Thấp:")) {
                            label.setText(String.format(Locale.US, "Thấp: %.0f°", minTemp));
                            break;
                        }
                    }
                }
            }
        }
        
        // Wind speed - ensure label exists
        if (windLabel != null) {
            if (current.getWindSpeed() >= 0) {
                double windKmh = current.getWindSpeed() * 3.6;
                if (current.getWindGust() != null && current.getWindGust() > 0) {
                    windLabel.setText(String.format(Locale.US, "%.0f km/h (giật: %.0f)", windKmh, current.getWindGust() * 3.6));
                } else {
                    windLabel.setText(String.format(Locale.US, "%.0f km/h", windKmh));
                }
            } else {
                windLabel.setText("N/A");
            }
        }
        
        // Humidity - ensure label exists
        if (humidityLabel != null) {
            if (current.getHumidity() >= 0 && current.getHumidity() <= 100) {
                humidityLabel.setText(String.format(Locale.US, "%d%%", current.getHumidity()));
            } else {
                humidityLabel.setText("N/A");
            }
        }
        
        // UV Index - ensure label exists
        if (uvLabel != null) {
            if (current.getUvi() >= 0) {
                String uvText = String.format(Locale.US, "%.0f", current.getUvi());
                uvLabel.setText(uvText);
                if (current.getUvi() >= 8) {
                    uvLabel.setForeground(new Color(255, 100, 100));
                } else if (current.getUvi() >= 6) {
                    uvLabel.setForeground(new Color(255, 180, 100));
                } else if (current.getUvi() >= 3) {
                    uvLabel.setForeground(new Color(255, 255, 100));
                } else {
                    uvLabel.setForeground(new Color(100, 255, 100));
                }
            } else {
                uvLabel.setText("N/A");
                uvLabel.setForeground(TEXT_SECONDARY);
            }
        }
        
        // Visibility - ensure label exists
        if (visibilityLabel != null) {
            if (current.getVisibility() > 0) {
                double visibilityKm = current.getVisibility() / 1000.0;
                visibilityLabel.setText(String.format(Locale.US, "%.1f km", visibilityKm));
            } else {
                visibilityLabel.setText("N/A");
            }
        }
        
        // Pressure - ensure label exists
        if (pressureLabel != null) {
            if (current.getPressure() > 0) {
                pressureLabel.setText(String.format(Locale.US, "%.0f hPa", current.getPressure()));
            } else {
                pressureLabel.setText("N/A");
            }
        }
    }

    private String translateWeatherDescription(String description) {
        if (description == null) return "";
        String desc = description.toLowerCase();
        if (desc.contains("clear")) return "Trời quang";
        if (desc.contains("cloud")) return "Trời quang mây";
        if (desc.contains("rain")) return "Mưa";
        if (desc.contains("drizzle")) return "Mưa phùn";
        if (desc.contains("thunderstorm")) return "Dông";
        if (desc.contains("snow")) return "Tuyết";
        if (desc.contains("mist") || desc.contains("fog")) return "Sương mù";
        return description;
    }
    
    private String getWeatherIcon(String iconCode) {
        if (iconCode == null) return "☀️";
        if (iconCode.contains("01d")) return "☀️";
        if (iconCode.contains("01n")) return "🌙";
        if (iconCode.contains("02d") || iconCode.contains("02n")) return "⛅";
        if (iconCode.contains("03") || iconCode.contains("04")) return "☁️";
        if (iconCode.contains("09") || iconCode.contains("10d")) return "🌧️";
        if (iconCode.contains("10n")) return "🌙🌧️";
        if (iconCode.contains("11")) return "⛈️";
        if (iconCode.contains("13")) return "❄️";
        if (iconCode.contains("50")) return "🌫️";
        return "☀️";
    }
    
    private InfoCardResult createInfoCardWithRef(String icon, String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Custom paint for card
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                g2.setColor(new Color(40, 51, 69, 200));
                g2.fillRoundRect(0, 0, width, height, 16, 16);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml(icon, 24));
        iconLabel.setFont(FontUtil.getEmojiFont(24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(TEXT_SECONDARY);
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(labelLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        
        panel.add(card);
        
        return new InfoCardResult(panel, valueLabel);
    }
    
    private static class InfoCardResult {
        JPanel panel;
        JLabel valueLabel;
        
        InfoCardResult(JPanel panel, JLabel valueLabel) {
            this.panel = panel;
            this.valueLabel = valueLabel;
        }
    }
}
