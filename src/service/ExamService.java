package service;

import dao.ExamDAO;
import dao.QuestionDAO;
import model.Exam;
import model.Question;

import java.util.List;

public class ExamService {

    private QuestionDAO dao = new QuestionDAO();
    private ExamDAO examDAO = new ExamDAO();

    public List<Question> getQuestionsByExamId(int examId) throws Exception {
        return dao.getQuestionsByExamId(examId);
    }

    public List<Exam> getExamsByCourseId(int courseId) throws Exception {
        return examDAO.getExamsByCourseId(courseId);
    }
}