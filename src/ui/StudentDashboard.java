package ui;

import client.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StudentDashboard {

    private List<ButtonGroup> answerGroups = new ArrayList<>();
    private List<Integer> questionIds = new ArrayList<>();
    private JFrame frame;
    private JPanel questionPanel;
    private JComboBox<String> courseCombo;
    private JComboBox<String> examCombo;
    private final Map<Integer, Integer> courseMap = new HashMap<>();
    private final Map<Integer, Integer> examMap = new HashMap<>();

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

        // Selection Panel for Courses and Exams
        JPanel selectionPanel = new JPanel();
        selectionPanel.setBackground(UITheme.BG_SECONDARY);
        selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 16));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(UITheme.FONT_LABEL);
        courseLabel.setForeground(UITheme.TEXT_PRIMARY);
        selectionPanel.add(courseLabel);

        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(280, 36));
        selectionPanel.add(courseCombo);

        JLabel examLabel = new JLabel("Exam:");
        examLabel.setFont(UITheme.FONT_LABEL);
        examLabel.setForeground(UITheme.TEXT_PRIMARY);
        selectionPanel.add(examLabel);

        examCombo = new JComboBox<>();
        examCombo.setPreferredSize(new Dimension(280, 36));
        selectionPanel.add(examCombo);

        JButton refreshButton = UITheme.createPrimaryButton("⟳ Refresh");
        selectionPanel.add(refreshButton);

        courseCombo.addActionListener(e -> {
            Integer selectedIndex = courseCombo.getSelectedIndex();
            if (selectedIndex >= 0) {
                Integer courseId = courseMap.get(selectedIndex);
                if (courseId != null) {
                    loadExams(courseId);
                }
            }
        });

        refreshButton.addActionListener(e -> loadCourses());

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

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(selectionPanel, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        loadCourses();

        // START EXAM EVENT
        startButton.addActionListener(e -> {
            try {
                int selectedExamIndex = examCombo.getSelectedIndex();
                if (selectedExamIndex < 0) {
                    JOptionPane.showMessageDialog(frame, "Please select an exam before starting.", "No Exam Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Integer examId = examMap.get(selectedExamIndex);
                if (examId == null) {
                    JOptionPane.showMessageDialog(frame, "Please choose a valid exam.", "Invalid Exam", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                questionPanel.removeAll();
                answerGroups.clear();
                questionIds.clear();

                String response = ApiClient.startExam(examId);
                String[] questions = response.split("\n");

                int questionNumber = 1;
                for (String q : questions) {
                    if (q.isBlank()) continue;
                    String[] parts = q.split("\\|");
                    if (parts.length < 6) continue;

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

                int selectedExamIndex = examCombo.getSelectedIndex();
                if (selectedExamIndex < 0 || !examMap.containsKey(selectedExamIndex)) {
                    JOptionPane.showMessageDialog(frame, "Please select an exam before submitting.", "No Exam Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int examId = examMap.get(selectedExamIndex);
                String data = "studentId=" + studentId + "&examId=" + examId + "&answers=" + answerData;

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

    private void loadCourses() {
        try {
            courseMap.clear();
            courseCombo.removeAllItems();

            String response = ApiClient.getCourses();
            String[] lines = response.split("\n");
            int index = 0;

            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                int courseId = Integer.parseInt(parts[0]);
                String title = parts[1];
                courseCombo.addItem(title);
                courseMap.put(index++, courseId);
            }

            if (courseCombo.getItemCount() > 0) {
                courseCombo.setSelectedIndex(0);
                if (courseMap.containsKey(0)) {
                    loadExams(courseMap.get(0));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading courses", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExams(int courseId) {
        try {
            examMap.clear();
            examCombo.removeAllItems();

            String response = ApiClient.getExams(courseId);
            String[] lines = response.split("\n");
            int index = 0;

            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                int examId = Integer.parseInt(parts[0]);
                String title = parts[1];
                examCombo.addItem(title);
                examMap.put(index++, examId);
            }

            if (examCombo.getItemCount() > 0) {
                examCombo.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading exams", "Error", JOptionPane.ERROR_MESSAGE);
        }
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