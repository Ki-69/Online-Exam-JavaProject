package model;

/**
 * Result model - Represents an exam result/attempt
 */
public class Result {
    private int resultId;
    private int studentId;
    private int examId;
    private int score;

    public Result() {
    }

    public Result(int studentId, int examId, int score) {
        this.studentId = studentId;
        this.examId = examId;
        this.score = score;
    }

    public Result(int resultId, int studentId, int examId, int score) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.examId = examId;
        this.score = score;
    }

    // Getters and Setters
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", studentId=" + studentId +
                ", examId=" + examId +
                ", score=" + score +
                '}';
    }
}
