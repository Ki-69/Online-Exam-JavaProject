package handler;

import com.sun.net.httpserver.*;
import service.AdminService;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;

public class AdminHandler implements HttpHandler {

    private AdminService service = new AdminService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {

            // ===== VIEW ALL RESULTS =====
            if (path.equals("/admin/allResults") && method.equalsIgnoreCase("GET")) {

                List<Map<String, Object>> results = service.getAllResults();

                String response = mapListToJson(results);

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== CREATE COURSE =====
            else if (path.equals("/admin/createCourse") && method.equalsIgnoreCase("POST")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                String courseName = URLDecoder.decode(p[0].split("=")[1], "UTF-8");
                String description = URLDecoder.decode(p[1].split("=")[1], "UTF-8");

                int courseId = service.createCourse(courseName, description);

                String response = "{\"success\": true, \"courseId\": " + courseId + "}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== ASSIGN TEACHER TO COURSE =====
            else if (path.equals("/admin/assignTeacher") && method.equalsIgnoreCase("POST")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int courseId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                int teacherId = Integer.parseInt(URLDecoder.decode(p[1].split("=")[1], "UTF-8"));

                service.assignTeacherToCourse(courseId, teacherId);

                String response = "{\"success\": true, \"message\": \"Teacher assigned to course\"}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== ENROLL STUDENT IN COURSE =====
            else if (path.equals("/admin/enrollStudent") && method.equalsIgnoreCase("POST")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int courseId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                int studentId = Integer.parseInt(URLDecoder.decode(p[1].split("=")[1], "UTF-8"));

                service.enrollStudentInCourse(courseId, studentId);

                String response = "{\"success\": true, \"message\": \"Student enrolled in course\"}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== REMOVE TEACHER FROM COURSE =====
            else if (path.equals("/admin/removeTeacher") && method.equalsIgnoreCase("DELETE")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int courseId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                int teacherId = Integer.parseInt(URLDecoder.decode(p[1].split("=")[1], "UTF-8"));

                service.removeTeacherFromCourse(courseId, teacherId);

                String response = "{\"success\": true, \"message\": \"Teacher removed from course\"}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== REMOVE STUDENT FROM COURSE =====
            else if (path.equals("/admin/removeStudent") && method.equalsIgnoreCase("DELETE")) {

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int courseId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                int studentId = Integer.parseInt(URLDecoder.decode(p[1].split("=")[1], "UTF-8"));

                service.removeStudentFromCourse(courseId, studentId);

                String response = "{\"success\": true, \"message\": \"Student removed from course\"}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            else {
                exchange.sendResponseHeaders(404, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String error = "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
            byte[] bytes = error.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    /**
     * Convert List<Map<String,Object>> to JSON string manually
     */
    private String mapListToJson(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(mapToJson(list.get(i)));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convert Map<String,Object> to JSON object string manually
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            sb.append("\"").append(key).append("\":");
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(escapeJsonString((String) value)).append("\"");
            } else if (value instanceof Number) {
                sb.append(value);
            } else if (value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(value.toString()).append("\"");
            }
            if (i < keys.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Escape special characters in JSON strings
     */
    private String escapeJsonString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}