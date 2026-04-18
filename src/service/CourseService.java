package service;

import dao.CourseDAO;
import model.Course;

import java.util.List;

public class CourseService {

    private CourseDAO dao = new CourseDAO();

    public List<Course> getAllCourses() throws Exception {
        return dao.getAllCourses();
    }
}
