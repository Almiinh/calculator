package calculatrice;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {

    private final int MIN_FONT_SIZE = 15, MAX_FONT_SIZE = 42;
    private final boolean resizable;

    public Label(String text, int horizontalAlignment, boolean resizable) {
        super(text, horizontalAlignment);
        this.resizable = resizable;
    }

    @Override
    public void setText(String text) {
        if (text.contains("Infinity"))
            text = text.replace("Infinity", "±∞");
        super.setText(text);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (resizable)
            fitText(g);
    }

    /**
     * Fit the text to the width available in the label
     *
     */
    private void fitText(Graphics g) {
        String text = getText();
        Font currentFont = getFont();
        int textWidth = g.getFontMetrics(currentFont).stringWidth(text);
        int fontSize = currentFont.getSize();
        float scale = (float) (getWidth() - 10) / textWidth;
        fontSize = (int) (fontSize * scale);

        // Ensure font size stays within defined limits
        fontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize));

        super.setFont(currentFont.deriveFont((float) fontSize));
    }
}