package com.weather.client.ui;

import com.weather.client.model.WeatherData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyForecastPanel extends JPanel {
    private static final Color CARD_BG_START = new Color(30, 41, 59, 250);
    private static final Color CARD_BG_END = new Color(40, 51, 69, 250);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color HOVER_COLOR = new Color(51, 65, 85, 200);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final int CORNER_RADIUS = 24;
    
    private JPanel contentPanel;
    private SimpleDateFormat dayFormat;
    private DayClickListener dayClickListener;

    public DailyForecastPanel() {
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
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml("📅", 26));
        iconLabel.setFont(FontUtil.getEmojiFont(26));
        JLabel titleLabel = new JLabel("Dự báo 7 ngày");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        // Content panel - no nested scroll, let parent handle it
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setDoubleBuffered(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0)); // More compact
        
        // No nested scroll pane - parent will handle scrolling
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Ensure proper double buffering to prevent ghosting
        setDoubleBuffered(true);
        
        dayFormat = new SimpleDateFormat("E", Locale.getDefault());
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
        
        if (data == null || data.getDaily() == null || data.getDaily().length == 0) {
            JLabel emptyLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu dự báo 7 ngày", 15));
            emptyLabel.setFont(FontUtil.getEmojiFont(15));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            contentPanel.add(emptyLabel);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }
        
        // Show 7 days - filter out invalid data
        Date today = new Date();
        int validDays = 0;
        
        for (int i = 0; i < data.getDaily().length && validDays < 7; i++) {
            WeatherData.DailyForecast daily = data.getDaily()[i];
            if (daily != null && daily.getTimestamp() > 0 && 
                !Double.isNaN(daily.getTempMin()) && !Double.isNaN(daily.getTempMax())) {
                JPanel dayPanel = createDayPanel(daily, validDays == 0, today);
                contentPanel.add(dayPanel);
                if (validDays < 6 && i < data.getDaily().length - 1) {
                    contentPanel.add(Box.createVerticalStrut(12));
                }
                validDays++;
            }
        }
        
        if (validDays == 0) {
            JLabel emptyLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu hợp lệ", 15));
            emptyLabel.setFont(FontUtil.getEmojiFont(15));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createDayPanel(WeatherData.DailyForecast daily, boolean isToday, Date today) {
        // Mobile-style very compact layout
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15)); // More compact
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(0, 65)); // Smaller height
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        panel.setMinimumSize(new Dimension(0, 65));
        
        // Custom paint with proper double buffering
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                
                // Clear background first to prevent ghosting
                g2.setColor(getParent() != null ? getParent().getBackground() : new Color(15, 23, 42));
                g2.fillRect(0, 0, width, height);
                
                if (panel.getBackground().getAlpha() > 0) {
                    g2.setColor(panel.getBackground());
                    g2.fillRoundRect(0, 0, width, height, 16, 16);
                } else if (isToday) {
                    g2.setColor(new Color(59, 130, 246, 20));
                    g2.fillRoundRect(0, 0, width, height, 16, 16);
                }
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setDoubleBuffered(true);
        
        // Left side - Day name and icon (mobile compact)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        
        String dayName;
        if (isToday) {
            dayName = "Hôm nay";
        } else {
            Date date = new Date(daily.getTimestamp() * 1000);
            dayName = dayFormat.format(date);
        }
        
        JLabel dayLabel = new JLabel(dayName);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Smaller
        dayLabel.setForeground(isToday ? ACCENT : TEXT_PRIMARY);
        dayLabel.setPreferredSize(new Dimension(80, 25)); // More compact
        
        // Weather icon - more compact
        String icon = getWeatherIcon(daily.getWeather());
        JLabel iconLabel = new JLabel(FontUtil.getEmojiHtml(icon, 28));
        iconLabel.setFont(FontUtil.getEmojiFont(28)); // Smaller
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(40, 35)); // More compact
        
        leftPanel.add(dayLabel);
        leftPanel.add(iconLabel);
        
        // Center - Temperature bar (very compact)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8)); // More compact
        
        JPanel tempBarPanel = createTempBar(daily.getTempMin(), daily.getTempMax());
        centerPanel.add(tempBarPanel, BorderLayout.CENTER);
        
        // Right side - Temperature labels (mobile style)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        double minTemp = daily.getTempMin();
        double maxTemp = daily.getTempMax();
        if (Double.isNaN(minTemp)) minTemp = 0;
        if (Double.isNaN(maxTemp)) maxTemp = 0;
        
        final double finalMinTemp = minTemp;
        final double finalMaxTemp = maxTemp;
        
        // Rain probability if significant
        double pop = daily.getPop();
        if (Double.isNaN(pop)) pop = 0;
        
        if (pop > 0.3) {
            JLabel popLabel = new JLabel(FontUtil.getEmojiHtml("💧" + String.format(Locale.US, "%.0f%%", pop * 100), 13));
            popLabel.setFont(FontUtil.getEmojiFont(Font.BOLD, 13));
            popLabel.setForeground(ACCENT);
            rightPanel.add(popLabel);
            rightPanel.add(Box.createHorizontalStrut(8));
        }
        
        JLabel minLabel = new JLabel(String.format(Locale.US, "%.0f°", finalMinTemp));
        minLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Smaller
        minLabel.setForeground(new Color(TEXT_SECONDARY.getRed(), TEXT_SECONDARY.getGreen(), TEXT_SECONDARY.getBlue(), 180));
        
        JLabel maxLabel = new JLabel(String.format(Locale.US, "%.0f°", finalMaxTemp));
        maxLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Smaller
        maxLabel.setForeground(TEXT_PRIMARY);
        
        rightPanel.add(minLabel);
        rightPanel.add(Box.createHorizontalStrut(6)); // More compact
        rightPanel.add(maxLabel);
        
        card.add(leftPanel, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        panel.add(card, BorderLayout.CENTER);
        
        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(HOVER_COLOR);
                SwingUtilities.invokeLater(panel::repaint);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(0, 0, 0, 0));
                SwingUtilities.invokeLater(panel::repaint);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dayClickListener != null) {
                    dayClickListener.onDayClick(daily.getTimestamp());
                }
            }
        });
        
        return panel;
    }

    private JPanel createTempBar(double min, double max) {
        // Fix and validate values
        double finalMin = Double.isNaN(min) ? 0 : min;
        double finalMax = Double.isNaN(max) ? 0 : max;
        if (finalMin > finalMax) {
            double temp = finalMin;
            finalMin = finalMax;
            finalMax = temp;
        }
        
        final double normalizedMinValue = finalMin;
        final double normalizedMaxValue = finalMax;
        
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(150, 8)); // Compact mobile size
        panel.setMinimumSize(new Dimension(100, 8));
        
        // Draw temperature bar
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Normalize temperatures
                double tempMin = -10;
                double tempMax = 40;
                double normalizedMin = (normalizedMinValue - tempMin) / (tempMax - tempMin);
                double normalizedMax = (normalizedMaxValue - tempMin) / (tempMax - tempMin);
                
                int xMin = (int) (normalizedMin * width);
                int xMax = (int) (normalizedMax * width);
                int barWidth = Math.max(4, xMax - xMin);
                
                // Gradient from blue to yellow
                GradientPaint gradient = new GradientPaint(
                    xMin, 0, new Color(100, 150, 255),
                    xMax, 0, new Color(255, 200, 100)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(xMin, 0, barWidth, height, 5, 5);
                
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        
        panel.add(bar);
        return panel;
    }

    private String getWeatherIcon(WeatherData.WeatherCondition weather) {
        if (weather == null || weather.getIcon() == null) {
            return "☀️";
        }
        
        String icon = weather.getIcon();
        if (icon.contains("01d") || icon.contains("01n")) return "☀️";
        if (icon.contains("02d") || icon.contains("02n")) return "⛅";
        if (icon.contains("03") || icon.contains("04")) return "☁️";
        if (icon.contains("09") || icon.contains("10")) return "🌧️";
        if (icon.contains("11")) return "⛈️";
        if (icon.contains("13")) return "❄️";
        if (icon.contains("50")) return "🌫️";
        
        return "☀️";
    }

    public void setDayClickListener(DayClickListener listener) {
        this.dayClickListener = listener;
    }

    public interface DayClickListener {
        void onDayClick(long dayTimestamp);
    }
}
