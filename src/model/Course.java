package model;

public class Course {
    private int courseId;
    private String courseName;
    private String description;

    // No-arg constructor
    public Course() {
    }

    // Constructor matching new schema
    public Course(int courseId, String courseName, String description) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Backward compatibility
    public String getTitle() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Backward compatibility
    public int getTeacherId() {
        return 0; // Not applicable in new schema (many-to-many)
    }
}
