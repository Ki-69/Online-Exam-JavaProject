package handler;

import com.sun.net.httpserver.*;
import service.ExamService;
import model.Question;

import java.io.*;
import java.util.List;

public class ExamHandler implements HttpHandler {

    private ExamService service = new ExamService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            List<Question> questions = service.getQuestions();

            StringBuilder response = new StringBuilder();

            for (Question q : questions) {
                response.append(q.getId()).append("|")
                        .append(q.getText()).append("|")
                        .append(q.getOptionA()).append("|")
                        .append(q.getOptionB()).append("|")
                        .append(q.getOptionC()).append("|")
                        .append(q.getOptionD()).append("\n");
            }

            byte[] bytes = response.toString().getBytes();

            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}