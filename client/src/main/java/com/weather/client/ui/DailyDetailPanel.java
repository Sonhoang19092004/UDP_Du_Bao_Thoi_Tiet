package com.weather.client.ui;

import com.weather.client.model.DayDetailData;
import com.weather.client.model.WeatherData;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyDetailPanel extends JPanel {
    private static final Color DARK_BG = new Color(20, 30, 48);
    private static final Color CARD_BG = new Color(30, 40, 58, 200);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    
    private JButton backButton;
    private JLabel dayTitleLabel;
    private ChartPanel chartPanel;
    private JPanel rainPanel;
    private JPanel comparisonPanel;
    
    private BackListener backListener;

    public DailyDetailPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(DARK_BG);
        setDoubleBuffered(true);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 0, 0, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        backButton = new JButton("← Quay lại");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setForeground(TEXT_PRIMARY);
        backButton.setBackground(new Color(60, 70, 88));
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            if (backListener != null) {
                backListener.onBack();
            }
        });
        
        dayTitleLabel = new JLabel("Chi tiết ngày");
        dayTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dayTitleLabel.setForeground(TEXT_PRIMARY);
        dayTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(dayTitleLabel, BorderLayout.CENTER);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DARK_BG);
        contentPanel.setDoubleBuffered(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 88), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Rain probability panel
        rainPanel = new JPanel();
        rainPanel.setLayout(new BoxLayout(rainPanel, BoxLayout.Y_AXIS));
        rainPanel.setBackground(CARD_BG);
        rainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 88), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel rainTitle = new JLabel("Khả năng mưa theo giờ");
        rainTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rainTitle.setForeground(TEXT_PRIMARY);
        rainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rainPanel.add(rainTitle);
        rainPanel.add(Box.createVerticalStrut(15));
        
        // Comparison panel
        comparisonPanel = new JPanel();
        comparisonPanel.setLayout(new BoxLayout(comparisonPanel, BoxLayout.Y_AXIS));
        comparisonPanel.setBackground(CARD_BG);
        comparisonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 88), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel comparisonTitle = new JLabel("So sánh với hôm nay");
        comparisonTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        comparisonTitle.setForeground(TEXT_PRIMARY);
        comparisonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        comparisonPanel.add(comparisonTitle);
        comparisonPanel.add(Box.createVerticalStrut(15));
        
        contentPanel.add(chartPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(rainPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(comparisonPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setDoubleBuffered(true);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getViewport().setDoubleBuffered(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = DARK_BG;
            }
        });
        // Optimize scroll speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(80);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Thin scrollbar
        
        // Fix mouse wheel scrolling - prevent conflicts
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.addMouseWheelListener(e -> {
            // Let the scroll pane handle wheel events normally
            // Don't interfere with default behavior
        });
        
        // Disable mouse wheel on child components to prevent conflicts
        disableMouseWheelOnChildren(scrollPane);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void disableMouseWheelOnChildren(Container container) {
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (comp instanceof Container) {
                Container cont = (Container) comp;
                // Remove any custom mouse wheel listeners that might conflict
                java.awt.event.MouseWheelListener[] listeners = cont.getMouseWheelListeners();
                for (java.awt.event.MouseWheelListener listener : listeners) {
                    if (listener != null && listener.getClass().getName().contains("ChartPanel")) {
                        cont.removeMouseWheelListener(listener);
                    }
                }
                disableMouseWheelOnChildren(cont);
            }
        }
    }

    public void updateData(DayDetailData detailData, WeatherData currentData) {
        if (detailData == null || detailData.getDay() == null) {
            return;
        }
        
        DayDetailData.DayData day = detailData.getDay();
        
        // Update title - validate timestamp
        if (dayTitleLabel != null && day.getTimestamp() > 0) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("vi", "VN"));
                Date date = new Date(day.getTimestamp() * 1000);
                dayTitleLabel.setText(dateFormat.format(date));
                dayTitleLabel.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error formatting date: " + e.getMessage());
                dayTitleLabel.setText("Chi tiết ngày");
            }
        }
        
        // Update chart - ensure data is valid
        if (detailData.getHourly() != null && detailData.getHourly().length > 0) {
            chartPanel.updateData(detailData.getHourly());
        }
        
        // Update rain panel - ensure data is valid
        if (detailData.getHourly() != null && detailData.getHourly().length > 0) {
            updateRainPanel(detailData.getHourly());
        } else {
            updateRainPanel(null);
        }
        
        // Update comparison panel
        if (detailData.getToday() != null) {
            updateComparisonPanel(day, detailData.getToday());
        } else {
            // Clear comparison if no data
            comparisonPanel.removeAll();
            JLabel comparisonTitle = new JLabel("So sánh với hôm nay");
            comparisonTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            comparisonTitle.setForeground(TEXT_PRIMARY);
            comparisonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            comparisonPanel.add(comparisonTitle);
            comparisonPanel.add(Box.createVerticalStrut(15));
            JLabel noDataLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu so sánh", 14));
            noDataLabel.setFont(FontUtil.getEmojiFont(14));
            noDataLabel.setForeground(TEXT_SECONDARY);
            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            comparisonPanel.add(noDataLabel);
            comparisonPanel.revalidate();
            comparisonPanel.repaint();
        }
    }

    private void updateRainPanel(DayDetailData.HourlyData[] hourly) {
        // Remove all except title (first 2 components: title and strut)
        int componentCount = rainPanel.getComponentCount();
        for (int i = componentCount - 1; i >= 2; i--) {
            rainPanel.remove(i);
        }
        
        if (hourly != null && hourly.length > 0) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            
            for (int i = 0; i < hourly.length; i++) {
                DayDetailData.HourlyData hour = hourly[i];
                if (hour != null && hour.getTimestamp() > 0) {
                    JPanel hourRainPanel = createRainHourPanel(hour, timeFormat);
                    rainPanel.add(hourRainPanel);
                    if (i < hourly.length - 1) {
                        rainPanel.add(Box.createVerticalStrut(8)); // Compact spacing
                    }
                }
            }
        } else {
            JLabel noDataLabel = new JLabel(FontUtil.getEmojiHtml("📭 Không có dữ liệu", 14));
            noDataLabel.setFont(FontUtil.getEmojiFont(14));
            noDataLabel.setForeground(TEXT_SECONDARY);
            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rainPanel.add(noDataLabel);
        }
        
        rainPanel.revalidate();
        rainPanel.repaint();
    }

    private JPanel createRainHourPanel(DayDetailData.HourlyData hour, SimpleDateFormat timeFormat) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        // Time - validate timestamp
        JLabel timeLabel;
        if (hour.getTimestamp() > 0) {
            try {
                Date date = new Date(hour.getTimestamp() * 1000);
                timeLabel = new JLabel(timeFormat.format(date));
            } catch (Exception e) {
                timeLabel = new JLabel("--:--");
            }
        } else {
            timeLabel = new JLabel("--:--");
        }
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(TEXT_SECONDARY);
        timeLabel.setPreferredSize(new Dimension(60, 20));
        
        // Rain bar
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(new Color(0, 0, 0, 0));
        
        JPanel bar = new JPanel() {
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
                    g2.setColor(DARK_BG);
                }
                g2.fillRect(0, 0, width, height);
                
                // Validate pop value
                double pop = hour != null ? hour.getPop() : 0.0;
                if (Double.isNaN(pop) || Double.isInfinite(pop)) pop = 0.0;
                if (pop < 0) pop = 0;
                if (pop > 1) pop = 1;
                
                // Calculate bar width - ensure it's within bounds
                int maxBarWidth = Math.max(0, width - 2); // Leave 1px margin on each side
                int barWidth = Math.max(0, Math.min(maxBarWidth, (int) (maxBarWidth * pop)));
                
                if (barWidth > 0) {
                    // Gradient from light blue to dark blue
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(100, 150, 255, 200),
                        barWidth, 0, new Color(50, 100, 200, 200)
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(1, 1, barWidth, height - 2, 3, 3);
                }
                
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(200, 20));
        
        // Percentage label
        JLabel percentLabel = new JLabel(String.format(Locale.US, "%.0f%%", hour.getPop() * 100));
        percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        percentLabel.setForeground(TEXT_PRIMARY);
        percentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        percentLabel.setPreferredSize(new Dimension(50, 20));
        
        barContainer.add(bar, BorderLayout.CENTER);
        
        panel.add(timeLabel, BorderLayout.WEST);
        panel.add(barContainer, BorderLayout.CENTER);
        panel.add(percentLabel, BorderLayout.EAST);
        
        return panel;
    }

    private void updateComparisonPanel(DayDetailData.DayData day, DayDetailData.TodayData today) {
        comparisonPanel.removeAll();
        
        JLabel comparisonTitle = new JLabel("So sánh với hôm nay");
        comparisonTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        comparisonTitle.setForeground(TEXT_PRIMARY);
        comparisonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        comparisonPanel.add(comparisonTitle);
        comparisonPanel.add(Box.createVerticalStrut(15));
        
        // Temperature comparison
        double tempDiff = day.getTempAvg() - today.getTempAvg();
        String tempDiffText = String.format(Locale.US, "%.1f°C", Math.abs(tempDiff));
        if (tempDiff > 0) {
            tempDiffText = "+" + tempDiffText;
        } else if (tempDiff < 0) {
            tempDiffText = "-" + tempDiffText;
        } else {
            tempDiffText = "0°C";
        }
        
        JPanel tempCompPanel = createComparisonItem(
            "Nhiệt độ trung bình",
            String.format(Locale.US, "%.1f°C", day.getTempAvg()),
            String.format(Locale.US, "%.1f°C", today.getTempAvg()),
            tempDiffText,
            tempDiff > 0 ? new Color(255, 150, 100) : new Color(100, 150, 255)
        );
        comparisonPanel.add(tempCompPanel);
        comparisonPanel.add(Box.createVerticalStrut(15));
        
        // Humidity comparison
        int humidityDiff = day.getHumidity() - today.getHumidity();
        String humidityDiffText = String.format(Locale.US, "%d%%", Math.abs(humidityDiff));
        if (humidityDiff > 0) {
            humidityDiffText = "+" + humidityDiffText;
        } else if (humidityDiff < 0) {
            humidityDiffText = "-" + humidityDiffText;
        } else {
            humidityDiffText = "0%";
        }
        
        JPanel humidityCompPanel = createComparisonItem(
            "Độ ẩm",
            String.format(Locale.US, "%d%%", day.getHumidity()),
            String.format(Locale.US, "%d%%", today.getHumidity()),
            humidityDiffText,
            humidityDiff > 0 ? new Color(100, 150, 255) : new Color(255, 200, 100)
        );
        comparisonPanel.add(humidityCompPanel);
        comparisonPanel.add(Box.createVerticalStrut(15));
        
        // Rain comparison
        double rainDiff = day.getRain() - today.getRain();
        String rainDiffText = String.format(Locale.US, "%.1f mm", Math.abs(rainDiff));
        if (rainDiff > 0) {
            rainDiffText = "+" + rainDiffText;
        } else if (rainDiff < 0) {
            rainDiffText = "-" + rainDiffText;
        } else {
            rainDiffText = "0 mm";
        }
        
        JPanel rainCompPanel = createComparisonItem(
            "Lượng mưa",
            String.format(Locale.US, "%.1f mm", day.getRain()),
            String.format(Locale.US, "%.1f mm", today.getRain()),
            rainDiffText,
            rainDiff > 0 ? new Color(100, 150, 255) : new Color(200, 200, 200)
        );
        comparisonPanel.add(rainCompPanel);
        
        SwingUtilities.invokeLater(() -> {
            comparisonPanel.revalidate();
            comparisonPanel.repaint();
        });
    }

    private JPanel createComparisonItem(String label, String dayValue, String todayValue, String diff, Color diffColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(40, 50, 68));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left side - label
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelLabel.setForeground(TEXT_SECONDARY);
        
        // Center - values
        JPanel valuesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        valuesPanel.setBackground(new Color(0, 0, 0, 0));
        
        JLabel dayValueLabel = new JLabel(dayValue);
        dayValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dayValueLabel.setForeground(TEXT_PRIMARY);
        
        JLabel vsLabel = new JLabel("vs");
        vsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vsLabel.setForeground(TEXT_SECONDARY);
        
        JLabel todayValueLabel = new JLabel(todayValue);
        todayValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        todayValueLabel.setForeground(TEXT_PRIMARY);
        
        valuesPanel.add(dayValueLabel);
        valuesPanel.add(vsLabel);
        valuesPanel.add(todayValueLabel);
        
        // Right side - difference
        JLabel diffLabel = new JLabel(diff);
        diffLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        diffLabel.setForeground(diffColor);
        diffLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(labelLabel, BorderLayout.WEST);
        panel.add(valuesPanel, BorderLayout.CENTER);
        panel.add(diffLabel, BorderLayout.EAST);
        
        return panel;
    }

    public void setBackListener(BackListener listener) {
        this.backListener = listener;
    }

    public interface BackListener {
        void onBack();
    }
}

