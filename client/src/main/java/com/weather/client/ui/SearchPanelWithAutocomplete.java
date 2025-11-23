package com.weather.client.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchPanelWithAutocomplete extends JPanel {
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color SEARCH_BG = new Color(30, 41, 59);
    private static final Color SEARCH_FOCUS = new Color(40, 51, 69);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color HOVER_COLOR = new Color(51, 65, 85);
    private static final int CORNER_RADIUS = 16;
    
    private JTextField searchField;
    private JButton searchButton;
    private JPanel suggestionsPanel;
    private JWindow suggestionsWindow;
    private SearchListener searchListener;
    private boolean hasFocus = false;
    
    // Popular cities for autocomplete
    private static final String[] POPULAR_CITIES = {
        "Hanoi", "Ho Chi Minh City", "Da Nang", "Hue", "Can Tho",
        "London", "Paris", "New York", "Tokyo", "Sydney",
        "Singapore", "Dubai", "Bangkok", "Seoul", "Hong Kong",
        "Mumbai", "Delhi", "Shanghai", "Beijing", "Moscow",
        "Berlin", "Madrid", "Rome", "Amsterdam", "Vienna"
    };
    
    private List<String> filteredSuggestions = new ArrayList<>();

    public SearchPanelWithAutocomplete() {
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
        
        JLabel searchIcon = new JLabel(FontUtil.getEmojiHtml("🔍", 20));
        searchIcon.setFont(FontUtil.getEmojiFont(20));
        
        searchField = new JTextField();
        searchField.setOpaque(false);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm tên thành phố...");
        
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                hasFocus = true;
                SwingUtilities.invokeLater(searchFieldPanel::repaint);
                updateSuggestions();
            }
            public void focusLost(FocusEvent evt) {
                hasFocus = false;
                SwingUtilities.invokeLater(() -> {
                    searchFieldPanel.repaint();
                    hideSuggestions();
                });
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideSuggestions();
                }
            }
        });
        
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        
        // Search button
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
        // Search button removed - using external button in MainFrame instead
        // searchButton.setContentAreaFilled(false);
        // searchButton.setForeground(TEXT_PRIMARY);
        // searchButton.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        // searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        // searchButton.setFocusPainted(false);
        // searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // searchButton.addActionListener(e -> performSearch());
        
        add(searchFieldPanel, BorderLayout.CENTER);
        // Removed: add(searchButton, BorderLayout.EAST);
        
        // Create suggestions window
        suggestionsWindow = new JWindow();
        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new BoxLayout(suggestionsPanel, BoxLayout.Y_AXIS));
        suggestionsPanel.setBackground(SEARCH_BG);
        suggestionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JScrollPane scrollPane = new JScrollPane(suggestionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SEARCH_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100, 150);
                this.trackColor = SEARCH_BG;
            }
        });
        
        suggestionsWindow.add(scrollPane);
        suggestionsWindow.setBackground(SEARCH_BG);
    }
    
    private void updateSuggestions() {
        String text = searchField.getText().trim().toLowerCase();
        
        if (text.isEmpty()) {
            hideSuggestions();
            return;
        }
        
        filteredSuggestions = java.util.Arrays.stream(POPULAR_CITIES)
            .filter(city -> city.toLowerCase().contains(text))
            .limit(5)
            .collect(Collectors.toList());
        
        if (filteredSuggestions.isEmpty()) {
            hideSuggestions();
            return;
        }
        
        showSuggestions();
    }
    
    private void showSuggestions() {
        suggestionsPanel.removeAll();
        
        for (String city : filteredSuggestions) {
            JPanel suggestionItem = createSuggestionItem(city);
            suggestionsPanel.add(suggestionItem);
        }
        
        suggestionsPanel.revalidate();
        suggestionsPanel.repaint();
        
        // Position window
        Point location = searchField.getLocationOnScreen();
        suggestionsWindow.setSize(searchField.getWidth(), Math.min(200, filteredSuggestions.size() * 45));
        suggestionsWindow.setLocation(location.x, location.y + searchField.getHeight() + 5);
        suggestionsWindow.setVisible(true);
    }
    
    private void hideSuggestions() {
        suggestionsWindow.setVisible(false);
    }
    
    private JPanel createSuggestionItem(String city) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(0, 40));
        
        JLabel cityLabel = new JLabel(FontUtil.getEmojiHtml("📍 " + city, 15));
        cityLabel.setFont(FontUtil.getEmojiFont(15));
        cityLabel.setForeground(TEXT_PRIMARY);
        
        panel.add(cityLabel, BorderLayout.CENTER);
        
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
                searchField.setText(city);
                hideSuggestions();
                performSearch();
            }
        });
        
        return panel;
    }

    private void performSearch() {
        String city = searchField.getText().trim();
        if (searchListener != null && !city.isEmpty()) {
            hideSuggestions();
            searchListener.onSearch(city);
        }
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }
    
    public void triggerSearch() {
        performSearch();
    }

    public interface SearchListener {
        void onSearch(String city);
    }
}

