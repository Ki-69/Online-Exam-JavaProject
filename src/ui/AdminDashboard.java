package ui;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import javax.swing.*;

public class AdminDashboard {

    public AdminDashboard() {

        JFrame frame = new JFrame("ExamHub - Admin Console");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.ACCENT_RED);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon headerImg = UITheme.loadIcon("/assets/admin.png", 36);
        JLabel headerIcon = (headerImg != null) ? new JLabel(headerImg) : new JLabel("⚙");
        if (headerImg == null) headerIcon.setFont(UITheme.getPreferredFont("Segoe UI", Font.PLAIN, 36));
        
        JLabel headerTitle = new JLabel("Admin Console");
        headerTitle.setFont(UITheme.FONT_TITLE_MEDIUM);
        headerTitle.setForeground(Color.WHITE);

        JPanel headerLeft = new JPanel(new BorderLayout(10, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(headerIcon, BorderLayout.WEST);
        headerLeft.add(headerTitle, BorderLayout.CENTER);

        headerPanel.add(headerLeft, BorderLayout.WEST);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(UITheme.BG_SECONDARY);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton viewBtn = UITheme.createSuccessButton("📊 View Results");
        JButton addBtn = UITheme.createPrimaryButton("➕ Add Question");

        controlPanel.add(viewBtn);
        controlPanel.add(addBtn);

        // Content Panel (elevated card)
        JPanel contentPanel = UITheme.createCardPanel();

        JLabel contentTitle = new JLabel("Results Dashboard");
        contentTitle.setFont(UITheme.FONT_TITLE_SMALL);
        contentTitle.setForeground(UITheme.TEXT_PRIMARY);
        contentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Table for results (clean, professional)
        String[] cols = new String[]{"Student ID", "Score"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(36);
        resultsTable.getTableHeader().setFont(UITheme.FONT_LABEL);
        resultsTable.setFont(UITheme.FONT_BODY);
        resultsTable.setShowGrid(false);
        resultsTable.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.getViewport().setBackground(UITheme.BG_TERTIARY);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(contentTitle, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        // VIEW RESULTS EVENT
        viewBtn.addActionListener(e -> {
            try {
                // clear table
                tableModel.setRowCount(0);

                URL url = new URL("http://localhost:8080/allResults");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                Scanner sc = new Scanner(con.getInputStream());

                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.isEmpty()) continue;

                    // Expected format: "Student ID: <id> | Score: <score>"
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        try {
                            int sid = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                            int scVal = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                            tableModel.addRow(new Object[]{sid, scVal});
                        } catch (NumberFormatException nfe) {
                            tableModel.addRow(new Object[]{line, ""});
                        }
                    } else {
                        tableModel.addRow(new Object[]{line, ""});
                    }
                }

                sc.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Error loading results:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ADD QUESTION EVENT
        addBtn.addActionListener(e -> {
            showAddQuestionDialog(frame);
        });

        frame.setVisible(true);
    }

    private void showAddQuestionDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Add New Question", true);
        dialog.setSize(650, 600);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UITheme.BG_PRIMARY);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel dialogTitle = new JLabel("➕ Create New Question");
        dialogTitle.setFont(UITheme.FONT_TITLE_SMALL);
        dialogTitle.setForeground(UITheme.PRIMARY_COLOR);
        dialogTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(dialogTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        JTextField q = UITheme.createTextField();
        q.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Question Text:", q);

        JTextField examIdField = UITheme.createTextField();
        examIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Exam ID:", examIdField);

        JTextField a = UITheme.createTextField();
        a.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Option A:", a);

        JTextField b = UITheme.createTextField();
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Option B:", b);

        JTextField c = UITheme.createTextField();
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Option C:", c);

        JTextField d = UITheme.createTextField();
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Option D:", d);

        JTextField correct = UITheme.createTextField();
        correct.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Correct Answer (A/B/C/D):", correct);

        JTextField marks = UITheme.createTextField();
        marks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addFieldRow(mainPanel, "Marks:", marks);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton submitBtn = UITheme.createSuccessButton("✓ Add Question");
        JButton cancelBtn = UITheme.createDangerButton("✕ Cancel");

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBackground(UITheme.BG_PRIMARY);
        scrollPane.getViewport().setBackground(UITheme.BG_PRIMARY);
        dialog.add(scrollPane);

        submitBtn.addActionListener(e -> {
            try {
                String questionText = q.getText().trim();
                String examIdText = examIdField.getText().trim();
                String optionA = a.getText().trim();
                String optionB = b.getText().trim();
                String optionC = c.getText().trim();
                String optionD = d.getText().trim();
                String correctAns = correct.getText().trim().toUpperCase();
                String marksStr = marks.getText().trim();

                if (questionText.isEmpty() || examIdText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || 
                    optionC.isEmpty() || optionD.isEmpty() || correctAns.isEmpty() || marksStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!correctAns.matches("[A-D]")) {
                    JOptionPane.showMessageDialog(dialog, "Correct answer must be A, B, C, or D", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int examId;
                try {
                    examId = Integer.parseInt(examIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Exam ID must be a number", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    Integer.parseInt(marksStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Marks must be a number", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String data =
                    "examId=" + examId +
                    "&q=" + URLEncoder.encode(questionText, "UTF-8") +
                    "&a=" + URLEncoder.encode(optionA, "UTF-8") +
                    "&b=" + URLEncoder.encode(optionB, "UTF-8") +
                    "&c=" + URLEncoder.encode(optionC, "UTF-8") +
                    "&d=" + URLEncoder.encode(optionD, "UTF-8") +
                    "&correct=" + URLEncoder.encode(correctAns, "UTF-8") +
                    "&marks=" + marksStr;

                URL url = new URL("http://localhost:8080/addQuestion");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    JOptionPane.showMessageDialog(dialog, "✓ Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Error adding question", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void addFieldRow(JPanel panel, String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(180, 40));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }
}