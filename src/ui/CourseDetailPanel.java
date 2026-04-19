package ui;

import client.ApiClient;
import model.Course;
import model.Exam;
import service.ExamService;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Course Detail Panel - Displays available and completed exams for a course
 * Features clean card-based layout with action buttons
 */
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

    /**
     * Create header with back button and course title
     */
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JButton backBtn = createBackButton("← Back to Courses", e -> 
            parentDashboard.backToCourses()
        );

        JLabel courseTitle = new JLabel(course.getTitle());
        courseTitle.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 32));
        courseTitle.setForeground(Color.WHITE);
        courseTitle.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(backBtn, BorderLayout.WEST);
        leftPanel.add(courseTitle, BorderLayout.CENTER);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        JLabel courseInfo = new JLabel("Course ID: " + course.getCourseId());
        courseInfo.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 13));
        courseInfo.setForeground(new Color(200, 220, 255));
        headerPanel.add(courseInfo, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create scrollable content with available and completed exams
     */
    private JPanel createExamContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(UITheme.BG_PRIMARY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        try {
            // Load exams
            List<Exam> allExams = examService.getExamsByCourseId(course.getCourseId());
            List<Exam> availableExams = new ArrayList<>();
            List<Exam> completedExams = new ArrayList<>();
            Map<Integer, String> examScores = new HashMap<>();

            // Separate exams
            for (Exam exam : allExams) {
                try {
                    String resultResponse = ApiClient.getResults(exam.getExamId(), studentId);
                    if (resultResponse != null && !resultResponse.trim().isEmpty()) {
                        // Parse the response: "score1|score2|score3" format
                        String[] scores = resultResponse.split("\\|");
                        if (scores.length > 0 && !scores[0].trim().isEmpty()) {
                            // Use the latest (last) score for display
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

            // Available Exams Section
            contentPanel.add(createSectionHeader("Available Exams"));
            contentPanel.add(Box.createVerticalStrut(15));

            if (availableExams.isEmpty()) {
                contentPanel.add(createEmptyLabel("No available exams"));
            } else {
                for (Exam exam : availableExams) {
                    contentPanel.add(createExamCard(exam, true, null));
                    contentPanel.add(Box.createVerticalStrut(12));
                }
            }

            contentPanel.add(Box.createVerticalStrut(30));

            // Completed Exams Section
            contentPanel.add(createSectionHeader("Completed Exams"));
            contentPanel.add(Box.createVerticalStrut(15));

            if (completedExams.isEmpty()) {
                contentPanel.add(createEmptyLabel("No completed exams yet"));
            } else {
                for (Exam exam : completedExams) {
                    String score = examScores.get(exam.getExamId());
                    contentPanel.add(createExamCard(exam, false, score));
                    contentPanel.add(Box.createVerticalStrut(12));
                }
            }

            contentPanel.add(Box.createVerticalGlue());

        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("Error loading exams: " + ex.getMessage());
            errorLabel.setForeground(UITheme.ACCENT_RED);
            contentPanel.add(errorLabel);
        }

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PRIMARY);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Create section header label
     */
    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 22));
        label.setForeground(UITheme.PRIMARY_COLOR);
        return label;
    }

    /**
     * Create empty state message
     */
    private JLabel createEmptyLabel(String message) {
        JLabel label = new JLabel(message);
        label.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 14));
        label.setForeground(UITheme.TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return label;
    }

    /**
     * Create an exam card with action button
     */
    private JPanel createExamCard(Exam exam, boolean isAvailable, String score) {
        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_SECONDARY);
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        // Left: Exam info
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel examTitle = new JLabel(exam.getTitle());
        examTitle.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 16));
        examTitle.setForeground(UITheme.TEXT_PRIMARY);
        infoPanel.add(examTitle);

        if (!isAvailable && score != null) {
            JLabel scoreLabel = new JLabel("Score: " + score);
            scoreLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 13));
            scoreLabel.setForeground(UITheme.ACCENT_GREEN);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(scoreLabel);
        } else if (isAvailable) {
            JLabel dateLabel = new JLabel("Exam ID: " + exam.getExamId());
            dateLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(UITheme.TEXT_SECONDARY);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(dateLabel);
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // Right: Action button
        JButton actionBtn;
        if (isAvailable) {
            actionBtn = createActionButton("Start Exam", UITheme.PRIMARY_COLOR, e -> {
                try {
                    String questionsData = ApiClient.startExam(exam.getExamId(), studentId);
                    SwingUtilities.getWindowAncestor(CourseDetailPanel.this).dispose();
                    new ModernExamUI(exam.getExamId(), questionsData, studentId, exam.getTitle());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        CourseDetailPanel.this,
                        "Error starting exam: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        } else {
            actionBtn = createActionButton("View Result", UITheme.ACCENT_GREEN, e -> {
                JOptionPane.showMessageDialog(
                    CourseDetailPanel.this,
                    "Exam: " + exam.getTitle() + "\nScore: " + score,
                    "Exam Result",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        }

        card.add(actionBtn, BorderLayout.EAST);

        return card;
    }

    /**
     * Create action button
     */
    private JButton createActionButton(String text, Color bgColor, 
                                      java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * Create back button
     */
    private JButton createBackButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(0, 0, 0, 40));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }
}
