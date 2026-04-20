package model;

public class CourseTeacher {
    private int id;
    private int courseId;
    private int teacherId;

    public CourseTeacher(int id, int courseId, int teacherId) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public int getTeacherId() { return teacherId; }
}
