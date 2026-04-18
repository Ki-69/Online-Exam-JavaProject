package dao;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public void saveResult(int studentId, int score) throws Exception {
        Connection con = DBConnection.getConnection();

        try {
            String query = "INSERT INTO results (student_id, score) VALUES (?, ?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, score);

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
            String query = "SELECT * FROM results";
            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            List<String> list = new ArrayList<>();

            while (rs.next()) {
                String row = "Student ID: " + rs.getInt("student_id") +
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
}