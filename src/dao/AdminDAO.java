package dao;

import db.DBConnection;
import model.*;
import java.sql.*;
import java.util.*;

/**
 * AdminDAO - Handles all admin operations for system management
 * Responsibilities:
 * - Create courses
 * - Assign/remove teachers to/from courses
 * - Enroll/remove students from courses
 * - View all system data
 */
public class AdminDAO {

    /**
     * Create a new course
     */
    public int createCourse(String courseName, String description) throws Exception {
        String sql = "INSERT INTO courses (course_name, description) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, courseName);
            pstmt.setString(2, description);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt(1);
                    }
                }
            }
            conn.rollback();
            throw new Exception("Failed to create course");
        }
    }

    /**
     * Assign a teacher to a course
     */
    public void assignTeacherToCourse(int courseId, int teacherId) throws Exception {
        // Check if teacher is already assigned
        String checkSql = "SELECT id FROM course_teachers WHERE course_id = ? AND teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, courseId);
            checkStmt.setInt(2, teacherId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Teacher already assigned to this course");
                }
            }
        }

        // Assign teacher
        String sql = "INSERT INTO course_teachers (course_id, teacher_id) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, teacherId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Failed to assign teacher to course");
            }
        }
    }

    /**
     * Enroll a student in a course
     */
    public void enrollStudentInCourse(int courseId, int studentId) throws Exception {
        // Check if already enrolled
        String checkSql = "SELECT enrollment_id FROM course_enrollments WHERE course_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, courseId);
            checkStmt.setInt(2, studentId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Student already enrolled in this course");
                }
            }
        }

        // Enroll student
        String sql = "INSERT INTO course_enrollments (course_id, student_id) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, studentId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Failed to enroll student in course");
            }
        }
    }

    /**
     * Remove a teacher from a course
     */
    public void removeTeacherFromCourse(int courseId, int teacherId) throws Exception {
        String sql = "DELETE FROM course_teachers WHERE course_id = ? AND teacher_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, teacherId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Teacher not found in course or deletion failed");
            }
        }
    }

    /**
     * Remove a student from a course
     */
    public void removeStudentFromCourse(int courseId, int studentId) throws Exception {
        String sql = "DELETE FROM course_enrollments WHERE course_id = ? AND student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, studentId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Student not found in course or removal failed");
            }
        }
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        }
        
        return users;
    }

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() throws Exception {
        String sql = "SELECT course_id, course_name, description FROM courses ORDER BY course_id";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                courses.add(course);
            }
        }
        
        return courses;
    }

    /**
     * Get all course enrollments (student enrollments)
     */
    public List<Map<String, Object>> getAllEnrollments() throws Exception {
        String sql = """
            SELECT ce.enrollment_id, ce.course_id, ce.student_id, 
                   c.course_name, u.username
            FROM course_enrollments ce
            JOIN courses c ON ce.course_id = c.course_id
            JOIN users u ON ce.student_id = u.user_id
            ORDER BY ce.course_id, ce.student_id
            """;
        
        List<Map<String, Object>> enrollments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> enrollment = new HashMap<>();
                enrollment.put("enrollment_id", rs.getInt("enrollment_id"));
                enrollment.put("course_id", rs.getInt("course_id"));
                enrollment.put("student_id", rs.getInt("student_id"));
                enrollment.put("course_name", rs.getString("course_name"));
                enrollment.put("username", rs.getString("username"));
                enrollments.add(enrollment);
            }
        }
        
        return enrollments;
    }

    /**
     * Get all course teacher assignments
     */
    public List<Map<String, Object>> getAllTeacherAssignments() throws Exception {
        String sql = """
            SELECT ct.id, ct.course_id, ct.teacher_id,
                   c.course_name, u.username
            FROM course_teachers ct
            JOIN courses c ON ct.course_id = c.course_id
            JOIN users u ON ct.teacher_id = u.user_id
            ORDER BY ct.course_id, ct.teacher_id
            """;
        
        List<Map<String, Object>> assignments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("id", rs.getInt("id"));
                assignment.put("course_id", rs.getInt("course_id"));
                assignment.put("teacher_id", rs.getInt("teacher_id"));
                assignment.put("course_name", rs.getString("course_name"));
                assignment.put("username", rs.getString("username"));
                assignments.add(assignment);
            }
        }
        
        return assignments;
    }

    /**
     * Get all exam results
     */
    public List<Map<String, Object>> getAllResults() throws Exception {
        String sql = """
            SELECT a.attempt_id, a.student_id, a.exam_id, a.attempt_number, a.score,
                   u.username AS student_name, e.title, c.course_name
            FROM attempts a
            JOIN users u ON a.student_id = u.user_id
            JOIN exams e ON a.exam_id = e.exam_id
            JOIN courses c ON e.course_id = c.course_id
            ORDER BY a.exam_id, a.student_id, a.attempt_number
            """;
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("attempt_id", rs.getInt("attempt_id"));
                result.put("student_id", rs.getInt("student_id"));
                result.put("exam_id", rs.getInt("exam_id"));
                result.put("attempt_number", rs.getInt("attempt_number"));
                result.put("score", rs.getInt("score"));
                result.put("student_name", rs.getString("student_name"));
                result.put("exam_title", rs.getString("title"));
                result.put("course_name", rs.getString("course_name"));
                results.add(result);
            }
        }
        
        return results;
    }

    /**
     * Get students by role
     */
    public List<User> getStudents() throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users WHERE role = 'STUDENT' ORDER BY username";
        List<User> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                students.add(user);
            }
        }
        
        return students;
    }

    /**
     * Get teachers by role
     */
    public List<User> getTeachers() throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users WHERE role = 'TEACHER' ORDER BY username";
        List<User> teachers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                teachers.add(user);
            }
        }
        
        return teachers;
    }

    /**
     * Get teachers for a specific course
     */
    public List<User> getTeachersForCourse(int courseId) throws Exception {
        String sql = """
            SELECT DISTINCT u.user_id, u.username, u.password, u.role
            FROM users u
            JOIN course_teachers ct ON u.user_id = ct.teacher_id
            WHERE ct.course_id = ?
            ORDER BY u.username
            """;
        
        List<User> teachers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    teachers.add(user);
                }
            }
        }
        
        return teachers;
    }

    /**
     * Get students enrolled in a specific course
     */
    public List<User> getStudentsInCourse(int courseId) throws Exception {
        String sql = """
            SELECT DISTINCT u.user_id, u.username, u.password, u.role
            FROM users u
            JOIN course_enrollments ce ON u.user_id = ce.student_id
            WHERE ce.course_id = ?
            ORDER BY u.username
            """;
        
        List<User> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    students.add(user);
                }
            }
        }
        
        return students;
    }
}
