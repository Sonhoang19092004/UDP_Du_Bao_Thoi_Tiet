package com.weather.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class CitySelector extends JPanel {
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color CARD_BG = new Color(30, 41, 59, 250);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color HOVER_COLOR = new Color(51, 65, 85, 200);
    private static final int CORNER_RADIUS = 16;
    
    private CitySelectListener listener;
    private BackListener backListener;
    
    // Famous cities worldwide
    private static final String[][] QUICK_CITIES = {
        {"Hanoi", "Hà Nội", "🇻🇳"},
        {"Ho Chi Minh City", "Hồ Chí Minh", "🇻🇳"},
        {"London", "London", "🇬🇧"},
        {"New York", "New York", "🇺🇸"},
        {"Tokyo", "Tokyo", "🇯🇵"},
        {"Paris", "Paris", "🇫🇷"},
        {"Sydney", "Sydney", "🇦🇺"}
    };
    
    // All cities with flags
    private static final String[][] ALL_CITIES = {
        {"Hanoi", "Hà Nội", "🇻🇳"},
        {"Ho Chi Minh City", "Hồ Chí Minh", "🇻🇳"},
        {"Da Nang", "Đà Nẵng", "🇻🇳"},
        {"London", "London", "🇬🇧"},
        {"New York", "New York", "🇺🇸"},
        {"Los Angeles", "Los Angeles", "🇺🇸"},
        {"Tokyo", "Tokyo", "🇯🇵"},
        {"Paris", "Paris", "🇫🇷"},
        {"Sydney", "Sydney", "🇦🇺"},
        {"Singapore", "Singapore", "🇸🇬"},
        {"Dubai", "Dubai", "🇦🇪"},
        {"Bangkok", "Bangkok", "🇹🇭"},
        {"Seoul", "Seoul", "🇰🇷"},
        {"Hong Kong", "Hong Kong", "🇭🇰"},
        {"Mumbai", "Mumbai", "🇮🇳"}
    };
    
    public CitySelector() {
        initializeUI();
    }
    
    private void initializeUI() {
        setBackground(DARK_BG);
        setDoubleBuffered(true);
        setLayout(new BorderLayout());
        
        // Top panel with back button and title
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton backBtn = createModernButton("← Quay lại", 12, () -> {
            if (backListener != null) backListener.onBack();
        });
        
        JLabel titleLabel = new JLabel(FontUtil.getEmojiHtml("🌍 Chọn thành phố", 24));
        titleLabel.setFont(FontUtil.getEmojiFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Content panel - ensure proper double buffering
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setDoubleBuffered(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Quick access section
        JLabel quickLabel = createSectionLabel("⚡ Truy cập nhanh");
        contentPanel.add(quickLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Use GridLayout with wrapping for quick access cities
        JPanel quickPanel = new JPanel(new GridLayout(0, 3, 12, 12));
        quickPanel.setOpaque(false);
        quickPanel.setDoubleBuffered(true);
        quickPanel.setFocusable(false); // Don't consume mouse wheel events
        quickPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (String[] city : QUICK_CITIES) {
            JPanel cityCard = createCityCard(city[0], city[1], city[2], true);
            quickPanel.add(cityCard);
        }
        
        contentPanel.add(quickPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // All cities section
        JLabel allLabel = createSectionLabel("🌎 Tất cả thành phố");
        contentPanel.add(allLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // All cities grid - ensure proper double buffering
        JPanel citiesGrid = new JPanel();
        citiesGrid.setLayout(new BoxLayout(citiesGrid, BoxLayout.Y_AXIS));
        citiesGrid.setOpaque(false);
        citiesGrid.setDoubleBuffered(true);
        citiesGrid.setFocusable(false); // Don't consume mouse wheel events
        
        for (int i = 0; i < ALL_CITIES.length; i++) {
            String[] city = ALL_CITIES[i];
            JPanel cityItem = createCityListItem(city[0], city[1], city[2]);
            citiesGrid.add(cityItem);
            if (i < ALL_CITIES.length - 1) {
                citiesGrid.add(Box.createVerticalStrut(10));
            }
        }
        
        // Add cities grid directly to content panel (no nested scroll)
        contentPanel.add(citiesGrid);
        
        // Main layout - single scroll pane to prevent ghosting
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(contentPanel, BorderLayout.CENTER);
        mainContent.setDoubleBuffered(true);
        
        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(null);
        mainScroll.setDoubleBuffered(true);
        mainScroll.getViewport().setBackground(DARK_BG);
        mainScroll.getViewport().setDoubleBuffered(true);
        mainScroll.getViewport().setScrollMode(javax.swing.JViewport.BACKINGSTORE_SCROLL_MODE); // Fix ghosting
        mainScroll.setOpaque(false);
        mainScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Enable mouse wheel scrolling
        mainScroll.setWheelScrollingEnabled(true);
        mainScroll.setFocusable(false); // Don't consume focus
        mainContent.setFocusable(false); // Don't consume focus
        
        mainScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100, 150);
                this.trackColor = DARK_BG;
            }
        });
        // Optimize scroll speed
        mainScroll.getVerticalScrollBar().setUnitIncrement(20);
        mainScroll.getVerticalScrollBar().setBlockIncrement(80);
        mainScroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Thin scrollbar
        
        // Add mouse wheel listener to ensure scrolling works
        mainScroll.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            JScrollBar vertical = mainScroll.getVerticalScrollBar();
            if (vertical != null && vertical.isVisible()) {
                int currentValue = vertical.getValue();
                int newValue = currentValue + (notches * vertical.getUnitIncrement() * 3);
                vertical.setValue(Math.max(vertical.getMinimum(), Math.min(vertical.getMaximum(), newValue)));
            }
        });
        
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mainScroll, BorderLayout.CENTER);
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontUtil.getEmojiFont(Font.BOLD, 18));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return label;
    }
    
    private JButton createModernButton(String text, int fontSize, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(HOVER_COLOR);
                } else {
                    g2.setColor(CARD_BG);
                }
                
                g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setContentAreaFilled(false);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        
        return btn;
    }
    
    private JPanel createCityCard(String cityApi, String cityDisplay, String flag, boolean isQuick) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(false);
        card.setDoubleBuffered(true);
        card.setFocusable(false); // Don't consume mouse wheel events
        card.setPreferredSize(new Dimension(130, 75));
        card.setMaximumSize(new Dimension(130, 75));
        card.setMinimumSize(new Dimension(130, 75));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_COLOR);
                card.repaint(); // Direct repaint, no invokeLater to prevent ghosting
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(0, 0, 0, 0));
                card.repaint(); // Direct repaint
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onCitySelected(cityApi);
                }
            }
        });
        
        // Custom paint for card - fix ghosting
        JPanel cardContent = new JPanel(new BorderLayout(10, 5)) {
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
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_BG,
                    width, height, new Color(40, 51, 69, 250)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
                
                // Border
                g2.setColor(new Color(59, 130, 246, 30));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, width - 1, height - 1, CORNER_RADIUS, CORNER_RADIUS);
                
                g2.dispose();
            }
        };
        cardContent.setOpaque(false);
        cardContent.setDoubleBuffered(true);
        cardContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel flagLabel = new JLabel(FontUtil.getEmojiHtml(flag, 28));
        flagLabel.setFont(FontUtil.getEmojiFont(28));
        flagLabel.setOpaque(false);
        
        // Format city display: "Country Code City Name" for quick access
        String displayText = cityDisplay;
        if (cityApi.equals("Hanoi")) {
            displayText = "VN " + cityDisplay;
        } else if (cityApi.equals("Ho Chi Minh City")) {
            displayText = "VN " + cityDisplay;
        } else if (cityApi.equals("London")) {
            displayText = "GB " + cityDisplay;
        } else if (cityApi.equals("New York")) {
            displayText = "US " + cityDisplay;
        } else if (cityApi.equals("Tokyo")) {
            displayText = "JP " + cityDisplay;
        } else if (cityApi.equals("Paris")) {
            displayText = "FR " + cityDisplay;
        } else if (cityApi.equals("Sydney")) {
            displayText = "AU " + cityDisplay;
        }
        
        JLabel cityLabel = new JLabel(displayText);
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cityLabel.setForeground(TEXT_PRIMARY);
        cityLabel.setOpaque(false);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.setDoubleBuffered(true);
        textPanel.add(cityLabel, BorderLayout.CENTER);
        
        cardContent.add(flagLabel, BorderLayout.WEST);
        cardContent.add(textPanel, BorderLayout.CENTER);
        
        card.add(cardContent, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCityListItem(String cityApi, String cityDisplay, String flag) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setDoubleBuffered(true);
        panel.setFocusable(false); // Don't consume mouse wheel events
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(0, 65));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        panel.setMinimumSize(new Dimension(0, 65));
        
        // Custom paint - fix ghosting
        JPanel content = new JPanel(new BorderLayout(15, 0)) {
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
                
                if (panel.getBackground().getAlpha() > 0) {
                    g2.setColor(panel.getBackground());
                    g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
                }
                
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setDoubleBuffered(true);
        content.setFocusable(false); // Don't consume mouse wheel events
        
        JLabel flagLabel = new JLabel(flag);
        flagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        flagLabel.setOpaque(false);
        
        // Format city display: "Country Code City Name"
        String displayText = cityDisplay;
        if (cityApi.equals("Hanoi")) {
            displayText = "VN " + cityDisplay;
        } else if (cityApi.equals("Ho Chi Minh City")) {
            displayText = "VN " + cityDisplay;
        } else if (cityApi.equals("Da Nang")) {
            displayText = "VN " + cityDisplay;
        } else if (cityApi.equals("London")) {
            displayText = "GB " + cityDisplay;
        } else if (cityApi.equals("New York") || cityApi.equals("Los Angeles")) {
            displayText = "US " + cityDisplay;
        } else if (cityApi.equals("Tokyo")) {
            displayText = "JP " + cityDisplay;
        } else if (cityApi.equals("Paris")) {
            displayText = "FR " + cityDisplay;
        } else if (cityApi.equals("Sydney")) {
            displayText = "AU " + cityDisplay;
        } else if (cityApi.equals("Singapore")) {
            displayText = "SG " + cityDisplay;
        } else if (cityApi.equals("Dubai")) {
            displayText = "AE " + cityDisplay;
        } else if (cityApi.equals("Bangkok")) {
            displayText = "TH " + cityDisplay;
        } else if (cityApi.equals("Seoul")) {
            displayText = "KR " + cityDisplay;
        } else if (cityApi.equals("Hong Kong")) {
            displayText = "HK " + cityDisplay;
        } else if (cityApi.equals("Mumbai")) {
            displayText = "IN " + cityDisplay;
        }
        
        JLabel cityLabel = new JLabel(displayText);
        cityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cityLabel.setForeground(TEXT_PRIMARY);
        cityLabel.setOpaque(false);
        
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        arrowLabel.setForeground(TEXT_SECONDARY);
        arrowLabel.setOpaque(false);
        
        content.add(flagLabel, BorderLayout.WEST);
        content.add(cityLabel, BorderLayout.CENTER);
        content.add(arrowLabel, BorderLayout.EAST);
        
        panel.add(content, BorderLayout.CENTER);
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(HOVER_COLOR);
                panel.repaint(); // Direct repaint to prevent ghosting
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(0, 0, 0, 0));
                panel.repaint(); // Direct repaint
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onCitySelected(cityApi);
                }
            }
        });
        
        return panel;
    }
    
    public void setCitySelectListener(CitySelectListener listener) {
        this.listener = listener;
    }
    
    public void setBackListener(BackListener listener) {
        this.backListener = listener;
    }
    
    public interface CitySelectListener {
        void onCitySelected(String city);
    }
    
    public interface BackListener {
        void onBack();
    }
}
