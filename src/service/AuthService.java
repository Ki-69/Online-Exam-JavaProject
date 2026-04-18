package service;

import dao.UserDAO;
import model.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws Exception {
        return userDAO.getUser(username, password);
    }
}