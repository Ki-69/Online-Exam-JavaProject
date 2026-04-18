package service;

import dao.QuestionDAO;
import dao.ResultDAO;
import model.Question;

import java.util.*;

public class ResultService {

    private QuestionDAO questionDAO = new QuestionDAO();
    private ResultDAO resultDAO = new ResultDAO();

    public int evaluateAndStore(int studentId, Map<Integer, String> answers) throws Exception {

        List<Question> questions = questionDAO.getAllQuestionsWithAnswers();

        int score = 0;

        for (Question q : questions) {
            String correct = q.getCorrectOption();
            String given = answers.get(q.getId());

            if (given != null && given.equalsIgnoreCase(correct)) {
                score += q.getMarks();
            }
        }

        resultDAO.saveResult(studentId, score);

        return score;
    }
}