package client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    // ===== STUDENT/GENERAL ENDPOINTS =====

    public static String login(String username, String password) throws Exception {

        URL url = new URL(BASE_URL + "/login");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "username=" + username + "&password=" + password;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String startExam(int examId, int studentId) throws Exception {

        URL url = new URL(BASE_URL + "/startExam?examId=" + examId + "&studentId=" + studentId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String getCourses() throws Exception {
        URL url = new URL(BASE_URL + "/courses");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String getExams(int courseId) throws Exception {
        URL url = new URL(BASE_URL + "/exams?courseId=" + courseId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String getResults(int examId, int studentId) throws Exception {
        URL url = new URL(BASE_URL + "/results?examId=" + examId + "&studentId=" + studentId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();       
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String submitAnswers(int studentId, int examId, String answers) throws Exception {
        URL url = new URL(BASE_URL + "/submitAnswers");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();       
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "studentId=" + studentId + "&examId=" + examId + "&answers=" + answers;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    // ===== ADMIN ENDPOINTS =====

    public static String adminCreateCourse(String courseName, String description) throws Exception {
        URL url = new URL(BASE_URL + "/admin/createCourse");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "courseName=" + URLEncoder.encode(courseName, "UTF-8") + 
                      "&description=" + URLEncoder.encode(description, "UTF-8");

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminAssignTeacher(int courseId, int teacherId) throws Exception {
        URL url = new URL(BASE_URL + "/admin/assignTeacher");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "courseId=" + courseId + "&teacherId=" + teacherId;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminEnrollStudent(int courseId, int studentId) throws Exception {
        URL url = new URL(BASE_URL + "/admin/enrollStudent");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "courseId=" + courseId + "&studentId=" + studentId;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminRemoveTeacher(int courseId, int teacherId) throws Exception {
        URL url = new URL(BASE_URL + "/admin/removeTeacher");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);

        String data = "courseId=" + courseId + "&teacherId=" + teacherId;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminRemoveStudent(int courseId, int studentId) throws Exception {
        URL url = new URL(BASE_URL + "/admin/removeStudent");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);

        String data = "courseId=" + courseId + "&studentId=" + studentId;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminGetAllUsers() throws Exception {
        URL url = new URL(BASE_URL + "/admin/allUsers");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminGetAllCourses() throws Exception {
        URL url = new URL(BASE_URL + "/admin/allCourses");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminGetAllEnrollments() throws Exception {
        URL url = new URL(BASE_URL + "/admin/allEnrollments");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String adminGetAllResults() throws Exception {
        URL url = new URL(BASE_URL + "/admin/allResults");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    // ===== TEACHER ENDPOINTS =====

    public static String teacherGetCourses(int teacherId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/courses?teacherId=" + teacherId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherCreateExam(int courseId, String title, int duration, int maxAttempts, int teacherId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/createExam");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "courseId=" + courseId + 
                      "&title=" + URLEncoder.encode(title, "UTF-8") +
                      "&duration=" + duration +
                      "&maxAttempts=" + maxAttempts +
                      "&createdBy=" + teacherId;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherGetExams(int courseId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/exams?courseId=" + courseId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherAddQuestion(int examId, String questionText, String optionA, String optionB, 
                                             String optionC, String optionD, String correctOption, int marks, int questionOrder) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/addQuestion");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String data = "examId=" + examId +
                      "&questionText=" + URLEncoder.encode(questionText, "UTF-8") +
                      "&optionA=" + URLEncoder.encode(optionA, "UTF-8") +
                      "&optionB=" + URLEncoder.encode(optionB, "UTF-8") +
                      "&optionC=" + URLEncoder.encode(optionC, "UTF-8") +
                      "&optionD=" + URLEncoder.encode(optionD, "UTF-8") +
                      "&correctOption=" + correctOption +
                      "&marks=" + marks +
                      "&questionOrder=" + questionOrder;

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.close();

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherGetQuestions(int examId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/questions?examId=" + examId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherGetResultsByExam(int examId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/results?examId=" + examId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    public static String teacherGetResultsByTeacher(int teacherId) throws Exception {
        URL url = new URL(BASE_URL + "/teacher/results?teacherId=" + teacherId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }
}
