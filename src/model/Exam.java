package model;

import java.time.LocalDateTime;

public class Exam {
    private int examId;
    private int courseId;
    private String title;
    private LocalDateTime dateTime;
    private int duration; // in minutes
    private int maxAttempts;
    private int createdBy; // teacher/admin ID

    // No-arg constructor
    public Exam() {
        this.maxAttempts = 1;
    }

    // Constructor for basic info
    public Exam(int examId, int courseId, String title) {
        this.examId = examId;
        this.courseId = courseId;
        this.title = title;
        this.maxAttempts = 1;
    }

    // Constructor with max attempts
    public Exam(int examId, int courseId, String title, int maxAttempts) {
        this.examId = examId;
        this.courseId = courseId;
        this.title = title;
        this.maxAttempts = maxAttempts;
    }

    // Constructor with all fields
    public Exam(int examId, int courseId, String title, LocalDateTime dateTime, int duration, int maxAttempts, int createdBy) {
        this.examId = examId;
        this.courseId = courseId;
        this.title = title;
        this.dateTime = dateTime;
        this.duration = duration;
        this.maxAttempts = maxAttempts;
        this.createdBy = createdBy;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
