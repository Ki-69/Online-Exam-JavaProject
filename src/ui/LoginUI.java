package ui;

import client.ApiClient;
import javax.swing.*;
import java.awt.*;

public class LoginUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ExamHub - Online Exam System");
            frame.setSize(1000, 650);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setIconImage(new ImageIcon(new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB)).getImage());

            JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
            mainPanel.setBackground(UITheme.BG_PRIMARY);

            // LEFT PANEL - BRANDING
            JPanel leftPanel = new JPanel(new GridBagLayout());
            leftPanel.setBackground(UITheme.PRIMARY_COLOR);

            GridBagConstraints leftGbc = new GridBagConstraints();
            leftGbc.insets = new Insets(30, 30, 30, 30);

            ImageIcon brandImg = UITheme.loadIcon("/assets/brand.png", 80);
            JLabel brandIcon = (brandImg != null) ? new JLabel(brandImg) : new JLabel("📚");
            if (brandImg == null) brandIcon.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 80));
            leftGbc.gridx = 0;
            leftGbc.gridy = 0;
            leftGbc.insets = new Insets(30, 30, 20, 30);
            leftPanel.add(brandIcon, leftGbc);

            JLabel brandTitle = new JLabel("ExamHub");
            brandTitle.setFont(UITheme.FONT_TITLE_LARGE);
            brandTitle.setForeground(Color.WHITE);
            leftGbc.gridy = 1;
            leftGbc.insets = new Insets(0, 30, 10, 30);
            leftPanel.add(brandTitle, leftGbc);

            JLabel brandSubtitle = new JLabel("Professional Examination Platform");
            brandSubtitle.setFont(UITheme.FONT_SUBTITLE);
            brandSubtitle.setForeground(new Color(200, 220, 255));
            leftGbc.gridy = 2;
            leftGbc.insets = new Insets(0, 30, 60, 30);
            leftPanel.add(brandSubtitle, leftGbc);

            JTextArea features = new JTextArea("✓ Secure Authentication\n✓ Real-time Scoring\n✓ Question Management\n✓ Result Analytics");
            features.setFont(UITheme.FONT_BODY);
            features.setForeground(new Color(220, 230, 255));
            features.setBackground(UITheme.PRIMARY_COLOR);
            features.setEditable(false);
            features.setLineWrap(true);
            features.setWrapStyleWord(true);
            features.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
            leftGbc.gridy = 3;
            leftGbc.insets = new Insets(0, 30, 30, 30);
            leftPanel.add(features, leftGbc);

            // RIGHT PANEL - LOGIN FORM wrapped in a polished card
            JPanel rightPanel = new JPanel();
            rightPanel.setBackground(UITheme.BG_PRIMARY);
            rightPanel.setLayout(new GridBagLayout());

            JPanel rightCard = UITheme.createCardPanel();
            rightCard.add(rightPanel, BorderLayout.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(UITheme.PADDING_LARGE, UITheme.PADDING_LARGE, UITheme.PADDING_MEDIUM, UITheme.PADDING_LARGE);

            JLabel loginTitle = new JLabel("Welcome Back");
            loginTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
            loginTitle.setForeground(UITheme.PRIMARY_COLOR);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(60, UITheme.PADDING_LARGE, 5, UITheme.PADDING_LARGE);
            rightPanel.add(loginTitle, gbc);

            JLabel loginSubtitle = new JLabel("Sign in to your account");
            loginSubtitle.setFont(UITheme.FONT_SUBTITLE);
            loginSubtitle.setForeground(UITheme.TEXT_SECONDARY);
            gbc.gridy = 1;
            gbc.insets = new Insets(0, UITheme.PADDING_LARGE, 40, UITheme.PADDING_LARGE);
            rightPanel.add(loginSubtitle, gbc);

            JLabel userLabel = new JLabel("Username");
            userLabel.setFont(UITheme.FONT_LABEL);
            userLabel.setForeground(UITheme.TEXT_PRIMARY);
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(20, UITheme.PADDING_LARGE, 5, UITheme.PADDING_LARGE);
            rightPanel.add(userLabel, gbc);

            JTextField userField = UITheme.createTextField();
            userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            gbc.gridy = 3;
            gbc.insets = new Insets(5, UITheme.PADDING_LARGE, 25, UITheme.PADDING_LARGE);
            rightPanel.add(userField, gbc);

            JLabel passLabel = new JLabel("Password");
            passLabel.setFont(UITheme.FONT_LABEL);
            passLabel.setForeground(UITheme.TEXT_PRIMARY);
            gbc.gridy = 4;
            gbc.insets = new Insets(20, UITheme.PADDING_LARGE, 5, UITheme.PADDING_LARGE);
            rightPanel.add(passLabel, gbc);

            JPasswordField passField = UITheme.createPasswordField();
            passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            gbc.gridy = 5;
            gbc.insets = new Insets(5, UITheme.PADDING_LARGE, 35, UITheme.PADDING_LARGE);
            rightPanel.add(passField, gbc);

            JButton loginButton = UITheme.createPrimaryButton("🔓 Sign In");
            loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            gbc.gridy = 6;
            gbc.insets = new Insets(10, UITheme.PADDING_LARGE, 30, UITheme.PADDING_LARGE);
            rightPanel.add(loginButton, gbc);

            mainPanel.add(leftPanel);
            mainPanel.add(rightCard);

            frame.add(mainPanel);

            loginButton.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both username and password", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Disable button during login attempt
                loginButton.setEnabled(false);
                loginButton.setText("⏳ Logging in...");

                // Use SwingWorker to perform HTTP request on background thread
                SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {
                    @Override
                    protected String[] doInBackground() throws Exception {
                        String response = ApiClient.login(username, password);
                        return response.split(",");
                    }

                    @Override
                    protected void done() {
                        try {
                            String[] parts = get();
                            int userId = Integer.parseInt(parts[0]);
                            String role = parts[1];

                            frame.dispose();

                            if (role.equals("STUDENT")) {
                                new StudentDashboard(userId);
                            } else if (role.equals("ADMIN")) {
                                new AdminDashboard();
                            } else if (role.equals("TEACHER")) {
                                // TODO: Create TeacherDashboard
                                JOptionPane.showMessageDialog(null, "Teacher dashboard not yet implemented", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } catch (Exception ex) {
                            loginButton.setEnabled(true);
                            loginButton.setText("🔓 Sign In");
                            JOptionPane.showMessageDialog(frame, "Invalid credentials or server not responding\n\nMake sure the server is running on port 8080", "Login Failed", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                };

                worker.execute();
            });

            frame.setVisible(true);
        });
    }
}