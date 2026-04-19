package handler;

import com.sun.net.httpserver.*;
import service.TeacherService;
import model.Course;
import model.Exam;
import model.Question;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;

/**
 * TeacherHandler - HTTP request handler for teacher operations
 * Endpoints:
 * - GET /teacher/courses?teacherId=X → Teacher's courses
 * - POST /teacher/createExam → Create new exam
 * - GET /teacher/exams?courseId=X → Exams in course
 * - POST /teacher/addQuestion → Add question to exam
 * - GET /teacher/questions?examId=X → Questions in exam
 * - GET /teacher/results?examId=X or ?teacherId=X → Results
 */
public class TeacherHandler implements HttpHandler {

    private TeacherService service = new TeacherService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();

        try {

            // ===== GET COURSES BY TEACHER =====
            if (path.equals("/teacher/courses") && method.equalsIgnoreCase("GET")) {
                int teacherId = Integer.parseInt(parseQuery(query, "teacherId"));
                
                List<Course> courses = service.getCoursesByTeacher(teacherId);
                String response = courseListToJson(courses);

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== CREATE EXAM =====
            else if (path.equals("/teacher/createExam") && method.equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int courseId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                String title = URLDecoder.decode(p[1].split("=")[1], "UTF-8");
                int duration = Integer.parseInt(URLDecoder.decode(p[2].split("=")[1], "UTF-8"));
                int maxAttempts = Integer.parseInt(URLDecoder.decode(p[3].split("=")[1], "UTF-8"));
                int createdBy = Integer.parseInt(URLDecoder.decode(p[4].split("=")[1], "UTF-8"));

                int examId = service.createExam(courseId, title, duration, maxAttempts, createdBy);

                String response = "{\"success\": true, \"examId\": " + examId + "}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== GET EXAMS BY COURSE =====
            else if (path.equals("/teacher/exams") && method.equalsIgnoreCase("GET")) {
                int courseId = Integer.parseInt(parseQuery(query, "courseId"));
                
                List<Exam> exams = service.getExamsByCourse(courseId);
                String response = examListToJson(exams);

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== ADD QUESTION TO EXAM =====
            else if (path.equals("/teacher/addQuestion") && method.equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());

                String[] p = body.split("&");
                int examId = Integer.parseInt(URLDecoder.decode(p[0].split("=")[1], "UTF-8"));
                String questionText = URLDecoder.decode(p[1].split("=")[1], "UTF-8");
                String optionA = URLDecoder.decode(p[2].split("=")[1], "UTF-8");
                String optionB = URLDecoder.decode(p[3].split("=")[1], "UTF-8");
                String optionC = URLDecoder.decode(p[4].split("=")[1], "UTF-8");
                String optionD = URLDecoder.decode(p[5].split("=")[1], "UTF-8");
                String correctOption = URLDecoder.decode(p[6].split("=")[1], "UTF-8");
                int marks = Integer.parseInt(URLDecoder.decode(p[7].split("=")[1], "UTF-8"));
                int questionOrder = Integer.parseInt(URLDecoder.decode(p[8].split("=")[1], "UTF-8"));

                int questionId = service.addQuestion(examId, questionText, optionA, optionB, optionC, optionD, correctOption, marks, questionOrder);

                String response = "{\"success\": true, \"questionId\": " + questionId + "}";

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== GET QUESTIONS BY EXAM =====
            else if (path.equals("/teacher/questions") && method.equalsIgnoreCase("GET")) {
                int examId = Integer.parseInt(parseQuery(query, "examId"));
                
                List<Question> questions = service.getQuestionsByExam(examId);
                String response = questionListToJson(questions);

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }

            // ===== GET RESULTS =====
            else if (path.equals("/teacher/results") && method.equalsIgnoreCase("GET")) {
                List<Map<String, Object>> results;
                
                if (query.contains("examId")) {
                    int examId = Integer.parseInt(parseQuery(query, "examId"));
                    results = service.getResultsByExam(examId);
                } else if (query.contains("teacherId")) {
                    int teacherId = Integer.parseInt(parseQuery(query, "teacherId"));
                    results = service.getResultsByTeacher(teacherId);
                } else {
                    throw new IllegalArgumentException("Must provide examId or teacherId");
                }

                String response = mapListToJson(results);

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
            String error = "{\"success\": false, \"error\": \"" + escapeJsonString(e.getMessage()) + "\"}";
            byte[] bytes = error.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    /**
     * Parse query parameter from query string
     */
    private String parseQuery(String query, String paramName) throws Exception {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Missing query parameter: " + paramName);
        }
        String[] params = query.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv[0].equals(paramName)) {
                return URLDecoder.decode(kv[1], "UTF-8");
            }
        }
        throw new IllegalArgumentException("Parameter not found: " + paramName);
    }

    /**
     * Convert List<Course> to JSON string
     */
    private String courseListToJson(List<Course> courses) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            sb.append("{")
              .append("\"courseId\":").append(c.getCourseId()).append(",")
              .append("\"courseName\":\"").append(escapeJsonString(c.getCourseName())).append("\",")
              .append("\"description\":\"").append(escapeJsonString(c.getDescription() != null ? c.getDescription() : "")).append("\"")
              .append("}");
            if (i < courses.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convert List<Exam> to JSON string
     */
    private String examListToJson(List<Exam> exams) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < exams.size(); i++) {
            Exam e = exams.get(i);
            sb.append("{")
              .append("\"examId\":").append(e.getExamId()).append(",")
              .append("\"courseId\":").append(e.getCourseId()).append(",")
              .append("\"title\":\"").append(escapeJsonString(e.getTitle())).append("\",")
              .append("\"duration\":").append(e.getDuration()).append(",")
              .append("\"maxAttempts\":").append(e.getMaxAttempts()).append(",")
              .append("\"createdBy\":").append(e.getCreatedBy())
              .append("}");
            if (i < exams.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convert List<Question> to JSON string
     */
    private String questionListToJson(List<Question> questions) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            sb.append("{")
              .append("\"questionId\":").append(q.getQuestionId()).append(",")
              .append("\"examId\":").append(q.getExamId()).append(",")
              .append("\"questionText\":\"").append(escapeJsonString(q.getQuestionText() != null ? q.getQuestionText() : q.getText())).append("\",")
              .append("\"optionA\":\"").append(escapeJsonString(q.getOptionA())).append("\",")
              .append("\"optionB\":\"").append(escapeJsonString(q.getOptionB())).append("\",")
              .append("\"optionC\":\"").append(escapeJsonString(q.getOptionC())).append("\",")
              .append("\"optionD\":\"").append(escapeJsonString(q.getOptionD())).append("\",")
              .append("\"correctOption\":\"").append(q.getCorrectOption()).append("\",")
              .append("\"marks\":").append(q.getMarks()).append(",")
              .append("\"questionOrder\":").append(q.getQuestionOrder())
              .append("}");
            if (i < questions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convert List<Map<String,Object>> to JSON string
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
     * Convert Map<String,Object> to JSON object string
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
                sb.append("\"").append(escapeJsonString(value.toString())).append("\"");
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
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
