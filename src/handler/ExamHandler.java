package handler;

import com.sun.net.httpserver.*;
import service.ExamService;
import model.Question;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

public class ExamHandler implements HttpHandler {

    private ExamService service = new ExamService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI requestURI = exchange.getRequestURI();
        Map<String, String> params = parseQuery(requestURI.getQuery());

        if (!params.containsKey("examId")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        try {
            int examId = Integer.parseInt(params.get("examId"));
            List<Question> questions = service.getQuestionsByExamId(examId);

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
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}