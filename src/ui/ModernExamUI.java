package ui;

import client.ApiClient;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

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

        frame = new JFrame("ExamHub - " + examTitle);
        frame.setSize(1100, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(UITheme.BG_PRIMARY);
        frame.setLayout(new BorderLayout());

        frame.add(createHeader(), BorderLayout.NORTH);

        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(UITheme.BG_PRIMARY);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(createFooter(), BorderLayout.SOUTH);

        frame.setVisible(true);

        loadQuestions(questionsData);
    }

    // =========================
    // HEADER (FIXED)
    // =========================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(examTitle);
        title.setFont(UITheme.FONT_TITLE_MEDIUM);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JButton exitBtn = UITheme.createSecondaryButton("Exit");
        exitBtn.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    frame,
                    "Exit without submitting?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
            );
            if (response == JOptionPane.YES_OPTION) {
                frame.dispose();
                new StudentDashboard(studentId);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(exitBtn, BorderLayout.EAST);

        return header;
    }

    // =========================
    // FOOTER
    // =========================

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footer.setBackground(UITheme.BG_SECONDARY);

        JButton submitBtn = UITheme.createPrimaryButton("Submit");
        submitBtn.addActionListener(e -> submitExam());

        footer.add(submitBtn);
        return footer;
    }

    // =========================
    // LOAD QUESTIONS (UNCHANGED)
    // =========================

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

                JPanel qCard = createQuestionCard(
                        questionNumber,
                        qId,
                        parts[1], parts[2], parts[3], parts[4], parts[5]
                );

                questionPanel.add(qCard);
                questionPanel.add(Box.createVerticalStrut(16));
                questionNumber++;

            } catch (Exception ignored) {}
        }

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    // =========================
    // QUESTION CARD (CLEAN)
    // =========================

    private JPanel createQuestionCard(int number, int qId, String questionText,
                                      String a, String b, String c, String d) {

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel qNum = new JLabel("Question " + number);
        qNum.setFont(UITheme.FONT_LABEL);
        qNum.setForeground(UITheme.TEXT_SECONDARY);

        JLabel qText = new JLabel("<html><body style='width:600px'>" + questionText + "</body></html>");
        qText.setFont(UITheme.FONT_BODY);

        card.add(qNum);
        card.add(Box.createVerticalStrut(6));
        card.add(qText);
        card.add(Box.createVerticalStrut(12));

        ButtonGroup group = new ButtonGroup();
        String[] options = {a, b, c, d};
        String[] labels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            JRadioButton option = new JRadioButton(labels[i] + ". " + options[i]);
            option.setFont(UITheme.FONT_BODY);
            option.setBackground(UITheme.BG_SECONDARY);
            option.setFocusPainted(false);
            option.setCursor(new Cursor(Cursor.HAND_CURSOR));

            group.add(option);
            card.add(option);
            card.add(Box.createVerticalStrut(6));
        }

        answerGroups.add(group);
        return card;
    }

    // =========================
    // SUBMIT (LOGIC SAME, UI FIXED)
    // =========================

    private void submitExam() {
        try {
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
                    answers.append(questionIds.get(i)).append(":").append(selected).append(",");
                }
            }

            if (answers.length() > 0) {
                answers.deleteCharAt(answers.length() - 1);
            }

            String response = ApiClient.submitAnswers(studentId, examId, answers.toString());

            showSubmitDialog(response);

            frame.dispose();
            new StudentDashboard(studentId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    // =========================
    // CLEAN SUBMIT DIALOG
    // =========================

    private void showSubmitDialog(String response) {

        JDialog dialog = new JDialog(frame, "Submitted", true);
        dialog.setSize(320, 180);
        dialog.setLocationRelativeTo(frame);
        dialog.setUndecorated(true);

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Submitted");
        title.setFont(UITheme.FONT_TITLE_SMALL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel(response);
        msg.setFont(UITheme.FONT_BODY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton ok = UITheme.createSecondaryButton("OK");
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(e -> dialog.dispose());

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(msg);
        card.add(Box.createVerticalStrut(20));
        card.add(ok);

        dialog.add(card);
        dialog.setVisible(true);
    }
}