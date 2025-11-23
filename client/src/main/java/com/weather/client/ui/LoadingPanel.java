package com.weather.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class LoadingPanel extends JPanel {
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    
    private double angle = 0;
    private Timer animationTimer;
    
    public LoadingPanel() {
        setOpaque(false);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(200, 200));
        
        // Start animation
        animationTimer = new Timer(16, e -> {
            angle += 0.1;
            if (angle >= 2 * Math.PI) {
                angle = 0;
            }
            repaint();
        });
        animationTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;
        
        // Draw spinning circle
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        
        // Gradient for spinner
        for (int i = 0; i < 8; i++) {
            double alpha = 1.0 - (i * 0.125);
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), (int)(alpha * 255)));
            g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            double startAngle = (i * Math.PI / 4) - Math.PI / 2;
            double endAngle = startAngle + Math.PI / 6;
            
            g2.drawArc(-radius, -radius, radius * 2, radius * 2, 
                      (int)Math.toDegrees(startAngle), (int)Math.toDegrees(endAngle - startAngle));
        }
        
        g2.setTransform(oldTransform);
        
        // Loading text
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2.setColor(TEXT_PRIMARY);
        String text = "Đang tải dữ liệu...";
        FontMetrics fm = g2.getFontMetrics();
        int textX = centerX - fm.stringWidth(text) / 2;
        int textY = centerY + radius + 30;
        g2.drawString(text, textX, textY);
        
        g2.dispose();
    }
    
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}

