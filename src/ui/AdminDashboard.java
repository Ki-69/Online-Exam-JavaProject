package ui;

import client.ApiClient;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminDashboard {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private static final String MENU = "MENU";
    private static final String ADD_COURSE = "ADD_COURSE";
    private static final String ASSIGN_TEACHER = "ASSIGN_TEACHER";
    private static final String ENROLL_STUDENT = "ENROLL_STUDENT";
    private static final String VIEW_DATA = "VIEW_DATA";

    public AdminDashboard() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("ExamHub - Admin Console");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.ACCENT_RED);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerTitle = new JLabel("Admin Console");
        headerTitle.setFont(UITheme.FONT_TITLE_LARGE);
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        // Card layout for navigation
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        // Create all panels
        mainPanel.add(createMenuPanel(), MENU);
        mainPanel.add(createAddCoursePanel(), ADD_COURSE);
        mainPanel.add(createAssignTeacherPanel(), ASSIGN_TEACHER);
        mainPanel.add(createEnrollStudentPanel(), ENROLL_STUDENT);
        mainPanel.add(createViewDataPanel(), VIEW_DATA);

        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Start with menu
        cardLayout.show(mainPanel, MENU);

        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Title
        JLabel titleLabel = new JLabel("Admin Management");
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.ipady = 20;
        panel.add(titleLabel, gbc);

        // Menu buttons
        JButton addCourseBtn = createMenuButton("+ Add Course", e -> cardLayout.show(mainPanel, ADD_COURSE));
        JButton assignTeacherBtn = createMenuButton("Assign Teacher to Course", e -> cardLayout.show(mainPanel, ASSIGN_TEACHER));
        JButton enrollStudentBtn = createMenuButton("Enroll Student in Course", e -> cardLayout.show(mainPanel, ENROLL_STUDENT));
        JButton viewDataBtn = createMenuButton("View Data", e -> cardLayout.show(mainPanel, VIEW_DATA));

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.ipady = 40;
        panel.add(addCourseBtn, gbc);

        gbc.gridy = 2;
        panel.add(assignTeacherBtn, gbc);

        gbc.gridy = 3;
        panel.add(enrollStudentBtn, gbc);

        gbc.gridy = 4;
        panel.add(viewDataBtn, gbc);

        return panel;
    }

    private JButton createMenuButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(350, 80));
        btn.setBackground(UITheme.PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel createAddCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Back button
        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        // Form panel
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(600, 400));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create New Course");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel nameLabel = new JLabel("Course Name:");
        JTextField nameField = new JTextField(30);
        nameField.setFont(UITheme.FONT_BODY);
        nameField.setPreferredSize(new Dimension(300, 40));

        JLabel descLabel = new JLabel("Description:");
        JTextArea descArea = new JTextArea(4, 30);
        descArea.setFont(UITheme.FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JButton submitBtn = UITheme.createPrimaryButton("Create Course");
        submitBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descArea.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Course name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String response = ApiClient.adminCreateCourse(name, desc);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    descArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error", "Unknown error"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(submitBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAssignTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(600, 400));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Assign Teacher to Course");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel courseLabel = new JLabel("Select Course:");
        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setPreferredSize(new Dimension(300, 40));
        populateCourses(courseDropdown);

        JLabel teacherLabel = new JLabel("Select Teacher:");
        JComboBox<String> teacherDropdown = new JComboBox<>();
        teacherDropdown.setPreferredSize(new Dimension(300, 40));
        populateTeachers(teacherDropdown);

        JButton assignBtn = UITheme.createPrimaryButton("Assign Teacher");
        assignBtn.addActionListener(e -> {
            try {
                String course = (String) courseDropdown.getSelectedItem();
                String teacher = (String) teacherDropdown.getSelectedItem();
                if (course == null || course.equals("Select Course...") || teacher == null || teacher.equals("Select Teacher...")) {
                    JOptionPane.showMessageDialog(frame, "Please select both course and teacher", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int courseId = Integer.parseInt(course.split(" - ")[0]);
                int teacherId = Integer.parseInt(teacher.split(" - ")[0]);
                String response = ApiClient.adminAssignTeacher(courseId, teacherId);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Teacher assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error", "Unknown error"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Lists");
        refreshBtn.addActionListener(e -> {
            populateCourses(courseDropdown);
            populateTeachers(teacherDropdown);
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(courseLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(courseDropdown);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(teacherLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(teacherDropdown);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(assignBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEnrollStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(600, 400));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Enroll Student in Course");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel courseLabel = new JLabel("Select Course:");
        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setPreferredSize(new Dimension(300, 40));
        populateCourses(courseDropdown);

        JLabel studentLabel = new JLabel("Select Student:");
        JComboBox<String> studentDropdown = new JComboBox<>();
        studentDropdown.setPreferredSize(new Dimension(300, 40));
        populateStudents(studentDropdown);

        JButton enrollBtn = UITheme.createPrimaryButton("Enroll Student");
        enrollBtn.addActionListener(e -> {
            try {
                String course = (String) courseDropdown.getSelectedItem();
                String student = (String) studentDropdown.getSelectedItem();
                if (course == null || course.equals("Select Course...") || student == null || student.equals("Select Student...")) {
                    JOptionPane.showMessageDialog(frame, "Please select both course and student", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int courseId = Integer.parseInt(course.split(" - ")[0]);
                int studentId = Integer.parseInt(student.split(" - ")[0]);
                String response = ApiClient.adminEnrollStudent(courseId, studentId);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Student enrolled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error", "Unknown error"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Lists");
        refreshBtn.addActionListener(e -> {
            populateCourses(courseDropdown);
            populateStudents(studentDropdown);
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(courseLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(courseDropdown);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(studentLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(studentDropdown);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(enrollBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createViewDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        // Main content area - courses list
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_PRIMARY);

        JLabel coursesTitle = new JLabel("All Courses - Click to view students and results");
        coursesTitle.setFont(UITheme.FONT_TITLE_SMALL);

        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(UITheme.BG_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        // Load courses on panel creation
        loadCoursesForViewData(coursesPanel, frame);

        contentPanel.add(coursesTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadCoursesForViewData(JPanel coursesPanel, JFrame parentFrame) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String response = ApiClient.adminGetAllCourses();
                    JSONArray courses = new JSONArray(response);

                    SwingUtilities.invokeLater(() -> {
                        coursesPanel.removeAll();
                        for (int i = 0; i < courses.length(); i++) {
                            try {
                                JSONObject course = courses.getJSONObject(i);
                                int courseId = course.getInt("course_id");
                                String courseName = course.getString("course_name");
                                String description = course.optString("description", "No description");

                                JPanel courseCard = UITheme.createCardPanel();
                                courseCard.setLayout(new BorderLayout());
                                courseCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                                courseCard.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                                ));

                                JPanel textPanel = new JPanel();
                                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                                textPanel.setOpaque(false);
                                JLabel courseNameLabel = new JLabel(courseName);
                                courseNameLabel.setFont(UITheme.FONT_TITLE_SMALL);
                                JLabel descriptionLabel = new JLabel(description);
                                descriptionLabel.setFont(UITheme.FONT_BODY);
                                textPanel.add(courseNameLabel);
                                textPanel.add(Box.createVerticalStrut(5));
                                textPanel.add(descriptionLabel);

                                JButton viewBtn = UITheme.createPrimaryButton("View Students & Results");
                                int finalCourseId = courseId;
                                viewBtn.addActionListener(e -> showCourseDetails(finalCourseId, courseName));

                                courseCard.add(textPanel, BorderLayout.CENTER);
                                courseCard.add(viewBtn, BorderLayout.EAST);

                                coursesPanel.add(courseCard);
                                coursesPanel.add(Box.createVerticalStrut(10));
                            } catch (Exception ex) {
                                // Skip this course on error
                            }
                        }
                        coursesPanel.revalidate();
                        coursesPanel.repaint();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(parentFrame, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
                return null;
            }
        }.execute();
    }

    private void showCourseDetails(int courseId, String courseName) {
        JDialog dialog = new JDialog(frame, courseName + " - Students & Results", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(frame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(courseName);
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        String[] columns = {"Student Name", "Grade", "Score", "Attempts"};
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(closeBtn, BorderLayout.SOUTH);

        // Load course details
        loadCourseResults(courseId, table, dialog);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void loadCourseResults(int courseId, JTable table, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Get enrollments for this course
                    String enrollResponse = ApiClient.adminGetAllEnrollments();
                    JSONArray enrollments = new JSONArray(enrollResponse);
                    
                    // Get results
                    String resultResponse = ApiClient.adminGetAllResults();
                    JSONArray results = new JSONArray(resultResponse);

                    SwingUtilities.invokeLater(() -> {
                        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
                        model.setRowCount(0);

                        for (int i = 0; i < enrollments.length(); i++) {
                            try {
                                JSONObject enroll = enrollments.getJSONObject(i);
                                if (enroll.getInt("course_id") == courseId) {
                                    String studentName = enroll.getString("student_name");
                                    
                                    // Find results for this student in this course
                                    int totalScore = 0;
                                    int attempts = 0;
                                    for (int j = 0; j < results.length(); j++) {
                                        JSONObject result = results.getJSONObject(j);
                                        if (result.getString("student_name").equals(studentName)) {
                                            totalScore += result.getInt("score");
                                            attempts++;
                                        }
                                    }
                                    
                                    String grade = calculateGrade(totalScore);
                                    model.addRow(new Object[]{studentName, grade, totalScore, attempts});
                                }
                            } catch (Exception ex) {
                                // Skip row on error
                            }
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(dialog, "Error loading course data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
                return null;
            }
        }.execute();
    }

    private String calculateGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private void populateCourses(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Course...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String response = ApiClient.adminGetAllCourses();
                    JSONArray courses = new JSONArray(response);
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject course = courses.getJSONObject(i);
                        String item = course.getInt("course_id") + " - " + course.getString("course_name");
                        SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
                return null;
            }
        }.execute();
    }

    private void populateTeachers(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Teacher...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String response = ApiClient.adminGetAllUsers();
                    JSONArray users = new JSONArray(response);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        if ("teacher".equals(user.getString("role"))) {
                            String item = user.getInt("user_id") + " - " + user.getString("username");
                            SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                        }
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading teachers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
                return null;
            }
        }.execute();
    }

    private void populateStudents(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Student...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String response = ApiClient.adminGetAllUsers();
                    JSONArray users = new JSONArray(response);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        if ("student".equals(user.getString("role"))) {
                            String item = user.getInt("user_id") + " - " + user.getString("username");
                            SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                        }
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
                return null;
            }
        }.execute();
    }
}
