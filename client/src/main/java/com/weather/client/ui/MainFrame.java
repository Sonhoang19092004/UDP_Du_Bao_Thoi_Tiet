package com.weather.client.ui;

import com.weather.client.model.DayDetailData;
import com.weather.client.model.WeatherData;
import com.weather.client.network.WeatherClientNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class MainFrame extends JFrame {
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color CARD_BG = new Color(30, 41, 59, 220);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    
    private final WeatherClientNetwork network;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    
    private JPanel searchPanel;
    private CitySelector citySelector;
    private CurrentWeatherPanel currentWeatherPanel;
    private HourlyForecastPanel hourlyForecastPanel;
    private DailyForecastPanel dailyForecastPanel;
    private DailyDetailPanel dailyDetailPanel;
    private LoadingPanel loadingPanel;
    
    private WeatherData currentWeatherData;
    private String currentCity;

    public MainFrame() {
        this.network = new WeatherClientNetwork();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Thời tiết - Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 900); // Mobile compact size
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(500, 700)); // Mobile compact
        
        // Set dark background
        getContentPane().setBackground(DARK_BG);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ((JComponent) getContentPane()).setDoubleBuffered(true);
        
        // Enable smooth rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Enable emoji rendering on Windows
        System.setProperty("java.awt.fonts", "");
        try {
            // Force font loading
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = ge.getAvailableFontFamilyNames();
            // Pre-load emoji fonts
            for (String fontName : fonts) {
                if (fontName.contains("Emoji") || fontName.contains("Symbol")) {
                    new Font(fontName, Font.PLAIN, 12);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Create search panel with autocomplete
        SearchPanelWithAutocomplete autocompletePanel = new SearchPanelWithAutocomplete();
        autocompletePanel.setSearchListener(city -> {
            SwingUtilities.invokeLater(() -> loadWeatherData(city));
        });
        searchPanel = autocompletePanel;
        
        // Create city selector
        citySelector = new CitySelector();
        citySelector.setCitySelectListener(city -> {
            SwingUtilities.invokeLater(() -> loadWeatherData(city));
        });
        citySelector.setBackListener(() -> {
            cardLayout.show(mainPanel, "MAIN");
        });
        
        // Create weather panels
        currentWeatherPanel = new CurrentWeatherPanel();
        hourlyForecastPanel = new HourlyForecastPanel();
        dailyForecastPanel = new DailyForecastPanel();
        dailyForecastPanel.setDayClickListener(dayTimestamp -> {
            SwingUtilities.invokeLater(() -> loadDayDetail(dayTimestamp));
        });
        dailyDetailPanel = new DailyDetailPanel();
        dailyDetailPanel.setBackListener(() -> {
            cardLayout.show(mainPanel, "MAIN");
        });
        
        // Create loading panel
        loadingPanel = new LoadingPanel();
        JPanel loadingContainer = new JPanel(new BorderLayout());
        loadingContainer.setBackground(DARK_BG);
        loadingContainer.add(loadingPanel, BorderLayout.CENTER);
        
        // Create main weather panel with gradient background
        JPanel mainWeatherPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Subtle gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_BG,
                    width, height, new Color(20, 30, 50)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, width, height);
                
                g2.dispose();
            }
        };
        mainWeatherPanel.setOpaque(false);
        mainWeatherPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Mobile compact
        
        JPanel weatherContent = new JPanel();
        weatherContent.setLayout(new BoxLayout(weatherContent, BoxLayout.Y_AXIS));
        weatherContent.setBackground(DARK_BG);
        weatherContent.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        weatherContent.add(currentWeatherPanel);
        weatherContent.add(Box.createVerticalStrut(20)); // Mobile compact spacing
        weatherContent.add(hourlyForecastPanel);
        weatherContent.add(Box.createVerticalStrut(20));
        weatherContent.add(dailyForecastPanel);
        
        // Enable double buffering for content
        weatherContent.setDoubleBuffered(true);
        
        JScrollPane scrollPane = new JScrollPane(weatherContent);
        scrollPane.setBorder(null);
        scrollPane.setDoubleBuffered(true);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getViewport().setDoubleBuffered(true);
        scrollPane.getViewport().setScrollMode(javax.swing.JViewport.BACKINGSTORE_SCROLL_MODE); // Fix ghosting
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100, 150);
                this.trackColor = DARK_BG;
            }
        });
        // Optimize scroll speed for mobile feel
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(80);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Thin scrollbar
        
        mainWeatherPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create city selector panel
        JPanel citySelectorPanel = new JPanel(new BorderLayout());
        citySelectorPanel.setBackground(DARK_BG);
        citySelectorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        citySelector.setDoubleBuffered(true);
        
        JScrollPane cityScrollPane = new JScrollPane(citySelector);
        cityScrollPane.setBorder(null);
        cityScrollPane.setDoubleBuffered(true);
        cityScrollPane.getViewport().setBackground(DARK_BG);
        cityScrollPane.getViewport().setDoubleBuffered(true);
        cityScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cityScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100, 150);
                this.trackColor = DARK_BG;
            }
        });
        // Optimize scroll speed
        cityScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        cityScrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        citySelectorPanel.add(cityScrollPane, BorderLayout.CENTER);
        
        // Add panels to card layout
        mainPanel.add(mainWeatherPanel, "MAIN");
        mainPanel.add(citySelectorPanel, "CITY_SELECTOR");
        mainPanel.add(dailyDetailPanel, "DETAIL");
        mainPanel.add(loadingContainer, "LOADING");
        mainPanel.setBackground(DARK_BG);
        
        // Create top bar with buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(DARK_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Layout: Search (50%), Tìm button (25%), Thành phố button (25%)
        // Use GridBagLayout for precise control
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 8);
        
        // Search panel takes 50% (weightx = 0.5)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        leftPanel.add(searchPanel, gbc);
        
        // Right side: Tìm button and Thành phố button (equal size, 25% each)
        JPanel rightButtonsPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        rightButtonsPanel.setOpaque(false);
        rightButtonsPanel.setDoubleBuffered(true);
        
        // Tìm button - fix ghosting
        JButton searchButton = new JButton(FontUtil.getEmojiHtml("🔍 Tìm", 14)) {
            @Override
            protected void paintComponent(Graphics g) {
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
                
                if (getModel().isPressed() || getModel().isSelected()) {
                    g2.setColor(ACCENT);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 200));
                } else {
                    g2.setColor(ACCENT);
                }
                
                g2.fillRoundRect(0, 0, width, height, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchButton.setContentAreaFilled(false);
        searchButton.setForeground(TEXT_PRIMARY);
        // Use emoji font for the emoji, but keep text in regular font
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        searchButton.setFont(buttonFont);
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            // Trigger search from search panel
            if (searchPanel instanceof SearchPanelWithAutocomplete) {
                ((SearchPanelWithAutocomplete) searchPanel).triggerSearch();
            }
        });
        
        // Thành phố button - fix ghosting
        JButton cityButton = new JButton(FontUtil.getEmojiHtml("🌍 Thành phố", 14)) {
            @Override
            protected void paintComponent(Graphics g) {
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
                
                if (getModel().isPressed() || getModel().isSelected()) {
                    g2.setColor(CARD_BG);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(51, 65, 85));
                } else {
                    g2.setColor(CARD_BG);
                }
                
                g2.fillRoundRect(0, 0, width, height, 12, 12); // Same corner radius as search button
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cityButton.setContentAreaFilled(false);
        cityButton.setForeground(TEXT_PRIMARY);
        // Use emoji font for the emoji, but keep text in regular font
        Font buttonFont2 = new Font("Segoe UI", Font.BOLD, 14);
        cityButton.setFont(buttonFont2);
        cityButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Same padding as search button
        cityButton.setFocusPainted(false);
        cityButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cityButton.addActionListener(e -> cardLayout.show(mainPanel, "CITY_SELECTOR"));
        
        // Make both buttons same size and ensure visibility
        int buttonHeight = searchPanel.getPreferredSize().height;
        if (buttonHeight <= 0) {
            buttonHeight = 45; // Default height
        }
        searchButton.setPreferredSize(new Dimension(0, buttonHeight));
        searchButton.setMinimumSize(new Dimension(80, buttonHeight));
        searchButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonHeight));
        cityButton.setPreferredSize(new Dimension(0, buttonHeight));
        cityButton.setMinimumSize(new Dimension(80, buttonHeight));
        cityButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonHeight));
        
        // Ensure buttons are visible
        searchButton.setVisible(true);
        cityButton.setVisible(true);
        
        rightButtonsPanel.add(searchButton);
        rightButtonsPanel.add(cityButton);
        
        // Ensure rightButtonsPanel is visible
        rightButtonsPanel.setVisible(true);
        
        // Add rightButtonsPanel to leftPanel with 50% width (weightx = 0.5)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(rightButtonsPanel, gbc);
        
        topBar.add(leftPanel, BorderLayout.CENTER);
        
        // Create main layout
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Show main panel initially
        cardLayout.show(mainPanel, "MAIN");
        
        // Load default city (Hanoi) on startup
        SwingUtilities.invokeLater(() -> {
            loadWeatherData("Hanoi");
        });
    }

    private void loadWeatherData(String city) {
        if (city == null || city.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành phố", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading panel
        cardLayout.show(mainPanel, "LOADING");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                WeatherData data = network.requestWeather(city);
                long loadTime = System.currentTimeMillis() - startTime;
                
                if (data == null) {
                    throw new IOException("Không thể parse dữ liệu từ server");
                }
                
                SwingUtilities.invokeLater(() -> {
                    currentWeatherData = data;
                    currentCity = city;
                    updateWeatherDisplay(data);
                    cardLayout.show(mainPanel, "MAIN");
                    setCursor(Cursor.getDefaultCursor());
                    System.out.println("✓ Data loaded in " + loadTime + "ms");
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    String errorMsg = formatErrorMessage(e.getMessage(), city);
                    JOptionPane.showMessageDialog(
                        this,
                        errorMsg,
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                    cardLayout.show(mainPanel, "MAIN");
                    setCursor(Cursor.getDefaultCursor());
                });
            }
        }).start();
    }
    
    private String formatErrorMessage(String errorMsg, String city) {
        if (errorMsg == null || errorMsg.isEmpty()) {
            return "Không thể kết nối đến server.\n\n" +
                   "Vui lòng kiểm tra:\n" +
                   "1. Server có đang chạy không?\n" +
                   "2. Kết nối mạng có ổn định không?";
        }
        if (errorMsg.contains("401") || errorMsg.contains("Invalid API key")) {
            return "❌ API key không hợp lệ!\n\n" +
                   "Vui lòng kiểm tra:\n" +
                   "1. API key trong server có đúng không?\n" +
                   "2. API key có còn hiệu lực không?\n" +
                   "3. Đã đăng ký API key tại openweathermap.org chưa?\n\n" +
                   "Chi tiết lỗi: " + errorMsg;
        }
        if (errorMsg.contains("429") || errorMsg.contains("rate limit")) {
            return "⚠️ Đã vượt quá giới hạn API!\n\n" +
                   "Bạn đã gửi quá nhiều yêu cầu.\n" +
                   "Vui lòng đợi một lát rồi thử lại.";
        }
        if (errorMsg.contains("not found") || errorMsg.contains("City not found")) {
            return "📍 Không tìm thấy thành phố: " + city + "\n\n" +
                   "Vui lòng thử:\n" +
                   "1. Nhập tên thành phố bằng tiếng Anh\n" +
                   "2. Kiểm tra chính tả\n" +
                   "3. Chọn từ danh sách thành phố có sẵn";
        }
        if (errorMsg.contains("timeout") || errorMsg.contains("Request timeout")) {
            return "⏱️ Kết nối timeout!\n\n" +
                   "Server không phản hồi kịp thời.\n" +
                   "Vui lòng thử lại sau.";
        }
        if (errorMsg.contains("Server error") || errorMsg.contains("Server")) {
            return "🔧 Lỗi server!\n\n" +
                   "Có vấn đề xảy ra ở phía server.\n" +
                   "Vui lòng kiểm tra log server hoặc thử lại sau.\n\n" +
                   "Chi tiết: " + errorMsg;
        }
        return "❌ Lỗi: " + errorMsg + "\n\n" +
               "Vui lòng thử lại hoặc liên hệ hỗ trợ.";
    }

    private void updateWeatherDisplay(WeatherData data) {
        if (data == null) {
            System.err.println("ERROR: Cannot update display - data is null");
            return;
        }
        
        // Update all panels
        currentWeatherPanel.updateData(data);
        hourlyForecastPanel.updateData(data);
        dailyForecastPanel.updateData(data);
        
        // Force repaint for smooth update
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void loadDayDetail(long dayTimestamp) {
        if (currentCity == null) {
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new Thread(() -> {
            try {
                DayDetailData detailData = network.requestDayDetail(currentCity, dayTimestamp);
                SwingUtilities.invokeLater(() -> {
                    dailyDetailPanel.updateData(detailData, currentWeatherData);
                    cardLayout.show(mainPanel, "DETAIL");
                    setCursor(Cursor.getDefaultCursor());
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    String errorMsg = formatErrorMessage(e.getMessage(), currentCity);
                    JOptionPane.showMessageDialog(
                        this,
                        errorMsg,
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                    setCursor(Cursor.getDefaultCursor());
                });
            }
        }).start();
    }
}
