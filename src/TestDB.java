import client.ApiClient;

public class TestDB {
    public static void main(String[] args) {
        try {
            int examId = 1; // update this to a valid exam ID for your database
            String response = ApiClient.startExam(examId);
            System.out.println("Questions:\n" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}