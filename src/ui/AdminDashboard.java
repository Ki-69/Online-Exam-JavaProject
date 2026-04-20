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

        frame.getContentPane().setBackground(UITheme.BG_PRIMARY);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Admin Console");
        title.setFont(UITheme.FONT_TITLE_MEDIUM);
        title.setForeground(UITheme.TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        mainPanel.add(createMenuPanel(), MENU);
        mainPanel.add(createAddCoursePanel(), ADD_COURSE);
        mainPanel.add(createAssignTeacherPanel(), ASSIGN_TEACHER);
        mainPanel.add(createEnrollStudentPanel(), ENROLL_STUDENT);
        mainPanel.add(createViewDataPanel(), VIEW_DATA);

        frame.setLayout(new BorderLayout());
        frame.add(header, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, MENU);
        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_PRIMARY);

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(500, 500));

        JLabel title = new JLabel("Admin Management");
        title.setFont(UITheme.FONT_TITLE_MEDIUM);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        card.add(createMenuButton("Add Course", e -> cardLayout.show(mainPanel, ADD_COURSE)));
        card.add(Box.createVerticalStrut(10));
        card.add(createMenuButton("Assign Teacher", e -> cardLayout.show(mainPanel, ASSIGN_TEACHER)));
        card.add(Box.createVerticalStrut(10));
        card.add(createMenuButton("Enroll Student", e -> cardLayout.show(mainPanel, ENROLL_STUDENT)));
        card.add(Box.createVerticalStrut(10));
        card.add(createMenuButton("View Data", e -> cardLayout.show(mainPanel, VIEW_DATA)));

        wrapper.add(card);
        return wrapper;
    }

    private JButton createMenuButton(String text, ActionListener l) {
        JButton btn = UITheme.createPrimaryButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(l);
        return btn;
    }

    private JPanel createAddCoursePanel() {
        JPanel panel = basePanel();

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(600, 400));

        JTextField name = UITheme.createTextField();
        JTextArea desc = UITheme.createTextArea();

        JButton submit = UITheme.createPrimaryButton("Create Course");

        submit.addActionListener(e -> {
            try {
                String n = name.getText().trim();
                String d = desc.getText().trim();

                if (n.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Course name cannot be empty");
                    return;
                }

                String res = ApiClient.adminCreateCourse(n, d);
                JSONObject json = new JSONObject(res);

                if (json.getBoolean("success")) {
                    JOptionPane.showMessageDialog(frame, "Created");
                    name.setText("");
                    desc.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, json.optString("error"));
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        card.add(label("Course Name"));
        card.add(name);
        card.add(Box.createVerticalStrut(15));
        card.add(label("Description"));
        card.add(new JScrollPane(desc));
        card.add(Box.createVerticalStrut(20));
        card.add(submit);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAssignTeacherPanel() {
        JPanel panel = basePanel();

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(600, 400));

        JComboBox<String> course = new JComboBox<>();
        JComboBox<String> teacher = new JComboBox<>();

        populateCourses(course);
        populateTeachers(teacher);

        JButton assign = UITheme.createPrimaryButton("Assign");

        assign.addActionListener(e -> {
            try {
                String c = (String) course.getSelectedItem();
                String t = (String) teacher.getSelectedItem();

                if (c == null || t == null || c.contains("Select") || t.contains("Select")) {
                    JOptionPane.showMessageDialog(frame, "Select both");
                    return;
                }

                int cid = Integer.parseInt(c.split(" - ")[0]);
                int tid = Integer.parseInt(t.split(" - ")[0]);

                JSONObject json = new JSONObject(ApiClient.adminAssignTeacher(cid, tid));

                JOptionPane.showMessageDialog(frame,
                        json.getBoolean("success") ? "Assigned" : json.optString("error"));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        card.add(label("Course"));
        card.add(course);
        card.add(Box.createVerticalStrut(15));
        card.add(label("Teacher"));
        card.add(teacher);
        card.add(Box.createVerticalStrut(20));
        card.add(assign);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEnrollStudentPanel() {
        JPanel panel = basePanel();

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(600, 400));

        JComboBox<String> course = new JComboBox<>();
        JComboBox<String> student = new JComboBox<>();

        populateCourses(course);
        populateStudents(student);

        JButton enroll = UITheme.createPrimaryButton("Enroll");

        enroll.addActionListener(e -> {
            try {
                String c = (String) course.getSelectedItem();
                String s = (String) student.getSelectedItem();

                if (c == null || s == null || c.contains("Select") || s.contains("Select")) {
                    JOptionPane.showMessageDialog(frame, "Select both");
                    return;
                }

                int cid = Integer.parseInt(c.split(" - ")[0]);
                int sid = Integer.parseInt(s.split(" - ")[0]);

                JSONObject json = new JSONObject(ApiClient.adminEnrollStudent(cid, sid));

                JOptionPane.showMessageDialog(frame,
                        json.getBoolean("success") ? "Enrolled" : json.optString("error"));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        card.add(label("Course"));
        card.add(course);
        card.add(Box.createVerticalStrut(15));
        card.add(label("Student"));
        card.add(student);
        card.add(Box.createVerticalStrut(20));
        card.add(enroll);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createViewDataPanel() {
        JPanel panel = basePanel();

        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(UITheme.BG_PRIMARY);

        JScrollPane scroll = new JScrollPane(coursesPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG_PRIMARY);

        loadCoursesForViewData(coursesPanel, frame);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadCoursesForViewData(JPanel coursesPanel, JFrame parentFrame) {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                try {
                    String response = ApiClient.adminGetAllCourses();
                    JSONArray courses = new JSONArray(response);

                    SwingUtilities.invokeLater(() -> {
                        coursesPanel.removeAll();

                        for (int i = 0; i < courses.length(); i++) {
                            JSONObject course = courses.getJSONObject(i);

                            int courseId = course.getInt("course_id");
                            String name = course.getString("course_name");
                            String desc = course.optString("description", "No description");

                            JPanel card = UITheme.createCardPanel();
                            card.setLayout(new BorderLayout());
                            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                            JPanel text = new JPanel();
                            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
                            text.setOpaque(false);

                            JLabel title = new JLabel(name);
                            title.setFont(UITheme.FONT_TITLE_SMALL);

                            JLabel d = new JLabel(desc);
                            d.setFont(UITheme.FONT_BODY);

                            text.add(title);
                            text.add(Box.createVerticalStrut(5));
                            text.add(d);

                            JButton view = UITheme.createPrimaryButton("View");
                            int cid = courseId;
                            view.addActionListener(e -> showCourseDetails(cid, name));

                            card.add(text, BorderLayout.CENTER);
                            card.add(view, BorderLayout.EAST);

                            coursesPanel.add(card);
                            coursesPanel.add(Box.createVerticalStrut(10));
                        }

                        coursesPanel.revalidate();
                        coursesPanel.repaint();
                    });

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(parentFrame, e.getMessage()));
                }
                return null;
            }
        }.execute();
    }

    private void showCourseDetails(int courseId, String courseName) {
        JDialog dialog = new JDialog(frame, courseName, true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTable table = new JTable(new javax.swing.table.DefaultTableModel(
            new String[]{"Student", "Score", "Attempts"}, 0));

        JScrollPane scroll = new JScrollPane(table);

        JButton close = UITheme.createSecondaryButton("Close");
        close.addActionListener(e -> dialog.dispose());

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(close, BorderLayout.SOUTH);

        loadCourseResults(courseId, table, dialog);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadCourseResults(int courseId, JTable table, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                try {
                    JSONArray enrollments = new JSONArray(ApiClient.adminGetAllEnrollments());
                    JSONArray results = new JSONArray(ApiClient.adminGetAllResults());

                    SwingUtilities.invokeLater(() -> {
                        javax.swing.table.DefaultTableModel model =
                                (javax.swing.table.DefaultTableModel) table.getModel();

                        model.setRowCount(0);

                        for (int i = 0; i < enrollments.length(); i++) {
                            JSONObject e = enrollments.getJSONObject(i);

                            if (e.getInt("course_id") == courseId) {
                                String name = e.getString("student_name");
                                String courseName = e.getString("course_name");

                                int maxScore = 0;
                                int attempts = 0;

                                for (int j = 0; j < results.length(); j++) {
                                    JSONObject r = results.getJSONObject(j);
                                    if (r.getString("student_name").equals(name) && r.getString("course_name").equals(courseName)) {
                                        maxScore = Math.max(maxScore, r.getInt("score"));
                                        attempts++;
                                    }
                                }

                                model.addRow(new Object[]{
                                        name,
                                        maxScore,
                                        attempts
                                });
                            }
                        }
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(dialog, ex.getMessage()));
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
            protected Void doInBackground() throws Exception {
                JSONArray courses = new JSONArray(ApiClient.adminGetAllCourses());
                for (int i = 0; i < courses.length(); i++) {
                    JSONObject c = courses.getJSONObject(i);
                    String item = c.getInt("course_id") + " - " + c.getString("course_name");
                    SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                }
                return null;
            }
        }.execute();
    }

    private void populateTeachers(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Teacher...");

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                JSONArray users = new JSONArray(ApiClient.adminGetAllUsers());
                for (int i = 0; i < users.length(); i++) {
                    JSONObject u = users.getJSONObject(i);
                    if ("TEACHER".equals(u.getString("role"))) {
                        String item = u.getInt("user_id") + " - " + u.getString("username");
                        SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                    }
                }
                return null;
            }
        }.execute();
    }

    private void populateStudents(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem("Select Student...");

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                JSONArray users = new JSONArray(ApiClient.adminGetAllUsers());
                for (int i = 0; i < users.length(); i++) {
                    JSONObject u = users.getJSONObject(i);
                    if ("STUDENT".equals(u.getString("role"))) {
                        String item = u.getInt("user_id") + " - " + u.getString("username");
                        SwingUtilities.invokeLater(() -> dropdown.addItem(item));
                    }
                }
                return null;
            }
        }.execute();
    }

    private JPanel basePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton back = UITheme.createSecondaryButton("Back");
        back.addActionListener(e -> cardLayout.show(mainPanel, MENU));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(back);

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }
}