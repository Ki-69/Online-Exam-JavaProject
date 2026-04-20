package ui;

import client.ApiClient;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Course;
import model.Exam;
import service.ExamService;

public class CourseDetailPanel extends JPanel {

    private StudentDashboard parentDashboard;
    private Course course;
    private int studentId;
    private ExamService examService;

    public CourseDetailPanel(StudentDashboard parentDashboard, Course course,
                             int studentId, ExamService examService) {
        this.parentDashboard = parentDashboard;
        this.course = course;
        this.studentId = studentId;
        this.examService = examService;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PRIMARY);

        add(createHeader(), BorderLayout.NORTH);
        add(createExamContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_SECONDARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> parentDashboard.backToCourses());

        JLabel courseTitle = new JLabel(course.getTitle());
        courseTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
        courseTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);
        left.add(backBtn, BorderLayout.WEST);
        left.add(courseTitle, BorderLayout.CENTER);

        JLabel courseInfo = new JLabel("Course ID: " + course.getCourseId());
        courseInfo.setFont(UITheme.FONT_LABEL);
        courseInfo.setForeground(UITheme.TEXT_SECONDARY);

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(courseInfo, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createExamContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(UITheme.BG_PRIMARY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        try {
            List<Exam> allExams = examService.getExamsByCourseId(course.getCourseId());
            List<Exam> availableExams = new ArrayList<>();
            List<Exam> completedExams = new ArrayList<>();
            Map<Integer, String> examScores = new HashMap<>();

            for (Exam exam : allExams) {
                try {
                    String resultResponse = ApiClient.getResults(exam.getExamId(), studentId);
                    if (resultResponse != null && !resultResponse.trim().isEmpty()) {
                        String[] scores = resultResponse.split("\\|");
                        if (scores.length > 0 && !scores[0].trim().isEmpty()) {
                            String latestScore = scores[scores.length - 1];
                            examScores.put(exam.getExamId(), latestScore);
                            completedExams.add(exam);
                        }
                    } else {
                        availableExams.add(exam);
                    }
                } catch (Exception e) {
                    availableExams.add(exam);
                }
            }

            contentPanel.add(createSectionHeader("Available Exams"));
            contentPanel.add(Box.createVerticalStrut(10));

            if (availableExams.isEmpty()) {
                contentPanel.add(createEmptyLabel("No available exams"));
            } else {
                for (Exam exam : availableExams) {
                    contentPanel.add(createExamCard(exam, true, null));
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            }

            contentPanel.add(Box.createVerticalStrut(25));

            contentPanel.add(createSectionHeader("Completed Exams"));
            contentPanel.add(Box.createVerticalStrut(10));

            if (completedExams.isEmpty()) {
                contentPanel.add(createEmptyLabel("No completed exams yet"));
            } else {
                for (Exam exam : completedExams) {
                    String score = examScores.get(exam.getExamId());
                    contentPanel.add(createExamCard(exam, false, score));
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            }

            contentPanel.add(Box.createVerticalGlue());

        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("Error loading exams: " + ex.getMessage());
            errorLabel.setForeground(UITheme.ACCENT_RED);
            contentPanel.add(errorLabel);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PRIMARY);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(UITheme.FONT_TITLE_SMALL);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    private JLabel createEmptyLabel(String message) {
        JLabel label = new JLabel(message);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        return label;
    }

    private JPanel createExamCard(Exam exam, boolean isAvailable, String score) {

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(exam.getTitle());
        title.setFont(UITheme.FONT_BODY);
        title.setForeground(UITheme.TEXT_PRIMARY);

        info.add(title);

        if (!isAvailable && score != null) {
            JLabel scoreLabel = new JLabel("Score: " + score);
            scoreLabel.setFont(UITheme.FONT_LABEL);
            scoreLabel.setForeground(UITheme.ACCENT_GREEN);
            info.add(Box.createVerticalStrut(4));
            info.add(scoreLabel);
        } else {
            JLabel id = new JLabel("Exam ID: " + exam.getExamId());
            id.setFont(UITheme.FONT_LABEL);
            id.setForeground(UITheme.TEXT_SECONDARY);
            info.add(Box.createVerticalStrut(3));
            info.add(id);
        }

        card.add(info, BorderLayout.CENTER);

        JButton btn;
        if (isAvailable) {
            btn = UITheme.createPrimaryButton("Start");
            btn.addActionListener(e -> {
                try {
                    String data = ApiClient.startExam(exam.getExamId(), studentId);
                    SwingUtilities.getWindowAncestor(CourseDetailPanel.this).dispose();
                    new ModernExamUI(exam.getExamId(), data, studentId, exam.getTitle());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });
        } else {
            btn = UITheme.createSecondaryButton("View");
            btn.addActionListener(e -> showResultDialog(exam.getTitle(), score));
        }

        card.add(btn, BorderLayout.EAST);

        return card;
    }

    // =========================
    // CUSTOM RESULT DIALOG
    // =========================

    private void showResultDialog(String examTitle, String score) {

    JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Result",
            Dialog.ModalityType.APPLICATION_MODAL
    );

    dialog.setSize(320, 200);
    dialog.setLocationRelativeTo(this);
    dialog.setUndecorated(true);

    JPanel card = UITheme.createCardPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel title = new JLabel("Result");
    title.setFont(UITheme.FONT_TITLE_SMALL);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel examName = new JLabel(examTitle);
    examName.setFont(UITheme.FONT_LABEL);
    examName.setForeground(UITheme.TEXT_SECONDARY);
    examName.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel scoreLabel = new JLabel(score);
    scoreLabel.setFont(UITheme.FONT_TITLE_MEDIUM);
    scoreLabel.setForeground(UITheme.TEXT_PRIMARY);
    scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton close = UITheme.createSecondaryButton("Close");
    close.setAlignmentX(Component.CENTER_ALIGNMENT);
    close.addActionListener(e -> dialog.dispose());

    card.add(title);
    card.add(Box.createVerticalStrut(8));
    card.add(examName);
    card.add(Box.createVerticalStrut(16));
    card.add(scoreLabel);
    card.add(Box.createVerticalStrut(20));
    card.add(close);

    dialog.add(card);
    dialog.setVisible(true);
}
}