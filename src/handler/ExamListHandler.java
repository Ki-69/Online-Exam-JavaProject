package handler;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Exam;
import service.ExamService;

public class ExamListHandler implements HttpHandler {

    private ExamService service = new ExamService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        Map<String, String> params = parseQuery(query);

        if (!params.containsKey("courseId")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        try {
            int courseId = Integer.parseInt(params.get("courseId"));
            List<Exam> exams = service.getExamsByCourseId(courseId);

            StringBuilder response = new StringBuilder();
            for (Exam exam : exams) {
                response.append(exam.getExamId())
                        .append("|")
                        .append(exam.getTitle())
                        .append("\n");
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
