package model;

public class Attempt {
    private int attemptId;
    private int studentId;
    private int examId;
    private int attemptNumber;
    private int score;

    public Attempt(int attemptId, int studentId, int examId, int attemptNumber, int score) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.examId = examId;
        this.attemptNumber = attemptNumber;
        this.score = score;
    }

    public int getAttemptId() { return attemptId; }
    public int getStudentId() { return studentId; }
    public int getExamId() { return examId; }
    public int getAttemptNumber() { return attemptNumber; }
    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }
}
