package service;

import dao.CourseDAO;
import java.util.List;
import model.Course;

public class CourseService {

    private CourseDAO dao = new CourseDAO();

    public List<Course> getAllCourses() throws Exception {
        return dao.getAllCourses();
    }
}
