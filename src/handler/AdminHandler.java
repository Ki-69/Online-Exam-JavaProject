package handler;

import com.sun.net.httpserver.*;
import service.AdminService;

import java.io.*;
import java.util.List;
import java.net.URLDecoder;

public class AdminHandler implements HttpHandler {

    private AdminService service = new AdminService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {

            // ===== VIEW RESULTS =====
            if (path.equals("/allResults") && method.equalsIgnoreCase("GET")) {

                List<String> results = service.getAllResults();

                StringBuilder response = new StringBuilder();
                for (String r : results) {
                    response.append(r).append("\n");
                }

                byte[] bytes = response.toString().getBytes();
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== ADD QUESTION =====
            else if (path.equals("/addQuestion") && method.equalsIgnoreCase("POST")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");

                String q = URLDecoder.decode(p[0].split("=")[1], "UTF-8");
                String a = URLDecoder.decode(p[1].split("=")[1], "UTF-8");
                String b = URLDecoder.decode(p[2].split("=")[1], "UTF-8");
                String c = URLDecoder.decode(p[3].split("=")[1], "UTF-8");
                String d = URLDecoder.decode(p[4].split("=")[1], "UTF-8");
                String correct = URLDecoder.decode(p[5].split("=")[1], "UTF-8");
                int marks = Integer.parseInt(URLDecoder.decode(p[6].split("=")[1], "UTF-8"));

                service.addQuestion(q, a, b, c, d, correct, marks);

                String response = "Question Added";

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

            else {
                exchange.sendResponseHeaders(404, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}