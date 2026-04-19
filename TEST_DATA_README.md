# Online Exam System - Test Data

This directory contains comprehensive test data for the Online Exam System.

## Files

- `test_data.sql` - Complete test dataset with users, courses, exams, questions, and attempts
- `test_queries.sql` - Useful queries for verifying data and testing functionality

## How to Use

### 1. Database Setup
Make sure your MySQL database is running and you have the correct schema created.

### 2. Insert Test Data
Run the test data script:

```bash
mysql -u root -p your_database_name < test_data.sql
```

Or copy and paste the contents into MySQL Workbench/phpMyAdmin.

### 3. Verify Data
Run the verification queries from `test_queries.sql` to ensure data was inserted correctly.

## Test Data Overview

### Users
- **Admin**: admin/admin123
- **Teachers** (3): teacher1, teacher2, teacher3 (password: teacher123)
- **Students** (8): student1-student8 (password: student123)

### Courses (6 total)
1. Java Programming Fundamentals
2. Database Management Systems
3. Web Development with Spring Boot
4. Data Structures and Algorithms
5. Software Engineering Principles
6. Computer Networks

### Exams (18 total)
- Each course has 2-3 exams
- Mix of single-attempt and multiple-attempt exams
- Various durations (60-120 minutes)
- Different difficulty levels

### Sample Scenarios
- **Completed Exams**: Students with finished attempts and scores
- **Available Exams**: Exams students haven't taken yet
- **Multiple Attempts**: Some students have retaken exams
- **Mixed Performance**: Passing and failing scores

## Testing the Application

### Login Credentials
- **Admin**: admin/admin123
- **Teacher**: teacher1/teacher123
- **Student**: student1/student123

### Test Cases
1. **Student Dashboard**: Check available vs completed exams
2. **Exam Taking**: Try different exams with various attempt limits
3. **Results Viewing**: Verify scores display correctly
4. **Admin Panel**: Manage users, courses, and enrollments
5. **Teacher Panel**: Create and manage exams

## Data Relationships

```
Users (roles: ADMIN/TEACHER/STUDENT)
├── Courses (enrolled/taught)
│   ├── Exams (created by teachers)
│   │   ├── Questions (multiple choice)
│   │   └── Attempts (student exam sessions)
│   │       └── Student Answers (individual responses)
│   └── Results (legacy compatibility)
```

## Notes

- All passwords are simple for testing (not secure for production)
- Dates are set in the past for immediate availability
- Scores range from failing (45) to excellent (92)
- Questions cover basic to intermediate difficulty
- Data is designed to test all major system features