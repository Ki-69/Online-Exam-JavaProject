package handler;

import com.sun.net.httpserver.*;
import service.ResultService;

import java.io.*;
import java.util.*;

public class ResultHandler implements HttpHandler {

    private ResultService service = new ResultService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());

        // format: studentId=1&answers=1:A,2:B
        String[] parts = body.split("&");

        int studentId = Integer.parseInt(parts[0].split("=")[1]);

        String answerStr = parts[1].split("=")[1];

        Map<Integer, String> answers = new HashMap<>();

        for (String pair : answerStr.split(",")) {
            String[] kv = pair.split(":");
            answers.put(Integer.parseInt(kv[0]), kv[1]);
        }

        try {
            int score = service.evaluateAndStore(studentId, answers);

            String response = "Score=" + score;

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}