package dao;

import db.DBConnection;
import model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT course_id, course_name, teacher_id FROM courses";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("teacher_id")
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
}
