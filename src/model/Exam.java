package model;

public class Exam {
    private int examId;
    private int courseId;
    private String title;

    public Exam(int examId, int courseId, String title) {
        this.examId = examId;
        this.courseId = courseId;
        this.title = title;
    }

    public int getExamId() {
        return examId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }
}
