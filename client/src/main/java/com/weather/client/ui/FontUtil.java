package com.weather.client.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to get fonts that properly support emojis on Windows
 */
public class FontUtil {
    
    private static final Map<String, Font> fontCache = new HashMap<>();
    
    /**
     * Get the best font for emoji icons on Windows
     * Tries Segoe UI Emoji first, then Segoe UI Symbol, then Segoe UI
     */
    public static Font getEmojiFont(int style, int size) {
        String cacheKey = style + "_" + size;
        if (fontCache.containsKey(cacheKey)) {
            return fontCache.get(cacheKey).deriveFont((float)size);
        }
        
        String[] emojiFonts = {
            "Segoe UI Emoji",
            "Segoe UI Symbol", 
            "Segoe UI",
            "Microsoft YaHei UI",
            "Arial Unicode MS"
        };
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // Try to find a font that supports emojis
        Font bestFont = null;
        for (String fontName : emojiFonts) {
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    try {
                        // Create font with explicit size
                        Font font = new Font(fontName, style, size);
                        if (font != null) {
                            // Test if font can display emoji characters
                           if (font.canDisplay('☀') || font.canDisplay("🌙".codePointAt(0)) || font.canDisplay("📍".codePointAt(0))) {

                                fontCache.put(cacheKey, font);
                                return font;
                            }
                            // Store first available font as fallback
                            if (bestFont == null) {
                                bestFont = font;
                            }
                        }
                    } catch (Exception e) {
                        // Continue to next font
                    }
                }
            }
        }
        
        // Use best available font even if emoji test fails
        if (bestFont != null) {
            fontCache.put(cacheKey, bestFont);
            return bestFont;
        }
        
        // Last resort: return Segoe UI
        Font fallbackFont = new Font("Segoe UI", style, size);
        fontCache.put(cacheKey, fallbackFont);
        return fallbackFont;
    }
    
    /**
     * Get emoji font with plain style
     */
    public static Font getEmojiFont(int size) {
        return getEmojiFont(Font.PLAIN, size);
    }
    
    /**
     * Create a JLabel with emoji support
     */
    public static javax.swing.JLabel createEmojiLabel(String text, int size) {
        javax.swing.JLabel label = new javax.swing.JLabel(text);
        label.setFont(getEmojiFont(size));
        return label;
    }
    
    /**
     * Create a JLabel with emoji support and style
     */
    public static javax.swing.JLabel createEmojiLabel(String text, int style, int size) {
        javax.swing.JLabel label = new javax.swing.JLabel(text);
        label.setFont(getEmojiFont(style, size));
        return label;
    }
    
    /**
     * Get HTML formatted text with emoji font support
     * This helps render emojis better on Windows
     */
    public static String getEmojiHtml(String text, int size) {
        String fontName = getBestEmojiFontName();
        return String.format(
            "<html><body style='font-family: %s; font-size: %dpx;'>%s</body></html>",
            fontName, size, text
        );
    }
    
    /**
     * Get the best emoji font name available
     */
    private static String getBestEmojiFontName() {
        String[] emojiFonts = {
            "Segoe UI Emoji",
            "Segoe UI Symbol", 
            "Segoe UI",
            "Microsoft YaHei UI",
            "Arial Unicode MS"
        };
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        for (String fontName : emojiFonts) {
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    return fontName;
                }
            }
        }
        
        return "Segoe UI";
    }
}

