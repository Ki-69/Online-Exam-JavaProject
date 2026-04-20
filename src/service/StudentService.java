package service;

import dao.CourseDAO;
import dao.CourseEnrollmentDAO;
import dao.ExamDAO;
import dao.QuestionDAO;
import java.util.ArrayList;
import java.util.List;
import model.Course;
import model.Exam;
import model.Question;

public class StudentService {

    private CourseEnrollmentDAO enrollmentDAO = new CourseEnrollmentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ExamDAO examDAO = new ExamDAO();
    private QuestionDAO questionDAO = new QuestionDAO();

    /**
     * Get all courses student is enrolled in
     */
    public List<Course> getMyEnrolledCourses(int studentId) throws Exception {
        List<Integer> courseIds = enrollmentDAO.getCoursesByStudent(studentId);
        List<Course> courses = new ArrayList<>();

        for (int courseId : courseIds) {
            Course course = courseDAO.getCourseById(courseId);
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }

    /**
     * Get exams for a specific course
     */
    public List<Exam> getExamsByCourseid(int courseId) throws Exception {
        return examDAO.getExamsByCourseId(courseId);
    }

    /**
     * Get questions for an exam
     */
    public List<Question> getExamQuestions(int examId) throws Exception {
        return questionDAO.getQuestionsByExamId(examId);
    }

    /**
     * Check if student is enrolled in a course
     */
    public boolean isEnrolledInCourse(int courseId, int studentId) throws Exception {
        return enrollmentDAO.isEnrolled(courseId, studentId);
    }
}
