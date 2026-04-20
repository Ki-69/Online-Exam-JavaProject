package ui;

import client.ApiClient;
import java.awt.*;
import javax.swing.*;

public class LoginUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("ExamHub");
            frame.setSize(1000, 650);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel root = new JPanel(new GridLayout(1, 2));
            root.setBackground(UITheme.BG_PRIMARY);

            // =========================
            // LEFT SIDE (CLEAN BRANDING)
            // =========================

            JPanel left = new JPanel(new GridBagLayout());
            left.setBackground(UITheme.BG_PRIMARY);

            GridBagConstraints l = new GridBagConstraints();
            l.gridx = 0;
            l.insets = new Insets(10, 40, 10, 40);

            JLabel logo = UITheme.iconLabel("/assets/brand.png", "📚", 64);
            l.gridy = 0;
            l.insets = new Insets(40, 40, 10, 40);
            left.add(logo, l);

            JLabel title = new JLabel("ExamHub");
            title.setFont(UITheme.FONT_TITLE_LARGE);
            title.setForeground(UITheme.TEXT_PRIMARY);
            l.gridy = 1;
            l.insets = new Insets(10, 40, 5, 40);
            left.add(title, l);

            JLabel subtitle = new JLabel("Online Examination Platform");
            subtitle.setFont(UITheme.FONT_BODY);
            subtitle.setForeground(UITheme.TEXT_SECONDARY);
            l.gridy = 2;
            l.insets = new Insets(0, 40, 40, 40);
            left.add(subtitle, l);

            JTextArea features = new JTextArea(
                    "• Secure login\n• Real-time results\n• Clean experience"
            );
            features.setFont(UITheme.FONT_BODY);
            features.setForeground(UITheme.TEXT_SECONDARY);
            features.setBackground(UITheme.BG_PRIMARY);
            features.setEditable(false);
            features.setBorder(null);
            l.gridy = 3;
            left.add(features, l);

            // =========================
            // RIGHT SIDE (LOGIN CARD)
            // =========================

            JPanel rightWrapper = new JPanel(new GridBagLayout());
            rightWrapper.setBackground(UITheme.BG_PRIMARY);

            JPanel card = UITheme.createCardPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(380, 420));

            JLabel loginTitle = new JLabel("Sign in");
            loginTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
            loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel loginSub = new JLabel("Enter your credentials");
            loginSub.setFont(UITheme.FONT_BODY);
            loginSub.setForeground(UITheme.TEXT_SECONDARY);
            loginSub.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField user = UITheme.createTextField();
            JPasswordField pass = UITheme.createPasswordField();

            JButton loginBtn = UITheme.createPrimaryButton("Sign In");

            user.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            pass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            card.add(loginTitle);
            card.add(Box.createVerticalStrut(5));
            card.add(loginSub);
            card.add(Box.createVerticalStrut(25));

            card.add(label("Username"));
            card.add(user);
            card.add(Box.createVerticalStrut(15));

            card.add(label("Password"));
            card.add(pass);
            card.add(Box.createVerticalStrut(25));

            card.add(loginBtn);

            rightWrapper.add(card);

            root.add(left);
            root.add(rightWrapper);

            frame.add(root);

            // =========================
            // LOGIN LOGIC (UNCHANGED)
            // =========================

            loginBtn.addActionListener(e -> {

                String username = user.getText().trim();
                String password = new String(pass.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Enter credentials");
                    return;
                }

                loginBtn.setEnabled(false);
                loginBtn.setText("Logging in...");

                SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {

                    protected String[] doInBackground() throws Exception {
                        return ApiClient.login(username, password).split(",");
                    }

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
                                new TeacherDashboard(userId);
                            }

                        } catch (Exception ex) {
                            loginBtn.setEnabled(true);
                            loginBtn.setText("Sign In");

                            JOptionPane.showMessageDialog(frame,
                                    "Invalid credentials or server not running");
                        }
                    }
                };

                worker.execute();
            });

            frame.setVisible(true);
        });
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}