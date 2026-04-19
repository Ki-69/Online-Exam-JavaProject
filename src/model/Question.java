package model;

public class Question {

    private int id;
    private int questionId;
    private int examId;
    private String text;
    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctOption;
    private int marks;
    private int questionOrder;

    // No-arg constructor
    public Question() {
    }

    public Question(int id, String text, String a, String b, String c, String d) {
        this.id = id;
        this.questionId = id;
        this.text = text;
        this.questionText = text;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
    }

    public int getId() { 
        return id; 
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getText() { 
        return text; 
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() { 
        return optionA; 
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() { 
        return optionB; 
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() { 
        return optionC; 
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() { 
        return optionD; 
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() { 
        return correctOption; 
    }

    public void setCorrectOption(String c) { 
        this.correctOption = c; 
    }

    public int getMarks() { 
        return marks; 
    }

    public void setMarks(int m) { 
        this.marks = m; 
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }
}