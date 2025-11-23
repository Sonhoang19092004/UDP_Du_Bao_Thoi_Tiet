package com.weather.client.ui;

import com.weather.client.model.DayDetailData;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChartPanel extends JPanel {
    private static final Color CARD_BG = new Color(30, 40, 58, 200);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    private static final Color LINE_COLOR = new Color(100, 150, 255);
    private static final Color GRID_COLOR = new Color(60, 70, 88);
    private static final Color POINT_COLOR = new Color(255, 200, 100);
    
    private DayDetailData.HourlyData[] hourlyData;
    private SimpleDateFormat timeFormat;

    public ChartPanel() {
        setBackground(CARD_BG);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(400, 300));
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // Don't consume mouse wheel events - let parent scroll pane handle them
        setFocusable(false);
    }

    public void updateData(DayDetailData.HourlyData[] hourly) {
        // Validate and filter data
        if (hourly == null || hourly.length == 0) {
            this.hourlyData = null;
            repaint();
            return;
        }
        
        // Filter out invalid data
        java.util.List<DayDetailData.HourlyData> validData = new java.util.ArrayList<>();
        for (DayDetailData.HourlyData hour : hourly) {
            if (hour != null && hour.getTimestamp() > 0 && !Double.isNaN(hour.getTemp())) {
                validData.add(hour);
            }
        }
        
        if (validData.isEmpty()) {
            this.hourlyData = null;
        } else {
            this.hourlyData = validData.toArray(new DayDetailData.HourlyData[0]);
        }
        
        repaint(); // Direct repaint, no invokeLater to prevent ghosting
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (hourlyData == null || hourlyData.length == 0) {
            drawNoData(g);
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(TEXT_PRIMARY);
        g2.drawString("Nhiệt độ theo giờ", padding, 25);
        
        // Find min and max temperatures - validate data
        double minTemp = Double.MAX_VALUE;
        double maxTemp = Double.MIN_VALUE;
        for (DayDetailData.HourlyData hour : hourlyData) {
            if (hour != null && !Double.isNaN(hour.getTemp()) && !Double.isInfinite(hour.getTemp())) {
                if (hour.getTemp() < minTemp) minTemp = hour.getTemp();
                if (hour.getTemp() > maxTemp) maxTemp = hour.getTemp();
            }
        }
        
        // Validate range
        if (minTemp == Double.MAX_VALUE || maxTemp == Double.MIN_VALUE || minTemp > maxTemp) {
            drawNoData(g);
            return;
        }
        
        // Add some padding to the range
        double tempRange = maxTemp - minTemp;
        if (tempRange < 5) {
            double paddingValue = (5 - tempRange) / 2;
            minTemp -= paddingValue;
            maxTemp += paddingValue;
            tempRange = maxTemp - minTemp;
        } else {
            minTemp -= tempRange * 0.1;
            maxTemp += tempRange * 0.1;
            tempRange = maxTemp - minTemp;
        }
        
        // Draw grid lines
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5}, 0));
        
        int numGridLines = 5;
        for (int i = 0; i <= numGridLines; i++) {
            double temp = minTemp + (tempRange * i / numGridLines);
            int y = padding + chartHeight - (int) ((temp - minTemp) / tempRange * chartHeight);
            g2.drawLine(padding, y, padding + chartWidth, y);
            
            // Draw temperature label
            g2.setColor(TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String tempLabel = String.format(Locale.US, "%.0f°", temp);
            g2.drawString(tempLabel, padding - 35, y + 4);
            g2.setColor(GRID_COLOR);
        }
        
        // Draw time labels on X-axis - validate data
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        int numPoints = hourlyData.length;
        if (numPoints == 0) {
            drawNoData(g);
            return;
        }
        
        for (int i = 0; i < numPoints; i++) {
            DayDetailData.HourlyData hour = hourlyData[i];
            if (hour != null && hour.getTimestamp() > 0) {
                if (i % (Math.max(1, numPoints / 6)) == 0 || i == numPoints - 1) {
                    int x = padding + (int) ((double) i / Math.max(1, (numPoints - 1)) * chartWidth);
                    try {
                        Date date = new Date(hour.getTimestamp() * 1000);
                        String timeLabel = timeFormat.format(date);
                        FontMetrics fm = g2.getFontMetrics();
                        int labelWidth = fm.stringWidth(timeLabel);
                        g2.drawString(timeLabel, x - labelWidth / 2, height - padding + 20);
                    } catch (Exception e) {
                        // Skip invalid timestamp
                    }
                }
            }
        }
        
        // Draw temperature line - validate data
        if (numPoints > 1) {
            g2.setColor(LINE_COLOR);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            java.util.List<Integer> validXPoints = new java.util.ArrayList<>();
            java.util.List<Integer> validYPoints = new java.util.ArrayList<>();
            
            for (int i = 0; i < numPoints; i++) {
                DayDetailData.HourlyData hour = hourlyData[i];
                if (hour != null && !Double.isNaN(hour.getTemp()) && !Double.isInfinite(hour.getTemp()) && hour.getTimestamp() > 0) {
                    int x = padding + (int) ((double) i / Math.max(1, (numPoints - 1)) * chartWidth);
                    int y = padding + chartHeight - (int) ((hour.getTemp() - minTemp) / tempRange * chartHeight);
                    validXPoints.add(x);
                    validYPoints.add(y);
                }
            }
            
            if (validXPoints.size() > 1) {
                int[] xPoints = new int[validXPoints.size()];
                int[] yPoints = new int[validYPoints.size()];
                for (int i = 0; i < validXPoints.size(); i++) {
                    xPoints[i] = validXPoints.get(i);
                    yPoints[i] = validYPoints.get(i);
                }
            
                // Draw smooth line
                for (int i = 0; i < validXPoints.size() - 1; i++) {
                    g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw points
                g2.setColor(POINT_COLOR);
                g2.setStroke(new BasicStroke(1));
                for (int i = 0; i < validXPoints.size(); i++) {
                    g2.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                g2.setColor(new Color(POINT_COLOR.getRed(), POINT_COLOR.getGreen(), POINT_COLOR.getBlue(), 100));
                g2.fillOval(xPoints[i] - 8, yPoints[i] - 8, 16, 16);
                g2.setColor(POINT_COLOR);
            }
            
                // Draw temperature values on points - use valid data
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                int validCount = validXPoints.size();
                for (int i = 0; i < validCount; i++) {
                    if (i % (Math.max(1, validCount / 6)) == 0 || i == validCount - 1) {
                        // Find corresponding hourly data
                        int originalIndex = -1;
                        int validIndex = 0;
                        for (int j = 0; j < numPoints; j++) {
                            DayDetailData.HourlyData hour = hourlyData[j];
                            if (hour != null && !Double.isNaN(hour.getTemp()) && !Double.isInfinite(hour.getTemp()) && hour.getTimestamp() > 0) {
                                if (validIndex == i) {
                                    originalIndex = j;
                                    break;
                                }
                                validIndex++;
                            }
                        }
                        if (originalIndex >= 0 && originalIndex < hourlyData.length) {
                            try {
                                String tempLabel = String.format(Locale.US, "%.0f°", hourlyData[originalIndex].getTemp());
                                FontMetrics fm = g2.getFontMetrics();
                                int labelWidth = fm.stringWidth(tempLabel);
                                g2.drawString(tempLabel, xPoints[i] - labelWidth / 2, yPoints[i] - 10);
                            } catch (Exception e) {
                                // Skip invalid data
                            }
                        }
                    }
                }
            }
        }
        
        g2.dispose();
    }

    private void drawNoData(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(TEXT_SECONDARY);
        
        String message = "Không có dữ liệu";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        
        g2.drawString(message, x, y);
        g2.dispose();
    }
}

