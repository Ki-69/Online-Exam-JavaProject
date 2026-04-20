package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseEnrollmentDAO {

    /**
     * Enroll a student in a course
     */
    public void enrollStudent(int courseId, int studentId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "INSERT INTO course_enrollments (course_id, student_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

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
     * Get all courses a student is enrolled in
     */
    public List<Integer> getCoursesByStudent(int studentId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT course_id FROM course_enrollments WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            List<Integer> courseIds = new ArrayList<>();

            while (rs.next()) {
                courseIds.add(rs.getInt("course_id"));
            }

            rs.close();
            ps.close();
            con.close();
            return courseIds;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Get all students enrolled in a course
     */
    public List<Integer> getStudentsByCourse(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT student_id FROM course_enrollments WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            List<Integer> studentIds = new ArrayList<>();

            while (rs.next()) {
                studentIds.add(rs.getInt("student_id"));
            }

            rs.close();
            ps.close();
            con.close();
            return studentIds;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Check if student is enrolled in course
     */
    public boolean isEnrolled(int courseId, int studentId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT COUNT(*) as count FROM course_enrollments WHERE course_id = ? AND student_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            ResultSet rs = ps.executeQuery();
            boolean enrolled = false;

            if (rs.next()) {
                enrolled = rs.getInt("count") > 0;
            }

            rs.close();
            ps.close();
            con.close();
            return enrolled;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Remove student enrollment from course
     */
    public void removeEnrollment(int courseId, int studentId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "DELETE FROM course_enrollments WHERE course_id = ? AND student_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

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
