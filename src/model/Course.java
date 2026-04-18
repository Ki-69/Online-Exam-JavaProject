package model;

public class Course {
    private int courseId;
    private String title;
    private int teacherId;

    public Course(int courseId, String title, int teacherId) {
        this.courseId = courseId;
        this.title = title;
        this.teacherId = teacherId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public int getTeacherId() {
        return teacherId;
    }
}
