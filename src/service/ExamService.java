package service;

import dao.QuestionDAO;
import model.Question;

import java.util.List;

public class ExamService {

    private QuestionDAO dao = new QuestionDAO();

    public List<Question> getQuestions() throws Exception {
        return dao.getAllQuestionsWithAnswers();
    }
}