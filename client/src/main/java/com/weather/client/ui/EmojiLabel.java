package com.weather.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Custom JLabel that properly renders emojis on Windows
 */
public class EmojiLabel extends JLabel {
    
    public EmojiLabel(String text) {
        super(text);
        setEmojiFont();
    }
    
    public EmojiLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setEmojiFont();
    }
    
    private void setEmojiFont() {
        Font emojiFont = FontUtil.getEmojiFont(getFont().getStyle(), getFont().getSize());
        if (emojiFont != null) {
            setFont(emojiFont);
        }
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
        setEmojiFont();
    }
    
    @Override
    public void setFont(Font font) {
        // Always use emoji font
        Font emojiFont = FontUtil.getEmojiFont(font.getStyle(), font.getSize());
        super.setFont(emojiFont != null ? emojiFont : font);
    }
}

