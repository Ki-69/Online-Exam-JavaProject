package dao;

import db.DBConnection;
import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public List<Question> getAllQuestionsWithAnswers() throws Exception {

        Connection con = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM questions";

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            List<Question> list = new ArrayList<>();

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                );

                q.setCorrectOption(rs.getString("correct_option"));
                q.setMarks(rs.getInt("marks"));

                list.add(q);
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

    public void insertQuestion(String q, String a, String b, String c, String d, String correct, int marks) throws Exception {

        Connection con = DBConnection.getConnection();

        try {
            String query = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, marks) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, q);
            ps.setString(2, a);
            ps.setString(3, b);
            ps.setString(4, c);
            ps.setString(5, d);
            ps.setString(6, correct);
            ps.setInt(7, marks);

            ps.executeUpdate();
            ps.close();
            con.commit();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}