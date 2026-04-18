package ui;

import client.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class StudentDashboard {

    private List<ButtonGroup> answerGroups = new ArrayList<>();
    private List<Integer> questionIds = new ArrayList<>();
    private JFrame frame;
    private JPanel questionPanel;

    public StudentDashboard(int studentId) {

        frame = new JFrame("ExamHub - Student Exam Portal");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon headerImg = UITheme.loadIcon("/assets/notes.png", 36);
        JLabel headerIcon = (headerImg != null) ? new JLabel(headerImg) : new JLabel("📝");
        if (headerImg == null) headerIcon.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 36));
        
        JLabel headerTitle = new JLabel("Exam Portal");
        headerTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
        headerTitle.setForeground(Color.WHITE);

        JLabel studentInfo = new JLabel("Student ID: " + studentId);
        studentInfo.setFont(UITheme.FONT_LABEL);
        studentInfo.setForeground(new Color(200, 220, 255));

        JPanel headerLeft = new JPanel(new BorderLayout(10, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(headerIcon, BorderLayout.WEST);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(headerTitle, BorderLayout.NORTH);
        titlePanel.add(studentInfo, BorderLayout.SOUTH);
        headerLeft.add(titlePanel, BorderLayout.CENTER);

        headerPanel.add(headerLeft, BorderLayout.WEST);

        // Control Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BG_SECONDARY);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton startButton = UITheme.createSuccessButton("▶ Start Exam");
        JButton submitButton = UITheme.createDangerButton("✓ Submit Answers");

        buttonPanel.add(startButton);
        buttonPanel.add(submitButton);

        // Questions Container
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(UITheme.BG_PRIMARY);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // START EXAM EVENT
        startButton.addActionListener(e -> {
            try {
                questionPanel.removeAll();
                answerGroups.clear();
                questionIds.clear();

                String response = ApiClient.startExam();
                String[] questions = response.split("\n");

                int questionNumber = 1;
                for (String q : questions) {
                    String[] parts = q.split("\\|");

                    int qId = Integer.parseInt(parts[0]);
                    questionIds.add(qId);

                    JPanel qPanel = createQuestionPanel(parts, questionNumber++);
                    questionPanel.add(qPanel);
                    questionPanel.add(Box.createVerticalStrut(15));
                }

                questionPanel.revalidate();
                questionPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading questions", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // SUBMIT ANSWERS EVENT
        submitButton.addActionListener(e -> {
            try {
                if (questionIds.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please start the exam first", "No Exam Started", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                StringBuilder answerData = new StringBuilder();

                for (int i = 0; i < answerGroups.size(); i++) {
                    ButtonGroup group = answerGroups.get(i);
                    Enumeration<AbstractButton> buttons = group.getElements();
                    String selected = "";

                    while (buttons.hasMoreElements()) {
                        AbstractButton btn = buttons.nextElement();
                        if (btn.isSelected()) {
                            selected = btn.getText().substring(0, 1);
                        }
                    }

                    if (!selected.isEmpty()) {
                        answerData.append(questionIds.get(i))
                                .append(":")
                                .append(selected)
                                .append(",");
                    }
                }

                if (answerData.length() > 0) {
                    answerData.deleteCharAt(answerData.length() - 1);
                }

                String data = "studentId=" + studentId + "&answers=" + answerData;

                URL url = new URL("http://localhost:8080/submitAnswers");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                os.write(data.getBytes());
                os.close();

                Scanner sc = new Scanner(con.getInputStream());
                String response = sc.nextLine();

                JOptionPane.showMessageDialog(frame, response, "Exam Submitted", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private JPanel createQuestionPanel(String[] parts, int questionNumber) {
        JPanel card = UITheme.createCardPanel();
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel qPanel = new JPanel();
        qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
        qPanel.setOpaque(false);
        qPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel questionNum = new JLabel("Question " + questionNumber);
        questionNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        questionNum.setForeground(UITheme.PRIMARY_COLOR);

        JLabel questionLabel = new JLabel("<html>" + parts[1] + "</html>");
        questionLabel.setFont(UITheme.FONT_BODY);
        questionLabel.setForeground(UITheme.TEXT_PRIMARY);

        JRadioButton a = new JRadioButton("A. " + parts[2]);
        JRadioButton b = new JRadioButton("B. " + parts[3]);
        JRadioButton c = new JRadioButton("C. " + parts[4]);
        JRadioButton d = new JRadioButton("D. " + parts[5]);

        for (JRadioButton btn : new JRadioButton[]{a, b, c, d}) {
            btn.setFont(UITheme.FONT_BODY);
            btn.setBackground(UITheme.BG_TERTIARY);
            btn.setForeground(UITheme.TEXT_PRIMARY);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        ButtonGroup group = new ButtonGroup();
        group.add(a);
        group.add(b);
        group.add(c);
        group.add(d);
        answerGroups.add(group);

        qPanel.add(questionNum);
        qPanel.add(Box.createVerticalStrut(8));
        qPanel.add(questionLabel);
        qPanel.add(Box.createVerticalStrut(12));
        qPanel.add(a);
        qPanel.add(b);
        qPanel.add(c);
        qPanel.add(d);

        card.add(qPanel, BorderLayout.CENTER);
        return card;
    }
}