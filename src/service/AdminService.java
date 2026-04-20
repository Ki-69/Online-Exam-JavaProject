package service;

import dao.AdminDAO;
import model.*;
import java.util.*;

/**
 * AdminService - Business logic for admin operations
 * Handles validation and coordination between AdminDAO calls
 */
public class AdminService {

    private AdminDAO adminDAO = new AdminDAO();

    /**
     * Create a new course
     */
    public int createCourse(String courseName, String description) throws Exception {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new Exception("Course name cannot be empty");
        }
        return adminDAO.createCourse(courseName, description);
    }

    /**
     * Assign a teacher to a course
     */
    public void assignTeacherToCourse(int courseId, int teacherId) throws Exception {
        if (courseId <= 0 || teacherId <= 0) {
            throw new Exception("Invalid course ID or teacher ID");
        }
        adminDAO.assignTeacherToCourse(courseId, teacherId);
    }

    /**
     * Enroll a student in a course
     */
    public void enrollStudentInCourse(int courseId, int studentId) throws Exception {
        if (courseId <= 0 || studentId <= 0) {
            throw new Exception("Invalid course ID or student ID");
        }
        adminDAO.enrollStudentInCourse(courseId, studentId);
    }

    /**
     * Remove a teacher from a course
     */
    public void removeTeacherFromCourse(int courseId, int teacherId) throws Exception {
        if (courseId <= 0 || teacherId <= 0) {
            throw new Exception("Invalid course ID or teacher ID");
        }
        adminDAO.removeTeacherFromCourse(courseId, teacherId);
    }

    /**
     * Remove a student from a course
     */
    public void removeStudentFromCourse(int courseId, int studentId) throws Exception {
        if (courseId <= 0 || studentId <= 0) {
            throw new Exception("Invalid course ID or student ID");
        }
        adminDAO.removeStudentFromCourse(courseId, studentId);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() throws Exception {
        return adminDAO.getAllUsers();
    }

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() throws Exception {
        return adminDAO.getAllCourses();
    }

    /**
     * Get all enrollments
     */
    public List<Map<String, Object>> getAllEnrollments() throws Exception {
        return adminDAO.getAllEnrollments();
    }

    /**
     * Get all teacher assignments
     */
    public List<Map<String, Object>> getAllTeacherAssignments() throws Exception {
        return adminDAO.getAllTeacherAssignments();
    }

    /**
     * Get all results
     */
    public List<Map<String, Object>> getAllResults() throws Exception {
        return adminDAO.getAllResults();
    }

    /**
     * Get all students
     */
    public List<User> getStudents() throws Exception {
        return adminDAO.getStudents();
    }

    /**
     * Get all teachers
     */
    public List<User> getTeachers() throws Exception {
        return adminDAO.getTeachers();
    }

    /**
     * Get teachers for a specific course
     */
    public List<User> getTeachersForCourse(int courseId) throws Exception {
        return adminDAO.getTeachersForCourse(courseId);
    }

    /**
     * Get students enrolled in a course
     */
    public List<User> getStudentsInCourse(int courseId) throws Exception {
        return adminDAO.getStudentsInCourse(courseId);
    }
}