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

/**
 * Modern Course Dashboard - Beautiful card-based grid layout with gradient backgrounds
 * Features smooth transitions between course, detail, and exam screens
 */
public class StudentDashboard {

    private JFrame frame;
    private int studentId;
    private List<Course> courses = new ArrayList<>();
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CourseService courseService;
    private ExamService examService;
    private JPanel courseGridPanel;  // Persistent reference to course grid

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        this.courseService = new CourseService();
        this.examService = new ExamService();

        // Main Frame
        frame = new JFrame("ExamHub - Student Dashboard");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setBackground(UITheme.BG_PRIMARY);

        // Main Panel with CardLayout for view switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        // Create Course Dashboard View - returns the grid panel
        JPanel courseCarouselView = createCourseCarouselView();
        mainPanel.add(courseCarouselView, "CAROUSEL");

        frame.add(mainPanel);
        frame.setVisible(true);

        // Load courses
        loadCourses();
    }

    /**
     * Create the main course carousel/grid view
     */
    private JPanel createCourseCarouselView() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(UITheme.BG_PRIMARY);

        // ===== HEADER SECTION =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Left section: Title
        JPanel leftSection = new JPanel(new BorderLayout(0, 10));
        leftSection.setOpaque(false);

        JLabel headerTitle = new JLabel("My Courses");
        headerTitle.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 40));
        headerTitle.setForeground(Color.WHITE);

        JLabel subheader = new JLabel("Select a course to view and take exams");
        subheader.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 14));
        subheader.setForeground(new Color(200, 220, 255));

        leftSection.add(headerTitle, BorderLayout.NORTH);
        leftSection.add(subheader, BorderLayout.SOUTH);

        // Right section: Logout button
        JButton logoutBtn = createHeaderButton("Logout", UITheme.ACCENT_RED, e -> {
            frame.dispose();
            new LoginUI();
        });

        headerPanel.add(leftSection, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        containerPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== COURSE GRID SECTION =====
        courseGridPanel = new JPanel();
        courseGridPanel.setBackground(UITheme.BG_PRIMARY);
        courseGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 28, 28));
        courseGridPanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JScrollPane scrollPane = new JScrollPane(courseGridPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    /**
     * Load all courses for the student
     */
    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseService.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    courses = get();
                    
                    courseGridPanel.removeAll();

                    // Color palette for course cards
                    Color[] cardColors = {
                        new Color(100, 150, 255),   // Blue
                        new Color(255, 120, 100),   // Coral
                        new Color(100, 200, 150),   // Green
                        new Color(255, 180, 100),   // Orange
                        new Color(180, 100, 200),   // Purple
                        new Color(100, 180, 200)    // Cyan
                    };

                    for (int i = 0; i < courses.size(); i++) {
                        Course course = courses.get(i);
                        Color cardColor = cardColors[i % cardColors.length];
                        JPanel courseCard = createCourseCard(course, cardColor);
                        courseGridPanel.add(courseCard);
                    }

                    courseGridPanel.revalidate();
                    courseGridPanel.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error loading courses: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Create a modern gradient course card with progress indicator
     */
    private JPanel createCourseCard(Course course, Color baseColor) {
        // Card with custom painting for gradient background
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);

                // Gradient fill
                GradientPaint gradient = new GradientPaint(
                    0, 0, baseColor,
                    0, getHeight(), baseColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                // Border highlight
                g2d.setColor(new Color(255, 255, 255, 90));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                super.paintComponent(g);
            }
        };

        card.setPreferredSize(new Dimension(310, 210));
        card.setMaximumSize(new Dimension(310, 210));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Progress bar at top
        JPanel progressSection = new JPanel();
        progressSection.setOpaque(false);
        progressSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setBackground(new Color(255, 255, 255, 30));
        progressBar.setForeground(Color.WHITE);
        progressBar.setOpaque(false);
        progressBar.setBorder(null);
        progressBar.setStringPainted(false);
        progressSection.add(progressBar);

        card.add(progressSection, BorderLayout.NORTH);

        // Center content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(course.getTitle());
        titleLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Semester 1, 2024");
        subtitleLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalGlue());

        card.add(contentPanel, BorderLayout.CENTER);

        // Progress text at bottom (dynamically updated)
        JLabel progressLabel = new JLabel("Loading progress...");
        progressLabel.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 11));
        progressLabel.setForeground(new Color(220, 220, 220));
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(progressLabel, BorderLayout.SOUTH);

        // Calculate actual progress based on completed exams
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                try {
                    List<Exam> allExams = examService.getExamsByCourseId(course.getCourseId());
                    if (allExams.isEmpty()) return 0;
                    
                    int completedCount = 0;
                    for (Exam exam : allExams) {
                        try {
                            String result = ApiClient.getResults(exam.getExamId(), studentId);
                            if (result != null && !result.trim().isEmpty()) {
                                completedCount++;
                            }
                        } catch (Exception e) {
                            // Exam not completed
                        }
                    }
                    
                    return (int) ((completedCount * 100.0) / allExams.size());
                } catch (Exception ex) {
                    return 0;
                }
            }
            
            @Override
            protected void done() {
                try {
                    int progress = get();
                    progressBar.setValue(progress);
                    progressLabel.setText(progress + "% complete");
                    card.repaint();
                } catch (Exception ex) {
                    progressLabel.setText("0% complete");
                }
            }
        }.execute();

        // Click handler
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showCourseDetail(course);
            }
        });

        return card;
    }

    /**
     * Show course detail view with available and completed exams
     */
    private void showCourseDetail(Course course) {
        new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                return new CourseDetailPanel(
                    StudentDashboard.this, 
                    course, 
                    studentId,
                    examService
                );
            }

            @Override
            protected void done() {
                try {
                    JPanel detailPanel = get();
                    mainPanel.add(detailPanel, "DETAIL");
                    cardLayout.show(mainPanel, "DETAIL");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Navigate back to course carousel
     */
    public void backToCourses() {
        cardLayout.show(mainPanel, "CAROUSEL");
        loadCourses();
    }

    /**
     * Create header button with modern styling
     */
    private JButton createHeaderButton(String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.getPreferredFont("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }
}
