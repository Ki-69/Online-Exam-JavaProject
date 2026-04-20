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

    public TeacherDashboard(int teacherId) {
        this.teacherId = teacherId;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Teacher Dashboard");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().setBackground(UITheme.BG_PRIMARY);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UITheme.BG_PRIMARY);

        mainPanel.add(menuPanel(), "menu");
        mainPanel.add(coursesPanel(), "courses");
        mainPanel.add(createExamPanel(), "exam");
        mainPanel.add(questionPanel(), "questions");
        mainPanel.add(resultsPanel(), "results");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // =========================
    // HELPERS
    // =========================

    private JPanel card() {
        JPanel p = UITheme.createCardPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private JButton btn(String t) {
        JButton b = UITheme.createPrimaryButton(t);
        b.setMaximumSize(new Dimension(250, 40));
        return b;
    }

    private JTextField field() {
        return UITheme.createTextField();
    }

    private JLabel title(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_TITLE_MEDIUM);
        return l;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }

    private JButton back() {
        JButton b = UITheme.createSecondaryButton("Back");
        b.addActionListener(e -> show("menu"));
        return b;
    }

    private void show(String name) {
        cardLayout.show(mainPanel, name);
    }

    // =========================
    // MENU
    // =========================

    private JPanel menuPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG_PRIMARY);

        JPanel c = card();

        c.add(title("Teacher Dashboard"));
        c.add(Box.createVerticalStrut(20));

        JButton b1 = btn("My Courses");
        b1.addActionListener(e -> show("courses"));

        JButton b2 = btn("Create Exam");
        b2.addActionListener(e -> show("exam"));

        JButton b3 = btn("Manage Questions");
        b3.addActionListener(e -> show("questions"));

        JButton b4 = btn("View Results");
        b4.addActionListener(e -> show("results"));

        c.add(b1); c.add(Box.createVerticalStrut(10));
        c.add(b2); c.add(Box.createVerticalStrut(10));
        c.add(b3); c.add(Box.createVerticalStrut(10));
        c.add(b4);

        p.add(c);
        return p;
    }

    // =========================
    // COURSES
    // =========================

    private JPanel coursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(back());

        JPanel list = card();

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG_PRIMARY);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                try {
                    JSONArray arr = new JSONArray(ApiClient.teacherGetCourses(teacherId));
                    SwingUtilities.invokeLater(() -> {
                        list.removeAll();
                        for (int i = 0; i < arr.length(); i++) {
                            JLabel l = new JLabel(arr.getJSONObject(i).getString("courseName"));
                            l.setFont(UITheme.FONT_BODY);
                            list.add(l);
                            list.add(Box.createVerticalStrut(8));
                        }
                        list.revalidate();
                    });
                } catch (Exception ignored) {}
                return null;
            }
        }.execute();

        return panel;
    }

    // =========================
    // CREATE EXAM
    // =========================

    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(back());

        JPanel form = card();

        JComboBox<String> courseBox = new JComboBox<>();
        JTextField titleF = field();
        JTextField duration = field();
        JTextField attempts = field();
        JTextField datetime = field();

        form.add(title("Create Exam"));
        form.add(Box.createVerticalStrut(15));

        form.add(label("Course")); form.add(courseBox);
        form.add(label("Title")); form.add(titleF);
        form.add(label("Duration")); form.add(duration);
        form.add(label("Attempts")); form.add(attempts);
        form.add(label("Date Time")); form.add(datetime);

        JButton create = btn("Create");

        create.addActionListener(e -> {
            try {
                int courseId = Integer.parseInt(((String) courseBox.getSelectedItem()).split("-")[0]);

                ApiClient.teacherCreateExam(
                        courseId,
                        titleF.getText(),
                        Integer.parseInt(duration.getText()),
                        Integer.parseInt(attempts.getText()),
                        teacherId
                );

                JOptionPane.showMessageDialog(frame, "Created");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        form.add(Box.createVerticalStrut(20));
        form.add(create);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                try {
                    JSONArray arr = new JSONArray(ApiClient.teacherGetCourses(teacherId));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        SwingUtilities.invokeLater(() -> courseBox.addItem(
                                c.getInt("courseId") + "-" + c.getString("courseName")
                        ));
                    }
                } catch (Exception ignored) {}
                return null;
            }
        }.execute();

        panel.add(top, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // =========================
    // QUESTIONS
    // =========================

    private JPanel questionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(back());

        JPanel form = card();

        JComboBox<String> examBox = new JComboBox<>();
        JTextField q = field(), a = field(), b = field(), c = field(), d = field();
        JTextField correct = field(), marks = field();

        form.add(title("Add Question"));

        form.add(label("Exam")); form.add(examBox);
        form.add(label("Question")); form.add(q);
        form.add(label("Option A")); form.add(a);
        form.add(label("Option B")); form.add(b);
        form.add(label("Option C")); form.add(c);
        form.add(label("Option D")); form.add(d);
        form.add(label("Correct (A-D)")); form.add(correct);
        form.add(label("Marks")); form.add(marks);

        JButton add = btn("Add");

        add.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(((String) examBox.getSelectedItem()).split("-")[0]);

                ApiClient.teacherAddQuestion(
                        examId,
                        q.getText(),
                        a.getText(),
                        b.getText(),
                        c.getText(),
                        d.getText(),
                        correct.getText(),
                        Integer.parseInt(marks.getText()),
                        1
                );

                JOptionPane.showMessageDialog(frame, "Added");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        form.add(add);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                try {
                    JSONArray courses = new JSONArray(ApiClient.teacherGetCourses(teacherId));
                    for (int i = 0; i < courses.length(); i++) {
                        int cid = courses.getJSONObject(i).getInt("courseId");
                        JSONArray exams = new JSONArray(ApiClient.teacherGetExams(cid));

                        for (int j = 0; j < exams.length(); j++) {
                            JSONObject e = exams.getJSONObject(j);
                            SwingUtilities.invokeLater(() -> examBox.addItem(
                                    e.getInt("examId") + "-" + e.getString("title")
                            ));
                        }
                    }
                } catch (Exception ignored) {}
                return null;
            }
        }.execute();

        panel.add(top, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // =========================
    // RESULTS
    // =========================

    private JPanel resultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PRIMARY);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(back());

        JTable table = new JTable(new javax.swing.table.DefaultTableModel(
                new String[]{"Student", "Exam", "Score"}, 0));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                try {
                    JSONArray arr = new JSONArray(ApiClient.teacherGetResultsByTeacher(teacherId));

                    SwingUtilities.invokeLater(() -> {
                        var m = (javax.swing.table.DefaultTableModel) table.getModel();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject r = arr.getJSONObject(i);
                            m.addRow(new Object[]{
                                    r.getString("student_name"),
                                    r.getString("exam_title"),
                                    r.getInt("score")
                            });
                        }
                    });

                } catch (Exception ignored) {}
                return null;
            }
        }.execute();

        return panel;
    }
}