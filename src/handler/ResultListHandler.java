package handler;

import com.sun.net.httpserver.*;
import dao.ResultDAO;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Result;

public class ResultListHandler implements HttpHandler {

    private ResultDAO resultDAO = new ResultDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        Map<String, String> params = parseQuery(query);

        if (!params.containsKey("examId") || !params.containsKey("studentId")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        try {
            int examId = Integer.parseInt(params.get("examId"));
            int studentId = Integer.parseInt(params.get("studentId"));

            List<Result> results = resultDAO.getResultsByExamAndStudent(examId, studentId);

            StringBuilder response = new StringBuilder();
            
            if (results.isEmpty()) {
                // No results found
                response.append("");
            } else {
                // Format: attempt1_score|attempt2_score|attempt3_score
                for (int i = 0; i < results.size(); i++) {
                    if (i > 0) response.append("|");
                    response.append(results.get(i).getScore());
                }
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
