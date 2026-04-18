package client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

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

    public static String startExam() throws Exception {

        URL url = new URL(BASE_URL + "/startExam");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }
}