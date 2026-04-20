package service;

import dao.TeacherDAO;
import model.Course;
import model.Exam;
import model.Question;
import java.util.List;
import java.util.Map;

/**
 * TeacherService - Business logic layer for teacher operations
 * Responsibilities:
 * - Validate all teacher operations (input validation, authorization)
 * - Delegate database operations to TeacherDAO
 * - Ensure teachers only access their courses and exams
 */
public class TeacherService {

    private TeacherDAO teacherDAO = new TeacherDAO();

    /**
     * Get all courses assigned to a teacher
     * Validates: teacherId > 0
     */
    public List<Course> getCoursesByTeacher(int teacherId) throws Exception {
        if (teacherId <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID: " + teacherId);
        }
        return teacherDAO.getCoursesByTeacher(teacherId);
    }

    /**
     * Check if a teacher teaches a specific course
     * Used for authorization checks before operations
     */
    public boolean canTeacherAccessCourse(int teacherId, int courseId) throws Exception {
        if (teacherId <= 0 || courseId <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID or course ID");
        }
        return teacherDAO.teacherTeachesCourse(teacherId, courseId);
    }

    /**
     * Create a new exam for a course
     * Validates:
     * - courseId > 0
     * - title is not empty
     * - duration > 0
     * - maxAttempts >= 1
     * - createdBy (teacherId) > 0
     * - Teacher teaches the course
     */
    public int createExam(int courseId, String title, int duration, int maxAttempts, int createdBy) throws Exception {
        // Input validation
        if (courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID: " + courseId);
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam title cannot be empty");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Exam duration must be greater than 0 minutes");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Maximum attempts must be at least 1");
        }
        if (createdBy <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID: " + createdBy);
        }
        
        // Authorization check: verify teacher teaches this course
        if (!teacherDAO.teacherTeachesCourse(createdBy, courseId)) {
            throw new SecurityException("Teacher ID " + createdBy + " does not teach course ID " + courseId);
        }
        
        return teacherDAO.createExam(courseId, title, duration, maxAttempts, createdBy);
    }

    /**
     * Get all exams for a course
     * Validates: courseId > 0
     */
    public List<Exam> getExamsByCourse(int courseId) throws Exception {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID: " + courseId);
        }
        return teacherDAO.getExamsByCourse(courseId);
    }

    /**
     * Get details of a specific exam
     * Validates: examId > 0
     */
    public Exam getExamById(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        return teacherDAO.getExamById(examId);
    }

    /**
     * Add a question to an exam
     * Validates:
     * - examId > 0
     * - questionText is not empty
     * - All options (A, B, C, D) are not empty
     * - correctOption is one of: A, B, C, D
     * - marks > 0
     * - questionOrder > 0
     */
    public int addQuestion(int examId, String questionText, String optionA, String optionB, 
                           String optionC, String optionD, String correctOption, int marks, int questionOrder) throws Exception {
        // Input validation
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        if (optionA == null || optionA.trim().isEmpty() ||
            optionB == null || optionB.trim().isEmpty() ||
            optionC == null || optionC.trim().isEmpty() ||
            optionD == null || optionD.trim().isEmpty()) {
            throw new IllegalArgumentException("All options (A, B, C, D) must be provided");
        }
        if (correctOption == null || !correctOption.matches("[ABCD]")) {
            throw new IllegalArgumentException("Correct option must be A, B, C, or D");
        }
        if (marks <= 0) {
            throw new IllegalArgumentException("Marks must be greater than 0");
        }
        if (questionOrder <= 0) {
            throw new IllegalArgumentException("Question order must be greater than 0");
        }
        
        return teacherDAO.addQuestion(examId, questionText, optionA, optionB, optionC, optionD, correctOption, marks, questionOrder);
    }

    /**
     * Get all questions for an exam (ordered by question_order)
     * Validates: examId > 0
     */
    public List<Question> getQuestionsByExam(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        return teacherDAO.getQuestionsByExam(examId);
    }

    /**
     * Update a question
     * Validates: Same as addQuestion, plus questionId > 0
     */
    public void updateQuestion(int questionId, String questionText, String optionA, String optionB,
                               String optionC, String optionD, String correctOption, int marks) throws Exception {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID: " + questionId);
        }
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        if (optionA == null || optionA.trim().isEmpty() ||
            optionB == null || optionB.trim().isEmpty() ||
            optionC == null || optionC.trim().isEmpty() ||
            optionD == null || optionD.trim().isEmpty()) {
            throw new IllegalArgumentException("All options (A, B, C, D) must be provided");
        }
        if (correctOption == null || !correctOption.matches("[ABCD]")) {
            throw new IllegalArgumentException("Correct option must be A, B, C, or D");
        }
        if (marks <= 0) {
            throw new IllegalArgumentException("Marks must be greater than 0");
        }
        
        teacherDAO.updateQuestion(questionId, questionText, optionA, optionB, optionC, optionD, correctOption, marks);
    }

    /**
     * Delete a question
     * Validates: questionId > 0
     */
    public void deleteQuestion(int questionId) throws Exception {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID: " + questionId);
        }
        teacherDAO.deleteQuestion(questionId);
    }

    /**
     * Get results for a specific exam
     * Returns List<Map> with result details and student names
     * Validates: examId > 0
     */
    public List<Map<String, Object>> getResultsByExam(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        return teacherDAO.getResultsByExam(examId);
    }

    /**
     * Get all results for a teacher (across all their exams)
     * Returns List<Map> with result details, exam info, and student names
     * Validates: teacherId > 0
     */
    public List<Map<String, Object>> getResultsByTeacher(int teacherId) throws Exception {
        if (teacherId <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID: " + teacherId);
        }
        return teacherDAO.getResultsByTeacher(teacherId);
    }

    /**
     * Delete an exam (and all associated questions and results)
     * Validates: examId > 0
     */
    public void deleteExam(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        teacherDAO.deleteExam(examId);
    }

    /**
     * Get count of questions in an exam
     * Validates: examId > 0
     */
    public int getQuestionCount(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Invalid exam ID: " + examId);
        }
        return teacherDAO.getQuestionCount(examId);
    }
}
