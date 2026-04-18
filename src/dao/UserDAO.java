package dao;

import db.DBConnection;
import model.User;

import java.sql.*;

public class UserDAO {

    public User getUser(String username, String password) throws Exception {

        Connection con = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM users WHERE username=? AND password=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            User user = null;
            if (rs.next()) {
                user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")
                );
            }

            rs.close();
            ps.close();
            con.close();
            
            return user;
        } catch (Exception e) {
            con.close();
            throw e;
        }
    }
}