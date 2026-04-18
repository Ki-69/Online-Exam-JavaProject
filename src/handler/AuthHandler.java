package handler;

import com.sun.net.httpserver.*;
import service.AuthService;
import model.User;

import java.io.*;

public class AuthHandler implements HttpHandler {

    private AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // 1. Allow only POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // 2. Read request body
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());

        // Expected: username=abc&password=123
        String[] params = body.split("&");
        String username = params[0].split("=")[1];
        String password = params[1].split("=")[1];

        try {
            // 3. Call service
            User user = authService.login(username, password);

            if (user != null) {
                String response = user.getUserId() + "," + user.getRole();

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(401, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}