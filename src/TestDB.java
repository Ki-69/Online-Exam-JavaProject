import client.ApiClient;

public class TestDB {
    public static void main(String[] args) {
        try {
            String response = ApiClient.startExam();
            System.out.println("Questions:\n" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}