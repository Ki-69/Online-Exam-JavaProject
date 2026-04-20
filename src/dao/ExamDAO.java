package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Exam;

public class ExamDAO {

    public List<Exam> getExamsByCourseId(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT exam_id, course_id, title, COALESCE(max_attempts, 1) as max_attempts FROM exams WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            List<Exam> exams = new ArrayList<>();

            while (rs.next()) {
                exams.add(new Exam(
                        rs.getInt("exam_id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getInt("max_attempts")
                ));
            }

            rs.close();
            ps.close();
            con.close();
            return exams;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Get exam by ID
     */
    public Exam getExamById(int examId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT exam_id, course_id, title, COALESCE(max_attempts, 1) as max_attempts FROM exams WHERE exam_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, examId);

            ResultSet rs = ps.executeQuery();
            Exam exam = null;

            if (rs.next()) {
                exam = new Exam(
                        rs.getInt("exam_id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getInt("max_attempts")
                );
            }

            rs.close();
            ps.close();
            con.close();
            return exam;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Create a new exam
     */
    public int createExam(int courseId, String title, int duration, int maxAttempts, int createdBy) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "INSERT INTO exams (course_id, title, duration, max_attempts, created_by) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, courseId);
            ps.setString(2, title);
            ps.setInt(3, duration);
            ps.setInt(4, maxAttempts);
            ps.setInt(5, createdBy);

            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int examId = 0;
            if (generatedKeys.next()) {
                examId = generatedKeys.getInt(1);
            }

            ps.close();
            con.commit();
            con.close();
            return examId;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Get max attempts for an exam
     */
    public int getMaxAttempts(int examId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT COALESCE(max_attempts, 1) as max_attempts FROM exams WHERE exam_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, examId);

            ResultSet rs = ps.executeQuery();
            int maxAttempts = 1;

            if (rs.next()) {
                maxAttempts = rs.getInt("max_attempts");
            }

            rs.close();
            ps.close();
            con.close();
            return maxAttempts;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Update max attempts for an exam
     */
    public void updateMaxAttempts(int examId, int maxAttempts) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "UPDATE exams SET max_attempts = ? WHERE exam_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, maxAttempts);
            ps.setInt(2, examId);

            ps.executeUpdate();
            ps.close();
            con.commit();
            con.close();
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Delete exam
     */
    public void deleteExam(int examId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "DELETE FROM exams WHERE exam_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, examId);

            ps.executeUpdate();
            ps.close();
            con.commit();
            con.close();
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }
}
