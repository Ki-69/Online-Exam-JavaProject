package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CourseDAO {

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT course_id, course_name, description FROM courses";
            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();

            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description")
                ));
            }

            rs.close();
            ps.close();
            con.close();
            return courses;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Get course by ID
     */
    public Course getCourseById(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT course_id, course_name, description FROM courses WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            Course course = null;

            if (rs.next()) {
                course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description")
                );
            }

            rs.close();
            ps.close();
            con.close();
            return course;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Create a new course
     */
    public int createCourse(String courseName, String description) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "INSERT INTO courses (course_name, description) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, courseName);
            ps.setString(2, description);

            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int courseId = 0;
            if (generatedKeys.next()) {
                courseId = generatedKeys.getInt(1);
            }

            ps.close();
            con.commit();
            con.close();
            return courseId;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }

    /**
     * Update course
     */
    public void updateCourse(int courseId, String courseName, String description) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "UPDATE courses SET course_name = ?, description = ? WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, courseName);
            ps.setString(2, description);
            ps.setInt(3, courseId);

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
     * Delete course
     */
    public void deleteCourse(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "DELETE FROM courses WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

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
