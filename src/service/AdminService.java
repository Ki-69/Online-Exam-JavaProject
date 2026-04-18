package service;

import dao.QuestionDAO;
import dao.ResultDAO;

import java.util.List;

public class AdminService {

    private ResultDAO resultDAO = new ResultDAO();
    private QuestionDAO questionDAO = new QuestionDAO();

    public List<String> getAllResults() throws Exception {
        return resultDAO.getAllResults();
    }

    public void addQuestion(int examId, String q, String a, String b, String c, String d, String correct, int marks) throws Exception {
        questionDAO.insertQuestion(examId, q, a, b, c, d, correct, marks);
    }
}