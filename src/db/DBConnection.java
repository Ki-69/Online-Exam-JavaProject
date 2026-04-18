package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {

        String url = "jdbc:mysql://localhost:3306/online_exam_v2";
        String user = "root";
        String password = "tsukasa911";

        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection con = DriverManager.getConnection(url, user, password);
        con.setAutoCommit(false);  // Disable auto-commit for manual transaction control
        
        return con;
    }
}