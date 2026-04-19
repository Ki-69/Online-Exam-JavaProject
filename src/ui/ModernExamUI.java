package ui;

import client.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Modern Exam UI - Clean interface for taking exams
 * Features questions with radio button options and a submit button
 */
public class ModernExamUI {

    private JFrame frame;
    private int examId;
    private int studentId;
    private String examTitle;
    private List<ButtonGroup> answerGroups = new ArrayList<>();
    private List<Integer> questionIds = new ArrayList<>();
    private JPanel questionPanel;

    public ModernExamUI(int examId, String questionsData, int studentId, String examTitle) {
        this.examId = examId;
        this.studentId = studentId;
        this.examTitle = examTitle;

        // Main Frame
        frame = new JFrame("ExamHub - " + examTitle);
        frame.setSize(1100, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setBackground(UITheme.BG_PRIMARY);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeader();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Questions panel
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(UITheme.BG_PRIMARY);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        frame.add(scrollPane, BorderLayout.CENTER);

        // Footer with submit button
        JPanel footerPanel = createFooter();
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Load questions
        loadQuestions(questionsData);
    }

    /**
     * Create header with exam title and exit button
     */
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel examTitleLabel = new JLabel(examTitle);
        examTitleLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 28));
        examTitleLabel.setForeground(Color.WHITE);

        JButton exitBtn = createHeaderButton("Exit Exam", UITheme.ACCENT_RED, e -> {
            int response = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit without submitting?",
                "Exit Exam",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                frame.dispose();
                new StudentDashboard(studentId);
            }
        });

        headerPanel.add(examTitleLabel, BorderLayout.WEST);
        headerPanel.add(exitBtn, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create footer with submit button
     */
    private JPanel createFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UITheme.BG_SECONDARY);
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JButton submitBtn = createSubmitButton("Submit Exam", UITheme.ACCENT_GREEN, e -> 
            submitExam()
        );

        footerPanel.add(submitBtn);

        return footerPanel;
    }

    /**
     * Load questions from data string
     */
    private void loadQuestions(String questionsData) {
        String[] lines = questionsData.split("\n");
        int questionNumber = 1;

        for (String line : lines) {
            if (line.isBlank()) continue;
            
            String[] parts = line.split("\\|");
            if (parts.length < 6) continue;

            try {
                int qId = Integer.parseInt(parts[0]);
                questionIds.add(qId);

                String questionText = parts[1];
                String optionA = parts[2];
                String optionB = parts[3];
                String optionC = parts[4];
                String optionD = parts[5];

                JPanel qCard = createQuestionCard(
                    questionNumber, qId, questionText, 
                    optionA, optionB, optionC, optionD
                );
                questionPanel.add(qCard);
                questionPanel.add(Box.createVerticalStrut(18));

                questionNumber++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    /**
     * Create a question card with radio button options
     */
    private JPanel createQuestionCard(int number, int qId, String questionText,
                                      String a, String b, String c, String d) {
        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Question number and text
        JLabel questionNum = new JLabel("Question " + number);
        questionNum.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 13));
        questionNum.setForeground(UITheme.PRIMARY_COLOR);

        JLabel questionLbl = new JLabel("<html><body style='width: 600px'>" + questionText + "</body></html>");
        questionLbl.setFont(UITheme.FONT_BODY);
        questionLbl.setForeground(UITheme.TEXT_PRIMARY);

        card.add(questionNum);
        card.add(Box.createVerticalStrut(8));
        card.add(questionLbl);
        card.add(Box.createVerticalStrut(15));

        // Options
        ButtonGroup group = new ButtonGroup();
        String[] options = {a, b, c, d};
        String[] labels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            JPanel optionPanel = new JPanel();
            optionPanel.setOpaque(false);
            optionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

            JRadioButton option = new JRadioButton(labels[i] + ". " + options[i]);
            option.setFont(UITheme.FONT_BODY);
            option.setBackground(UITheme.BG_SECONDARY);
            option.setForeground(UITheme.TEXT_PRIMARY);
            option.setFocusPainted(false);
            option.setCursor(new Cursor(Cursor.HAND_CURSOR));

            optionPanel.add(option);
            group.add(option);
            card.add(optionPanel);
            card.add(Box.createVerticalStrut(6));
        }

        answerGroups.add(group);
        return card;
    }

    /**
     * Submit exam answers
     */
    private void submitExam() {
        try {
            // Build answer string
            StringBuilder answers = new StringBuilder();

            for (int i = 0; i < answerGroups.size(); i++) {
                ButtonGroup group = answerGroups.get(i);
                String selected = "";

                Enumeration<AbstractButton> buttons = group.getElements();
                while (buttons.hasMoreElements()) {
                    AbstractButton btn = buttons.nextElement();
                    if (btn.isSelected()) {
                        selected = btn.getText().substring(0, 1);
                        break;
                    }
                }

                if (!selected.isEmpty() && i < questionIds.size()) {
                    answers.append(questionIds.get(i))
                            .append(":")
                            .append(selected)
                            .append(",");
                }
            }

            if (answers.length() > 0) {
                answers.deleteCharAt(answers.length() - 1);
            }

            // Submit answers
            String response = ApiClient.submitAnswers(studentId, examId, answers.toString());

            JOptionPane.showMessageDialog(
                frame, 
                "Exam submitted successfully!\n" + response, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE
            );

            frame.dispose();
            new StudentDashboard(studentId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                frame, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Create header button
     */
    private JButton createHeaderButton(String text, Color bgColor, 
                                       java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * Create submit button
     */
    private JButton createSubmitButton(String text, Color bgColor, 
                                       java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }
}
