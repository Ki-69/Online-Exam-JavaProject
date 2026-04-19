import client.ApiClient;

public class TestDB {
    public static void main(String[] args) {
        try {
            int examId = 1; // update this to a valid exam ID for your database
            int studentId = 4; // update this to a valid student ID for your database
            String response = ApiClient.startExam(examId, studentId);
            System.out.println("Questions:\n" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}