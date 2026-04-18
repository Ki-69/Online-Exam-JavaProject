import com.sun.net.httpserver.HttpServer;
import handler.*;
import java.net.InetSocketAddress;

public class MainServer {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/login", new AuthHandler());
            server.createContext("/startExam", new ExamHandler());
            server.createContext("/courses", new CourseHandler());
            server.createContext("/exams", new ExamListHandler());
            server.createContext("/submitAnswers", new ResultHandler());

            server.createContext("/allResults", new AdminHandler());
            server.createContext("/addQuestion", new AdminHandler());

            server.start();

            System.out.println("Server running on port 8080");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}