package com.weather.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchPanel extends JPanel {
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color SEARCH_BG = new Color(30, 41, 59);
    private static final Color SEARCH_FOCUS = new Color(40, 51, 69);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final int CORNER_RADIUS = 16;
    
    private JTextField searchField;
    private JButton searchButton;
    private SearchListener searchListener;
    private boolean hasFocus = false;

    public SearchPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(DARK_BG);
        setDoubleBuffered(true);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(12, 0));
        
        // Search field with rounded corners
        JPanel searchFieldPanel = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                g2.setColor(hasFocus ? SEARCH_FOCUS : SEARCH_BG);
                g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
                
                g2.setColor(new Color(59, 130, 246, hasFocus ? 120 : 40));
                g2.setStroke(new BasicStroke(hasFocus ? 2.5f : 1.5f));
                g2.drawRoundRect(0, 0, width - 1, height - 1, CORNER_RADIUS, CORNER_RADIUS);
                
                g2.dispose();
            }
        };
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        searchField = new JTextField();
        searchField.setOpaque(false);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm tên thành phố...");
        
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hasFocus = true;
                SwingUtilities.invokeLater(searchFieldPanel::repaint);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                hasFocus = false;
                SwingUtilities.invokeLater(searchFieldPanel::repaint);
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        
        // Search button with gradient
        searchButton = new JButton("🔍 Tìm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                if (getModel().isPressed()) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(ACCENT.getRed() - 30, ACCENT.getGreen() - 30, ACCENT.getBlue()),
                        width, height, ACCENT
                    );
                    g2.setPaint(gradient);
                } else if (getModel().isRollover()) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT,
                        width, height, new Color(ACCENT.getRed() + 20, ACCENT.getGreen() + 20, ACCENT.getBlue())
                    );
                    g2.setPaint(gradient);
                } else {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT,
                        width, height, new Color(ACCENT.getRed() - 10, ACCENT.getGreen() - 10, ACCENT.getBlue())
                    );
                    g2.setPaint(gradient);
                }
                
                g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchButton.setContentAreaFilled(false);
        searchButton.setForeground(TEXT_PRIMARY);
        searchButton.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> performSearch());
        
        add(searchFieldPanel, BorderLayout.CENTER);
        add(searchButton, BorderLayout.EAST);
    }

    private void performSearch() {
        String city = searchField.getText().trim();
        if (searchListener != null && !city.isEmpty()) {
            searchListener.onSearch(city);
        }
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    public interface SearchListener {
        void onSearch(String city);
    }
}
