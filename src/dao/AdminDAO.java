package dao;

import db.DBConnection;
import java.sql.*;
import java.util.*;
import model.*;

public class AdminDAO {

    // ---------------- CREATE COURSE ----------------
    public int createCourse(String courseName, String description) throws Exception {
        String sql = "INSERT INTO courses (course_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, courseName);
            pstmt.setString(2, description);

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt(1);
                    }
                }
            }
            conn.rollback();
            throw new Exception("Course creation failed");
        }
    }

    // ---------------- ASSIGN TEACHER ----------------
    public void assignTeacherToCourse(int courseId, int teacherId) throws Exception {
        String check = "SELECT id FROM course_teachers WHERE course_id=? AND teacher_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {

            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

            if (ps.executeQuery().next()) {
                throw new Exception("Teacher already assigned");
            }
        }

        String sql = "INSERT INTO course_teachers (course_id, teacher_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

            if (ps.executeUpdate() > 0) conn.commit();
            else {
                conn.rollback();
                throw new Exception("Assign failed");
            }
        }
    }

    // ---------------- ENROLL STUDENT ----------------
    public void enrollStudentInCourse(int courseId, int studentId) throws Exception {
        String check = "SELECT id FROM course_enrollments WHERE course_id=? AND student_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {

            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            if (ps.executeQuery().next()) {
                throw new Exception("Already enrolled");
            }
        }

        String sql = "INSERT INTO course_enrollments (course_id, student_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            if (ps.executeUpdate() > 0) conn.commit();
            else {
                conn.rollback();
                throw new Exception("Enroll failed");
            }
        }
    }

    // ---------------- REMOVE ----------------
    public void removeTeacherFromCourse(int courseId, int teacherId) throws Exception {
        String sql = "DELETE FROM course_teachers WHERE course_id=? AND teacher_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, teacherId);

            if (ps.executeUpdate() > 0) conn.commit();
            else throw new Exception("Remove failed");
        }
    }

    public void removeStudentFromCourse(int courseId, int studentId) throws Exception {
        String sql = "DELETE FROM course_enrollments WHERE course_id=? AND student_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            if (ps.executeUpdate() > 0) conn.commit();
            else throw new Exception("Remove failed");
        }
    }

    // ---------------- USERS ----------------
    public List<User> getAllUsers() throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users ORDER BY user_id";
        List<User> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        }
        return list;
    }

    public List<User> getStudents() throws Exception {
        return getUsersByRole("STUDENT");
    }

    public List<User> getTeachers() throws Exception {
        return getUsersByRole("TEACHER");
    }

    private List<User> getUsersByRole(String role) throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users WHERE role=?";
        List<User> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    // ---------------- COURSES ----------------
    public List<Course> getAllCourses() throws Exception {
        String sql = "SELECT course_id, course_name, description FROM courses";
        List<Course> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Course cObj = new Course();
                cObj.setCourseId(rs.getInt("course_id"));
                cObj.setCourseName(rs.getString("course_name"));
                cObj.setDescription(rs.getString("description"));
                list.add(cObj);
            }
        }
        return list;
    }

    // ---------------- ENROLLMENTS ----------------
    public List<Map<String,Object>> getAllEnrollments() throws Exception {
        String sql = """
            SELECT ce.id, ce.course_id, ce.student_id,
                   c.course_name, u.username AS student_name
            FROM course_enrollments ce
            JOIN courses c ON ce.course_id = c.course_id
            JOIN users u ON ce.student_id = u.user_id
        """;

        List<Map<String,Object>> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("course_id", rs.getInt("course_id"));
                m.put("student_id", rs.getInt("student_id"));
                m.put("course_name", rs.getString("course_name"));
                m.put("student_name", rs.getString("student_name"));
                list.add(m);
            }
        }
        return list;
    }

    // ---------------- TEACHER ASSIGNMENTS ----------------
    public List<Map<String,Object>> getAllTeacherAssignments() throws Exception {
        String sql = """
            SELECT ct.id, ct.course_id, ct.teacher_id,
                   c.course_name, u.username AS teacher_name
            FROM course_teachers ct
            JOIN courses c ON ct.course_id = c.course_id
            JOIN users u ON ct.teacher_id = u.user_id
        """;

        List<Map<String,Object>> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("course_id", rs.getInt("course_id"));
                m.put("teacher_id", rs.getInt("teacher_id"));
                m.put("course_name", rs.getString("course_name"));
                m.put("teacher_name", rs.getString("teacher_name"));
                list.add(m);
            }
        }
        return list;
    }

    // ---------------- COURSE FILTERS ----------------
    public List<User> getTeachersForCourse(int courseId) throws Exception {
        String sql = """
            SELECT u.user_id, u.username, u.password, u.role
            FROM users u
            JOIN course_teachers ct ON u.user_id = ct.teacher_id
            WHERE ct.course_id = ?
        """;

        List<User> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    public List<User> getStudentsInCourse(int courseId) throws Exception {
        String sql = """
            SELECT u.user_id, u.username, u.password, u.role
            FROM users u
            JOIN course_enrollments ce ON u.user_id = ce.student_id
            WHERE ce.course_id = ?
        """;

        List<User> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    // ---------------- RESULTS ----------------
    public List<Map<String,Object>> getAllResults() throws Exception {
        String sql = """
            SELECT a.score, a.attempt_number,
                   u.username AS student_name,
                   e.title AS exam_title,
                   c.course_name
            FROM attempts a
            JOIN users u ON a.student_id = u.user_id
            JOIN exams e ON a.exam_id = e.exam_id
            JOIN courses c ON e.course_id = c.course_id
        """;

        List<Map<String,Object>> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("student_name", rs.getString("student_name"));
                m.put("exam_title", rs.getString("exam_title"));
                m.put("course_name", rs.getString("course_name"));
                m.put("score", rs.getInt("score"));
                m.put("attempt_number", rs.getInt("attempt_number"));
                list.add(m);
            }
        }
        return list;
    }
}