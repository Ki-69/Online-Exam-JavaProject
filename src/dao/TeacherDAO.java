package dao;

import db.DBConnection;
import model.*;
import java.sql.*;
import java.util.*;

/**
 * TeacherDAO - Handles all teacher-specific operations
 * Responsibilities:
 * - View assigned courses
 * - Create and manage exams
 * - Add questions to exams
 * - View results for their exams
 */
public class TeacherDAO {

    /**
     * Get all courses assigned to a teacher
     */
    public List<Course> getCoursesByTeacher(int teacherId) throws Exception {
        String sql = """
            SELECT DISTINCT c.course_id, c.course_name, c.description
            FROM courses c
            JOIN course_teachers ct ON c.course_id = ct.course_id
            WHERE ct.teacher_id = ?
            ORDER BY c.course_name
            """;
        
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course();
                    course.setCourseId(rs.getInt("course_id"));
                    course.setCourseName(rs.getString("course_name"));
                    course.setDescription(rs.getString("description"));
                    courses.add(course);
                }
            }
        }
        
        return courses;
    }

    /**
     * Verify teacher teaches a course (for authorization)
     */
    public boolean teacherTeachesCourse(int teacherId, int courseId) throws Exception {
        String sql = "SELECT id FROM course_teachers WHERE teacher_id = ? AND course_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Create a new exam
     */
    public int createExam(int courseId, String title, int duration, int maxAttempts, int createdBy) throws Exception {
        String sql = """
            INSERT INTO exams (course_id, title, duration, max_attempts, created_by)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setString(2, title);
            pstmt.setInt(3, duration);
            pstmt.setInt(4, maxAttempts);
            pstmt.setInt(5, createdBy);
            
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
            throw new Exception("Failed to create exam");
        }
    }

    /**
     * Get all exams for a course
     */
    public List<Exam> getExamsByCourse(int courseId) throws Exception {
        String sql = """
            SELECT exam_id, course_id, title, duration, max_attempts, created_by
            FROM exams
            WHERE course_id = ?
            ORDER BY exam_id DESC
            """;
        
        List<Exam> exams = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Exam exam = new Exam();
                    exam.setExamId(rs.getInt("exam_id"));
                    exam.setCourseId(rs.getInt("course_id"));
                    exam.setTitle(rs.getString("title"));
                    exam.setDuration(rs.getInt("duration"));
                    exam.setMaxAttempts(rs.getInt("max_attempts"));
                    exam.setCreatedBy(rs.getInt("created_by"));
                    exams.add(exam);
                }
            }
        }
        
        return exams;
    }

    /**
     * Get exam by ID
     */
    public Exam getExamById(int examId) throws Exception {
        String sql = """
            SELECT exam_id, course_id, title, duration, max_attempts, created_by
            FROM exams
            WHERE exam_id = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, examId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Exam exam = new Exam();
                    exam.setExamId(rs.getInt("exam_id"));
                    exam.setCourseId(rs.getInt("course_id"));
                    exam.setTitle(rs.getString("title"));
                    exam.setDuration(rs.getInt("duration"));
                    exam.setMaxAttempts(rs.getInt("max_attempts"));
                    exam.setCreatedBy(rs.getInt("created_by"));
                    return exam;
                }
            }
        }
        
        return null;
    }

    /**
     * Add a question to an exam
     */
    public int addQuestion(int examId, String questionText, String optionA, String optionB, 
                          String optionC, String optionD, String correctOption, int marks, int questionOrder) throws Exception {
        String sql = """
            INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, question_order)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, examId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, optionA);
            pstmt.setString(4, optionB);
            pstmt.setString(5, optionC);
            pstmt.setString(6, optionD);
            pstmt.setString(7, correctOption);
            pstmt.setInt(8, marks);
            pstmt.setInt(9, questionOrder);
            
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
            throw new Exception("Failed to add question");
        }
    }

    /**
     * Get all questions for an exam
     */
    public List<Question> getQuestionsByExam(int examId) throws Exception {
        String sql = """
            SELECT question_id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, question_order
            FROM questions
            WHERE exam_id = ?
            ORDER BY question_order ASC
            """;
        
        List<Question> questions = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, examId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setQuestionId(rs.getInt("question_id"));
                    question.setExamId(rs.getInt("exam_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setOptionA(rs.getString("option_a"));
                    question.setOptionB(rs.getString("option_b"));
                    question.setOptionC(rs.getString("option_c"));
                    question.setOptionD(rs.getString("option_d"));
                    question.setCorrectOption(rs.getString("correct_option"));
                    question.setMarks(rs.getInt("marks"));
                    question.setQuestionOrder(rs.getInt("question_order"));
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }

    /**
     * Delete a question
     */
    public void deleteQuestion(int questionId) throws Exception {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Question not found");
            }
        }
    }

    /**
     * Update a question
     */
    public void updateQuestion(int questionId, String questionText, String optionA, String optionB,
                              String optionC, String optionD, String correctOption, int marks) throws Exception {
        String sql = """
            UPDATE questions
            SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ?, marks = ?
            WHERE question_id = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, questionText);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, correctOption);
            pstmt.setInt(7, marks);
            pstmt.setInt(8, questionId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Question not found or update failed");
            }
        }
    }

    /**
     * Get all results (attempts) for an exam
     */
    public List<Map<String, Object>> getResultsByExam(int examId) throws Exception {
        String sql = """
            SELECT a.attempt_id, a.student_id, a.exam_id, a.attempt_number, a.score,
                   u.username AS student_name
            FROM attempts a
            JOIN users u ON a.student_id = u.user_id
            WHERE a.exam_id = ?
            ORDER BY a.student_id, a.attempt_number
            """;
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, examId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("attempt_id", rs.getInt("attempt_id"));
                    result.put("student_id", rs.getInt("student_id"));
                    result.put("exam_id", rs.getInt("exam_id"));
                    result.put("attempt_number", rs.getInt("attempt_number"));
                    result.put("score", rs.getInt("score"));
                    result.put("student_name", rs.getString("student_name"));
                    results.add(result);
                }
            }
        }
        
        return results;
    }

    /**
     * Get results for all exams created by a teacher
     */
    public List<Map<String, Object>> getResultsByTeacher(int teacherId) throws Exception {
        String sql = """
            SELECT a.attempt_id, a.student_id, a.exam_id, a.attempt_number, a.score,
                   u.username AS student_name, e.title AS exam_title, c.course_name
            FROM attempts a
            JOIN users u ON a.student_id = u.user_id
            JOIN exams e ON a.exam_id = e.exam_id
            JOIN courses c ON e.course_id = c.course_id
            WHERE e.created_by = ?
            ORDER BY e.exam_id, a.student_id, a.attempt_number
            """;
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("attempt_id", rs.getInt("attempt_id"));
                    result.put("student_id", rs.getInt("student_id"));
                    result.put("exam_id", rs.getInt("exam_id"));
                    result.put("attempt_number", rs.getInt("attempt_number"));
                    result.put("score", rs.getInt("score"));
                    result.put("student_name", rs.getString("student_name"));
                    result.put("exam_title", rs.getString("exam_title"));
                    result.put("course_name", rs.getString("course_name"));
                    results.add(result);
                }
            }
        }
        
        return results;
    }

    /**
     * Delete an exam (and cascade deletes questions, attempts, answers)
     */
    public void deleteExam(int examId) throws Exception {
        String sql = "DELETE FROM exams WHERE exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, examId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Exam not found");
            }
        }
    }

    /**
     * Count total questions for an exam
     */
    public int getQuestionCount(int examId) throws Exception {
        String sql = "SELECT COUNT(*) as count FROM questions WHERE exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, examId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        
        return 0;
    }
}
