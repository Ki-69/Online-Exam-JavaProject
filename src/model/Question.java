package model;

public class Question {

    private int id;
    private String text;
    private String optionA, optionB, optionC, optionD;

    private String correctOption;
    private int marks;

    public Question(int id, String text, String a, String b, String c, String d) {
        this.id = id;
        this.text = text;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String c) { this.correctOption = c; }

    public int getMarks() { return marks; }
    public void setMarks(int m) { this.marks = m; }
}