package com.weather.client.ui;

import com.weather.client.model.WeatherData;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourlyForecastPanel extends JPanel {
    private static final Color CARD_BG_START = new Color(30, 41, 59, 250);
    private static final Color CARD_BG_END = new Color(40, 51, 69, 250);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final int CORNER_RADIUS = 24;
    
    private JPanel contentPanel;
    private SimpleDateFormat timeFormat;

    public HourlyForecastPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setOpaque(false);
        setDoubleBuffered(true);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22)); // Mobile compact
        
        // Title with icon - improved styling
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setOpaque(false);
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml("⏰", 26));
        iconLabel.setFont(FontUtil.getEmojiFont(26));
        JLabel titleLabel = new JLabel("Dự báo theo giờ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        // Content panel with horizontal scroll - improved spacing
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setDoubleBuffered(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Mobile compact
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setDoubleBuffered(true);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setDoubleBuffered(true);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100, 180);
                this.trackColor = new Color(0, 0, 0, 0);
            }
        });
        // Optimize scroll speed and enable mouse wheel
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(150);
        scrollPane.setWheelScrollingEnabled(true);
        
        // Enable mouse wheel scrolling for horizontal scroll
        scrollPane.addMouseWheelListener(e -> {
            if (e.isShiftDown() || e.getModifiersEx() == 0) {
                JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
                if (horizontal != null && horizontal.isVisible()) {
                    int amount = e.getWheelRotation() * horizontal.getUnitIncrement() * 3;
                    horizontal.setValue(horizontal.getValue() + amount);
                }
            }
        });
        
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
        
        // Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(2, 2, width, height, CORNER_RADIUS, CORNER_RADIUS);
        
        // Border
        g2.setColor(new Color(59, 130, 246, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, width - 2, height - 2, CORNER_RADIUS, CORNER_RADIUS);
        
        g2.dispose();
    }

    public void updateData(WeatherData data) {
        contentPanel.removeAll();
        
        if (data == null || data.getHourly() == null || data.getHourly().length == 0) {
            JLabel emptyLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu dự báo theo giờ", 15));
            emptyLabel.setFont(FontUtil.getEmojiFont(15));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            contentPanel.add(Box.createHorizontalGlue());
            contentPanel.add(emptyLabel);
            contentPanel.add(Box.createHorizontalGlue());
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }
        
        // Show only next 7 hours - filter out invalid data
        long now = System.currentTimeMillis() / 1000;
        
        int validHours = 0;
        int maxHours = 7; // 7 hours next
        for (int i = 0; i < data.getHourly().length && validHours < maxHours; i++) {
            WeatherData.HourlyForecast hourly = data.getHourly()[i];
            if (hourly != null && hourly.getTimestamp() > 0 && !Double.isNaN(hourly.getTemp())) {
                // Only show hours from now onwards
                if (hourly.getTimestamp() >= now) {
                    JPanel hourPanel = createHourPanel(hourly, validHours == 0);
                    contentPanel.add(hourPanel);
                    if (validHours > 0) {
                        contentPanel.add(Box.createHorizontalStrut(10)); // Very compact spacing
                    }
                    validHours++;
                }
            }
        }
        
        if (validHours == 0) {
            JLabel emptyLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu hợp lệ", 15));
            emptyLabel.setFont(FontUtil.getEmojiFont(15));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createHorizontalGlue());
            contentPanel.add(emptyLabel);
            contentPanel.add(Box.createHorizontalGlue());
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createHourPanel(WeatherData.HourlyForecast hourly, boolean isNow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14)); // Mobile compact
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setPreferredSize(new Dimension(70, 110)); // Mobile very compact size
        panel.setMaximumSize(new Dimension(70, 110));
        panel.setMinimumSize(new Dimension(70, 110));
        
        // Custom paint for card
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                if (isNow) {
                    // Highlight current hour
                    g2.setColor(new Color(59, 130, 246, 30));
                    g2.fillRoundRect(0, 0, width, height, 16, 16);
                    g2.setColor(new Color(59, 130, 246, 100));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, width - 2, height - 2, 16, 16);
                } else {
                    g2.setColor(new Color(40, 51, 69, 150));
                    g2.fillRoundRect(0, 0, width, height, 16, 16);
                }
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8)); // Mobile very compact
        
        // Time label - mobile compact
        String timeText;
        if (isNow) {
            timeText = "Bây giờ";
        } else {
            Date date = new Date(hourly.getTimestamp() * 1000);
            timeText = timeFormat.format(date);
        }
        
        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setFont(new Font("Segoe UI", isNow ? Font.BOLD : Font.PLAIN, 12)); // Mobile size
        timeLabel.setForeground(isNow ? ACCENT : TEXT_SECONDARY);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Weather icon - mobile very compact
        String icon = getWeatherIcon(hourly.getWeather());
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml(icon, 32));
        iconLabel.setFont(FontUtil.getEmojiFont(32)); // Mobile very compact
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Temperature - mobile very compact
        double temp = hourly.getTemp();
        if (Double.isNaN(temp)) temp = 0;
        JLabel tempLabel = new JLabel(String.format(Locale.US, "%.0f°", temp));
        tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Mobile very compact
        tempLabel.setForeground(TEXT_PRIMARY);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Rain probability - mobile compact
        JPanel popPanel = new JPanel();
        popPanel.setLayout(new BoxLayout(popPanel, BoxLayout.X_AXIS));
        popPanel.setOpaque(false);
        popPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        double pop = hourly.getPop();
        if (Double.isNaN(pop)) pop = 0;
        
        if (pop > 0.1) {
            JLabel popIcon = new JLabel(FontUtil.getEmojiHtml("💧", 11));
            popIcon.setFont(FontUtil.getEmojiFont(11)); // Mobile size
            JLabel popLabel = new JLabel(String.format(Locale.US, "%.0f%%", pop * 100));
            popLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Mobile size
            popLabel.setForeground(ACCENT);
            popPanel.add(popIcon);
            popPanel.add(Box.createHorizontalStrut(3));
            popPanel.add(popLabel);
        }
        
        card.add(timeLabel, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);
        card.add(tempLabel, BorderLayout.SOUTH);
        
        panel.add(card);
        if (hourly.getPop() > 0.1) {
            panel.add(Box.createVerticalStrut(5));
            panel.add(popPanel);
        }
        
        return panel;
    }

    private String getWeatherIcon(WeatherData.WeatherCondition weather) {
        if (weather == null || weather.getIcon() == null) {
            return "☀️";
        }
        
        String icon = weather.getIcon();
        if (icon.contains("01d")) return "☀️";
        if (icon.contains("01n")) return "🌙";
        if (icon.contains("02d") || icon.contains("02n")) return "⛅";
        if (icon.contains("03") || icon.contains("04")) return "☁️";
        if (icon.contains("09") || icon.contains("10d")) return "🌧️";
        if (icon.contains("10n")) return "🌙🌧️";
        if (icon.contains("11")) return "⛈️";
        if (icon.contains("13")) return "❄️";
        if (icon.contains("50")) return "🌫️";
        
        return "☀️";
    }
}
