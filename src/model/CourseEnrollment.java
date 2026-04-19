package model;

public class CourseEnrollment {
    private int enrollmentId;
    private int courseId;
    private int studentId;

    public CourseEnrollment(int enrollmentId, int courseId, int studentId) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public int getEnrollmentId() { return enrollmentId; }
    public int getCourseId() { return courseId; }
    public int getStudentId() { return studentId; }
}
