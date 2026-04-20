package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;

public class UITheme {

    // =========================
    // COLOR SYSTEM (PREMIUM)
    // =========================

    // Accent
    public static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);

    // Semantic
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color WARNING = new Color(245, 158, 11);

    // Backgrounds
    public static final Color BG_PRIMARY = new Color(250, 250, 252);
    public static final Color BG_SECONDARY = Color.WHITE;
    public static final Color BG_TERTIARY = new Color(244, 245, 247);

    // Text
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);

    // Borders
    public static final Color BORDER_COLOR = new Color(229, 231, 235);

    // =========================
    // BACKWARD COMPATIBILITY
    // =========================

    public static final Color ACCENT_RED = DANGER;
    public static final Color ACCENT_GREEN = SUCCESS;
    public static final Color ACCENT_ORANGE = WARNING;

    // =========================
    // TYPOGRAPHY
    // =========================

    public static final Font FONT_TITLE_LARGE = getPreferredFont("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_TITLE_MEDIUM = getPreferredFont("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE_SMALL = getPreferredFont("Segoe UI", Font.BOLD, 16);

    public static final Font FONT_BODY = getPreferredFont("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL = getPreferredFont("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = getPreferredFont("Segoe UI", Font.BOLD, 13);

    // Old alias
    public static final Font FONT_SUBTITLE = FONT_BODY;

    // =========================
    // SPACING
    // =========================

    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;

    public static final int CORNER_RADIUS = 12;

    // =========================
    // BUTTONS
    // =========================

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, BG_TERTIARY, TEXT_PRIMARY);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, Color.WHITE);
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        RoundedButton btn = new RoundedButton(text, CORNER_RADIUS);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
    }

    // =========================
    // INPUTS
    // =========================

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    private static void styleInput(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(BG_TERTIARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    // =========================
    // TEXT AREA
    // =========================

    public static JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_BODY);
        area.setBackground(BG_TERTIARY);
        area.setForeground(TEXT_PRIMARY);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    // =========================
    // CARD
    // =========================

    public static JPanel createCardPanel() {
        RoundedPanel p = new RoundedPanel(CORNER_RADIUS, true);
        p.setBackground(BG_SECONDARY);
        p.setLayout(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(
                PADDING_MEDIUM, PADDING_MEDIUM,
                PADDING_MEDIUM, PADDING_MEDIUM
        ));
        return p;
    }

    // =========================
    // ICONS
    // =========================

    public static ImageIcon loadIcon(String path, int size) {
        try {
            Image img = null;
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                java.net.URL res = UITheme.class.getResource(path);
                if (res != null) img = ImageIO.read(res);
            }
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static JLabel iconLabel(String path, String fallback, int size) {
        ImageIcon ic = loadIcon(path, size);
        if (ic != null) return new JLabel(ic);
        JLabel lbl = new JLabel(fallback);
        lbl.setFont(getPreferredFont("Segoe UI", Font.PLAIN, size));
        return lbl;
    }

    // =========================
    // FONT FALLBACK
    // =========================

    public static Font getPreferredFont(String preferred, int style, int size) {
        String[] fonts = {preferred, "Inter", "Roboto", "Arial", "SansSerif"};
        java.util.List<String> available = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        );
        for (String f : fonts) {
            if (available.contains(f)) return new Font(f, style, size);
        }
        return new Font("SansSerif", style, size);
    }

    // =========================
    // UTIL
    // =========================

    public static Color darken(Color c) {
        return new Color(
                Math.max(0, c.getRed() - 15),
                Math.max(0, c.getGreen() - 15),
                Math.max(0, c.getBlue() - 15)
        );
    }

    // =========================
    // ROUNDED PANEL
    // =========================

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
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, w - 6, h - 6, radius, radius);
            }

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================
    // ROUNDED BUTTON
    // =========================

    private static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = getBackground();

            if (getModel().isRollover()) color = PRIMARY_HOVER;
            if (getModel().isPressed()) color = darken(color);

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius)
                    .contains(x, y);
        }
    }
}