package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Exam;

public class ExamDAO {

    public List<Exam> getExamsByCourseId(int courseId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            String query = "SELECT exam_id, course_id, exam_name FROM exams WHERE course_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            List<Exam> exams = new ArrayList<>();

            while (rs.next()) {
                exams.add(new Exam(
                        rs.getInt("exam_id"),
                        rs.getInt("course_id"),
                        rs.getString("exam_name")
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
}
