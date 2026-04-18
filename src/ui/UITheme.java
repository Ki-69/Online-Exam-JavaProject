package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

public class UITheme {
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(33, 150, 243);      // Modern Blue
    public static final Color PRIMARY_DARK = new Color(21, 101, 192);       // Darker Blue
    public static final Color ACCENT_GREEN = new Color(76, 175, 80);        // Green for positive actions
    public static final Color ACCENT_RED = new Color(244, 67, 54);          // Red for admin/danger
    public static final Color ACCENT_ORANGE = new Color(255, 152, 0);       // Orange for warnings
    
    public static final Color BG_PRIMARY = Color.WHITE;
    public static final Color BG_SECONDARY = new Color(245, 245, 245);      // Light gray
    public static final Color BG_TERTIARY = new Color(250, 250, 250);       // Lighter gray
    
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);         // Dark gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);    // Medium gray
    public static final Color TEXT_LIGHT = new Color(189, 189, 189);        // Light gray
    
    public static final Color BORDER_COLOR = new Color(224, 224, 224);      // Light border
    public static final Color DIVIDER_COLOR = new Color(238, 238, 238);     // Divider line
    
    // Fonts (use preferred font with sensible fallbacks)
    public static final Font FONT_TITLE_LARGE = getPreferredFont("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_TITLE_MEDIUM = getPreferredFont("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE_SMALL = getPreferredFont("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = getPreferredFont("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY = getPreferredFont("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = getPreferredFont("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = getPreferredFont("Segoe UI", Font.PLAIN, 12);
    
    // Spacing
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 15;
    public static final int PADDING_LARGE = 20;

    // Component geometry
    public static final int CORNER_RADIUS = 12;
    
    // Border radius (simulated with EmptyBorder)
    public static Border createRoundBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        );
    }
    
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR, Color.WHITE);
    }
    
    public static JButton createSuccessButton(String text) {
        return createButton(text, ACCENT_GREEN, Color.WHITE);
    }
    
    public static JButton createDangerButton(String text) {
        return createButton(text, ACCENT_RED, Color.WHITE);
    }
    
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        RoundedButton button = new RoundedButton(text, CORNER_RADIUS);
        button.setFont(FONT_BUTTON);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect handled inside RoundedButton paint logic via model state
        return button;
    }
    
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(createRoundBorder(10, 12, 10, 12));
        field.setCaretColor(PRIMARY_COLOR);
        return field;
    }
    
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(createRoundBorder(10, 12, 10, 12));
        field.setCaretColor(PRIMARY_COLOR);
        return field;
    }
    
    public static JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_BODY);
        area.setBackground(BG_TERTIARY);
        area.setForeground(TEXT_PRIMARY);
        area.setBorder(createRoundBorder(12, 12, 12, 12));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    /**
     * Load an icon from disk or resources and scale it. Returns null if not found.
     */
    public static ImageIcon loadIcon(String pathOrResource, int size) {
        try {
            java.awt.Image img = null;
            java.io.File f = new java.io.File(pathOrResource);
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                java.net.URL res = UITheme.class.getResource(pathOrResource);
                if (res != null) img = ImageIO.read(res);
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            // ignore and fallback to emoji/text
        }
        return null;
    }

    /**
     * Returns a JLabel showing the icon if available, otherwise an emoji fallback.
     */
    public static JLabel iconLabel(String imagePath, String emojiFallback, int size) {
        ImageIcon ic = loadIcon(imagePath, size);
        if (ic != null) return new JLabel(ic);
        JLabel lbl = new JLabel(emojiFallback);
        lbl.setFont(getPreferredFont("Segoe UI", Font.PLAIN, size));
        return lbl;
    }

    /**
     * Return the preferred font if available, otherwise fall back to common alternatives.
     */
    public static Font getPreferredFont(String preferred, int style, int size) {
        String[] candidates = new String[]{preferred, "Inter", "Roboto", "Arial", "SansSerif"};
        java.util.List<String> families = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : candidates) {
            if (families.contains(f)) return new Font(f, style, size);
        }
        return new Font("SansSerif", style, size);
    }

    /**
     * Create a subtle elevated card panel with rounded corners.
     */
    public static JPanel createCardPanel() {
        RoundedPanel p = new RoundedPanel(CORNER_RADIUS, true);
        p.setBackground(BG_PRIMARY);
        p.setLayout(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
        return p;
    }
    
    public static Color darkenColor(Color color) {
        return new Color(
                Math.max(0, color.getRed() - 20),
                Math.max(0, color.getGreen() - 20),
                Math.max(0, color.getBlue() - 20)
        );
    }

    // Rounded border used for inputs & small decorations
    private static class RoundedBorder implements Border {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius/3, radius/2, radius/3, radius/2);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }

    // A panel which paints a rounded rect and optional shadow to simulate elevation.
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final boolean shadow;

        public RoundedPanel(int radius, boolean shadow) {
            this.radius = radius;
            this.shadow = shadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (shadow) {
                // draw subtle shadow
                Color shadowColor = new Color(0, 0, 0, 25);
                for (int i = 0; i < 4; i++) {
                    g2.setColor(shadowColor);
                    g2.fillRoundRect(3 - i, 3 - i, w - 6 + i*2, h - 6 + i*2, radius + i, radius + i);
                }
            }

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, w-1, h-1, radius, radius);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, w-1, h-1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Button with rounded background painting for a modern look
    private static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            setForeground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color base = getBackground();
            if (!isEnabled()) base = UITheme.BORDER_COLOR;
            if (getModel().isArmed()) {
                base = darkenColor(base);
            } else if (getModel().isRollover()) {
                base = darkenColor(base);
            }

            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(0,0,0,20));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            repaint();
        }

        @Override
        public boolean contains(int x, int y) {
            Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
            return shape.contains(x, y);
        }
    }
}
