package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseTeacherDAO {

    /**
     * Assign a teacher to a course
     */
    public void assignTeacher(int courseId, int teacherId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "INSERT INTO course_teachers (course_id, teacher_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

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
     * Get all courses a teacher is assigned to
     */
    public List<Integer> getCoursesByTeacher(int teacherId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT course_id FROM course_teachers WHERE teacher_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, teacherId);

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
     * Get all teachers assigned to a course
     */
    public List<Integer> getTeachersByCourse(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT teacher_id FROM course_teachers WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            List<Integer> teacherIds = new ArrayList<>();

            while (rs.next()) {
                teacherIds.add(rs.getInt("teacher_id"));
            }

            rs.close();
            ps.close();
            con.close();
            return teacherIds;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Check if teacher teaches a course
     */
    public boolean teacherTeachesCourse(int courseId, int teacherId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT COUNT(*) as count FROM course_teachers WHERE course_id = ? AND teacher_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

            ResultSet rs = ps.executeQuery();
            boolean teaches = false;

            if (rs.next()) {
                teaches = rs.getInt("count") > 0;
            }

            rs.close();
            ps.close();
            con.close();
            return teaches;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Remove teacher from course
     */
    public void removeTeacher(int courseId, int teacherId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "DELETE FROM course_teachers WHERE course_id = ? AND teacher_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

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
