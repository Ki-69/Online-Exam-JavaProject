package handler;

import com.sun.net.httpserver.*;
import java.io.*;
import java.util.List;
import model.Course;
import service.CourseService;

public class CourseHandler implements HttpHandler {

    private CourseService service = new CourseService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            List<Course> courses = service.getAllCourses();
            StringBuilder response = new StringBuilder();
            for (Course course : courses) {
                response.append(course.getCourseId())
                        .append("|")
                        .append(course.getTitle())
                        .append("|")
                        .append(course.getTeacherId())
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
}
