import com.sun.net.httpserver.HttpServer;
import handler.*;
import java.net.InetSocketAddress;

public class MainServer {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Student endpoints
            server.createContext("/login", new AuthHandler());
            server.createContext("/startExam", new ExamHandler());
            server.createContext("/courses", new CourseHandler());
            server.createContext("/exams", new ExamListHandler());
            server.createContext("/submitAnswers", new ResultHandler());
            server.createContext("/results", new ResultListHandler());

            // Admin endpoints (wildcard path /admin/*)
            server.createContext("/admin/", new AdminHandler());
            
            // Teacher endpoints (wildcard path /teacher/*)
            server.createContext("/teacher/", new TeacherHandler());

            server.start();

            System.out.println("Server running on port 8080");
            System.out.println("Endpoints:");
            System.out.println("  Student: /login, /startExam, /courses, /exams, /submitAnswers, /results");
            System.out.println("  Admin:   /admin/* (createCourse, assignTeacher, enrollStudent, removeTeacher, removeStudent, allResults)");
            System.out.println("  Teacher: /teacher/* (courses, createExam, exams, addQuestion, questions, results)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}