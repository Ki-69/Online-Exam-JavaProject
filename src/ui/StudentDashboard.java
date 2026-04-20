package ui;

import client.ApiClient;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Course;
import model.Exam;
import service.CourseService;
import service.ExamService;

public class StudentDashboard {

    private JFrame frame;
    private int studentId;
    private List<Course> courses = new ArrayList<>();
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CourseService courseService;
    private ExamService examService;
    private JPanel courseGridPanel;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        this.courseService = new CourseService();
        this.examService = new ExamService();

        frame = new JFrame("ExamHub - Student Dashboard");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(UITheme.BG_PRIMARY);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        JPanel courseCarouselView = createCourseCarouselView();
        mainPanel.add(courseCarouselView, "CAROUSEL");

        frame.add(mainPanel);
        frame.setVisible(true);

        loadCourses();
    }

    private JPanel createCourseCarouselView() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(UITheme.BG_PRIMARY);

        // HEADER (FIXED — no aggressive blue)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_SECONDARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JPanel leftSection = new JPanel(new BorderLayout(0, 6));
        leftSection.setOpaque(false);

        JLabel headerTitle = new JLabel("My Courses");
        headerTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
        headerTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subheader = new JLabel("Select a course to view exams");
        subheader.setFont(UITheme.FONT_BODY);
        subheader.setForeground(UITheme.TEXT_SECONDARY);

        leftSection.add(headerTitle, BorderLayout.NORTH);
        leftSection.add(subheader, BorderLayout.SOUTH);

        JButton logoutBtn = UITheme.createSecondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginUI();
        });

        headerPanel.add(leftSection, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        containerPanel.add(headerPanel, BorderLayout.NORTH);

        // GRID
        courseGridPanel = new JPanel();
        courseGridPanel.setBackground(UITheme.BG_PRIMARY);
        courseGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        courseGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JScrollPane scrollPane = new JScrollPane(courseGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            protected List<Course> doInBackground() throws Exception {
                return courseService.getAllCourses();
            }

            protected void done() {
                try {
                    courses = get();
                    courseGridPanel.removeAll();

                    for (Course course : courses) {
                        JPanel card = createCourseCard(course);
                        courseGridPanel.add(card);
                    }

                    courseGridPanel.revalidate();
                    courseGridPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error loading courses: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // CLEAN CARD (NO GRADIENT, NO RANDOM COLORS)
    private JPanel createCourseCard(Course course) {

        JPanel card = UITheme.createCardPanel();
        card.setPreferredSize(new Dimension(300, 180));
        card.setLayout(new BorderLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(course.getTitle());
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel subtitleLabel = new JLabel("Course");
        subtitleLabel.setFont(UITheme.FONT_LABEL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalGlue());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setBorder(null);

        card.add(contentPanel, BorderLayout.CENTER);
        card.add(progressBar, BorderLayout.SOUTH);

        // SAME LOGIC (UNCHANGED)
        new SwingWorker<Integer, Void>() {
            protected Integer doInBackground() throws Exception {
                try {
                    List<Exam> allExams = examService.getExamsByCourseId(course.getCourseId());
                    if (allExams.isEmpty()) return 0;

                    int completed = 0;

                    for (Exam exam : allExams) {
                        try {
                            String result = ApiClient.getResults(exam.getExamId(), studentId);
                            if (result != null && !result.trim().isEmpty()) {
                                completed++;
                            }
                        } catch (Exception ignored) {}
                    }

                    return (int) ((completed * 100.0) / allExams.size());
                } catch (Exception e) {
                    return 0;
                }
            }

            protected void done() {
                try {
                    progressBar.setValue(get());
                } catch (Exception ignored) {}
            }
        }.execute();

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showCourseDetail(course);
            }
        });

        return card;
    }

    private void showCourseDetail(Course course) {
        new SwingWorker<JPanel, Void>() {
            protected JPanel doInBackground() throws Exception {
                return new CourseDetailPanel(
                        StudentDashboard.this,
                        course,
                        studentId,
                        examService
                );
            }

            protected void done() {
                try {
                    JPanel detailPanel = get();
                    mainPanel.add(detailPanel, "DETAIL");
                    cardLayout.show(mainPanel, "DETAIL");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void backToCourses() {
        cardLayout.show(mainPanel, "CAROUSEL");
        loadCourses();
    }

    private JButton createHeaderButton(String text, Color bgColor, ActionListener listener) {
        JButton btn = UITheme.createButton(text, bgColor, Color.WHITE);
        btn.addActionListener(listener);
        return btn;
    }
}