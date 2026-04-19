package ui;

import client.ApiClient;
import java.awt.*;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TeacherDashboard {

    private JFrame frame;
    private int teacherId;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private static final String MENU = "MENU";
    private static final String MY_COURSES = "MY_COURSES";
    private static final String CREATE_EXAM = "CREATE_EXAM";
    private static final String MANAGE_QUESTIONS = "MANAGE_QUESTIONS";
    private static final String VIEW_RESULTS = "VIEW_RESULTS";

    public TeacherDashboard(int teacherId) {
        this.teacherId = teacherId;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("ExamHub - Teacher Console");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerTitle = new JLabel("Teacher Console");
        headerTitle.setFont(UITheme.FONT_TITLE_LARGE);
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        // Card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        mainPanel.add(createMenuPanel(), MENU);
        mainPanel.add(createMyCoursesPanel(), MY_COURSES);
        mainPanel.add(createExamPanel(), CREATE_EXAM);
        mainPanel.add(createQuestionsPanel(), MANAGE_QUESTIONS);
        mainPanel.add(createResultsPanel(), VIEW_RESULTS);

        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, MENU);

        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel titleLabel = new JLabel("Teacher Dashboard");
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.ipady = 20;
        panel.add(titleLabel, gbc);

        JButton myCoursesBtn = createMenuButton("My Courses", e -> cardLayout.show(mainPanel, MY_COURSES));
        JButton createExamBtn = createMenuButton("Create Exam", e -> cardLayout.show(mainPanel, CREATE_EXAM));
        JButton manageQuestionsBtn = createMenuButton("Manage Questions", e -> cardLayout.show(mainPanel, MANAGE_QUESTIONS));
        JButton viewResultsBtn = createMenuButton("View Results", e -> cardLayout.show(mainPanel, VIEW_RESULTS));

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.ipady = 40;
        panel.add(myCoursesBtn, gbc);

        gbc.gridy = 2;
        panel.add(createExamBtn, gbc);

        gbc.gridy = 3;
        panel.add(manageQuestionsBtn, gbc);

        gbc.gridy = 4;
        panel.add(viewResultsBtn, gbc);

        return panel;
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener listener) {
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

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        JLabel titleLabel = new JLabel("My Assigned Courses");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(UITheme.BG_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        loadTeacherCourses(coursesPanel);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_PRIMARY);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExamPanel() {
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
        formPanel.setMaximumSize(new Dimension(600, 500));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create New Exam");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel courseLabel = new JLabel("Select Course:");
        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setPreferredSize(new Dimension(300, 40));
        populateTeacherCourses(courseDropdown);

        JLabel examLabel = new JLabel("Exam Title:");
        JTextField examField = new JTextField(30);
        examField.setPreferredSize(new Dimension(300, 40));

        JLabel durationLabel = new JLabel("Duration (minutes):");
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 180, 5));

        JLabel attemptsLabel = new JLabel("Max Attempts:");
        JSpinner attemptsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

        JButton createBtn = UITheme.createPrimaryButton("Create Exam");
        createBtn.addActionListener(e -> {
            try {
                String course = (String) courseDropdown.getSelectedItem();
                if (course == null || course.equals("Select Course...")) {
                    JOptionPane.showMessageDialog(frame, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int courseId = Integer.parseInt(course.split(" - ")[0]);
                String examTitle = examField.getText().trim();
                if (examTitle.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter exam title", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int duration = (Integer) durationSpinner.getValue();
                int attempts = (Integer) attemptsSpinner.getValue();

                String response = ApiClient.teacherCreateExam(courseId, examTitle, duration, attempts, teacherId);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Exam created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    examField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error", "Error creating exam"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(courseLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(courseDropdown);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(examLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(examField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(durationLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(durationSpinner);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(attemptsLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(attemptsSpinner);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuestionsPanel() {
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
        formPanel.setMaximumSize(new Dimension(700, 600));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Add Question to Exam");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel examLabel = new JLabel("Select Exam:");
        JComboBox<String> examDropdown = new JComboBox<>();
        examDropdown.setPreferredSize(new Dimension(300, 40));
        populateTeacherExams(examDropdown);

        JLabel questionLabel = new JLabel("Question Text:");
        JTextArea questionArea = new JTextArea(3, 50);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        JLabel optionsLabel = new JLabel("Options (one per line):");
        JTextArea optionsArea = new JTextArea(4, 50);
        optionsArea.setLineWrap(true);
        optionsArea.setWrapStyleWord(true);

        JLabel correctLabel = new JLabel("Correct Option (A/B/C/D):");
        JTextField correctField = new JTextField(30);

        JLabel marksLabel = new JLabel("Marks:");
        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JButton addBtn = UITheme.createPrimaryButton("Add Question");
        addBtn.addActionListener(e -> {
            try {
                String exam = (String) examDropdown.getSelectedItem();
                if (exam == null || exam.equals("Select Exam...")) {
                    JOptionPane.showMessageDialog(frame, "Please select an exam", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int examId = Integer.parseInt(exam.split(" - ")[0]);
                String question = questionArea.getText().trim();
                String correct = correctField.getText().trim().toUpperCase();
                int marks = (Integer) marksSpinner.getValue();

                if (question.isEmpty() || correct.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String response = ApiClient.teacherAddQuestion(examId, question, "Option A", "Option B", "Option C", "Option D", correct, marks, 1);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    questionArea.setText("");
                    correctField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error", "Error adding question"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(examLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(examDropdown);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(questionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(new JScrollPane(questionArea));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(optionsLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(new JScrollPane(optionsArea));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(correctLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(correctField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(marksLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(marksSpinner);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(addBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backBtn = UITheme.createSecondaryButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);

        JLabel titleLabel = new JLabel("Student Results");
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        String[] columns = {"Student", "Exam", "Score", "Attempts", "Date"};
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadTeacherResults(table));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshBtn);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_PRIMARY);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadTeacherResults(table);

        return panel;
    }

    private void loadTeacherCourses(JPanel panel) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String response = ApiClient.teacherGetCourses(teacherId);
                    JSONArray courses = new JSONArray(response);

                    SwingUtilities.invokeLater(() -> {
                        panel.removeAll();
                        if (courses.length() == 0) {
                            JLabel noCoursesLabel = new JLabel("No courses assigned");
                            noCoursesLabel.setFont(UITheme.FONT_BODY);
                            panel.add(noCoursesLabel);
                        } else {
                            for (int i = 0; i < courses.length(); i++) {
                                try {
                                    JSONObject course = courses.getJSONObject(i);
                                    JPanel courseCard = createCourseCard(course);
                                    panel.add(courseCard);
                                    panel.add(Box.createVerticalStrut(10));
                                } catch (Exception ex) {
                                    // Skip on error
                                }
                            }
                        }
                        panel.revalidate();
                        panel.repaint();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }
        }.execute();
    }

    private JPanel createCourseCard(JSONObject course) throws Exception {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel titleLabel = new JLabel(course.getString("course_name"));
        titleLabel.setFont(UITheme.FONT_TITLE_SMALL);

        JLabel descLabel = new JLabel(course.optString("description", "No description"));
        descLabel.setFont(UITheme.FONT_BODY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private void populateTeacherCourses(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Course...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String response = ApiClient.teacherGetCourses(teacherId);
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
            }
        }.execute();
    }

    private void populateTeacherExams(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Exam...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String coursesResponse = ApiClient.teacherGetCourses(teacherId);
                    JSONArray courses = new JSONArray(coursesResponse);

                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject course = courses.getJSONObject(i);
                        int courseId = course.getInt("course_id");
                        String examsResponse = ApiClient.teacherGetExams(courseId);
                        JSONArray exams = new JSONArray(examsResponse);

                        for (int j = 0; j < exams.length(); j++) {
                            JSONObject exam = exams.getJSONObject(j);
                            String item = exam.getInt("exam_id") + " - " + exam.getString("title");
                            SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                        }
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading exams: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }
        }.execute();
    }

    private void loadTeacherResults(JTable table) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String response = ApiClient.teacherGetResultsByTeacher(teacherId);
                    JSONArray results = new JSONArray(response);

                    SwingUtilities.invokeLater(() -> {
                        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
                        model.setRowCount(0);

                        for (int i = 0; i < results.length(); i++) {
                            try {
                                JSONObject result = results.getJSONObject(i);
                                model.addRow(new Object[]{
                                    result.getString("student_name"),
                                    result.getString("exam_title"),
                                    result.getInt("score"),
                                    result.getInt("attempt_number"),
                                    "N/A"
                                });
                            } catch (Exception ex) {
                                // Skip row on error
                            }
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Error loading results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }
        }.execute();
    }
}
