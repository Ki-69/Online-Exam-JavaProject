package dao;

import db.DBConnection;
import model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public void saveResult(int studentId, int examId, int score) throws Exception {
        Connection con = DBConnection.getConnection();

        try {
            String attemptQuery = "SELECT COALESCE(MAX(attempt_number), 0) + 1 AS next_attempt " +
                                  "FROM results WHERE student_id = ? AND exam_id = ?";
            PreparedStatement attemptPs = con.prepareStatement(attemptQuery);
            attemptPs.setInt(1, studentId);
            attemptPs.setInt(2, examId);

            ResultSet attemptRs = attemptPs.executeQuery();
            int attemptNumber = 1;
            if (attemptRs.next()) {
                attemptNumber = attemptRs.getInt("next_attempt");
            }
            attemptRs.close();
            attemptPs.close();

            String query = "INSERT INTO results (student_id, exam_id, score, attempt_number) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, score);
            ps.setInt(4, attemptNumber);

            ps.executeUpdate();
            ps.close();
            con.commit();
            con.close();
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    public List<String> getAllResults() throws Exception {
        Connection con = DBConnection.getConnection();

        try {
            String query = "SELECT r.student_id, r.exam_id, r.score, r.attempt_number, e.exam_name AS exam_title, c.course_name AS course_title " +
                           "FROM results r " +
                           "LEFT JOIN exams e ON r.exam_id = e.exam_id " +
                           "LEFT JOIN courses c ON e.course_id = c.course_id";
            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            List<String> list = new ArrayList<>();

            while (rs.next()) {
                String row = "Student ID: " + rs.getInt("student_id") +
                             " | Course: " + rs.getString("course_title") +
                             " | Exam: " + rs.getString("exam_title") +
                             " | Attempt: " + rs.getInt("attempt_number") +
                             " | Score: " + rs.getInt("score");
                list.add(row);
            }

            rs.close();
            ps.close();
            con.close();
            
            return list;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Get all results for a specific exam and student
     */
    public List<Result> getResultsByExamAndStudent(int examId, int studentId) throws Exception {
        Connection con = DBConnection.getConnection();

        try {
            String query = "SELECT student_id, exam_id, attempt_number, score FROM results WHERE exam_id = ? AND student_id = ? ORDER BY attempt_number ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, examId);
            ps.setInt(2, studentId);

            ResultSet rs = ps.executeQuery();

            List<Result> results = new ArrayList<>();

            while (rs.next()) {
                Result result = new Result(
                    rs.getInt("student_id"),
                    rs.getInt("exam_id"),
                    rs.getInt("attempt_number"),
                    rs.getInt("score")
                );
                results.add(result);
            }

            rs.close();
            ps.close();
            con.close();

            return results;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Check if student has taken an exam
     */
    public boolean hasStudentTakenExam(int examId, int studentId) throws Exception {
        Connection con = DBConnection.getConnection();

        try {
            String query = "SELECT COUNT(*) as count FROM results WHERE exam_id = ? AND student_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, examId);
            ps.setInt(2, studentId);

            ResultSet rs = ps.executeQuery();
            boolean hasResult = false;

            if (rs.next()) {
                hasResult = rs.getInt("count") > 0;
            }

            rs.close();
            ps.close();
            con.close();

            return hasResult;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }
}