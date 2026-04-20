# Online Exam System - Complete Code Structure

## Project Overview

**Objective**: Multi-role online exam platform with Admin, Teacher, and Student roles.

**Technology Stack**:
- Language: Java 11+
- UI Framework: Swing
- Backend: Java HttpServer
- Database: MySQL 5.7+
- JDBC Driver: mysql-connector-j-9.6.0.jar

**Architecture**: 3-Layer (UI/Handler → Service → DAO → Database)

## System Architecture Overview

The Online Exam System is built using a 3-tier architecture:

1. **Presentation Layer (UI)**: Swing-based graphical user interface for students, teachers, and admins. Handles user interactions, displays data, and communicates with the backend via HTTP API calls.

2. **Application Layer (Handlers & Services)**: 
   - **Handlers**: HTTP request handlers that process API endpoints, parse requests, and return JSON responses.
   - **Services**: Business logic layer that validates inputs, enforces authorization, and orchestrates data operations.

3. **Data Layer (DAO & Database)**: 
   - **DAO**: Data Access Objects that encapsulate database operations using JDBC.
   - **Database**: MySQL database storing users, courses, exams, questions, and results.

**Application Flow**:
- User logs in via LoginUI, which authenticates against the /login endpoint.
- Based on role, appropriate dashboard is shown (StudentDashboard, TeacherDashboard, AdminDashboard).
- Students can view enrolled courses, take exams, and view results.
- Teachers can manage their courses, create exams, add questions, and view results.
- Admins can manage courses, assign teachers, enroll students, and view all results.
- All UI interactions use ApiClient to make HTTP requests to the backend server running on MainServer.

---

## Complete File Structure

```
Online-Exam-JavaProject/
├── API_ENDPOINTS.md                      [API endpoint documentation]
├── app.sh                                [Application startup script]
├── ARCHITECTURE.md                       [This file: system architecture]
├── DATABASE_REDESIGN.md                  [Database schema and redesign notes]
├── run.sh                                [Run script for the application]
│
├── client/
│   └── ApiClient.java                    [HTTP client for API interactions]
│
├── dao/
│   ├── AdminDAO.java                     [Data access for admin operations]
│   ├── CourseDAO.java                    [Course data access]
│   ├── CourseEnrollmentDAO.java          [Course enrollment data access]
│   ├── CourseTeacherDAO.java             [Course-teacher assignments]
│   ├── ExamDAO.java                      [Exam data access]
│   ├── QuestionDAO.java                  [Question data access]
│   ├── ResultDAO.java                    [Result data access]
│   ├── TeacherDAO.java                   [Teacher data access]
│   └── UserDAO.java                      [User data access]
│
├── db/
│   └── DBConnection.java                 [Database connection management]
│
├── handler/
│   ├── AdminHandler.java                 [Admin API endpoints]
│   ├── AuthHandler.java                  [Authentication endpoints]
│   ├── CourseHandler.java                [Course-related endpoints]
│   ├── ExamHandler.java                  [Exam start endpoints]
│   ├── ExamListHandler.java              [Exam list endpoints]
│   ├── ResultHandler.java                [Result submission endpoints]
│   ├── ResultListHandler.java            [Result retrieval endpoints]
│   └── TeacherHandler.java               [Teacher API endpoints]
│
├── lib/
│   └── mysql-connector-j-9.6.0.jar       [MySQL JDBC driver]
│
├── model/
│   ├── Attempt.java                      [Exam attempt model]
│   ├── Course.java                       [Course model]
│   ├── CourseEnrollment.java             [Course enrollment model]
│   ├── CourseTeacher.java                [Course-teacher model]
│   ├── Exam.java                         [Exam model]
│   ├── Question.java                     [Question model]
│   ├── Result.java                       [Result model]
│   └── User.java                         [User model]
│
├── service/
│   ├── AdminService.java                 [Admin business logic]
│   ├── AuthService.java                  [Authentication service]
│   ├── CourseService.java                [Course service]
│   ├── ExamService.java                  [Exam service]
│   ├── ResultService.java                [Result service]
│   ├── StudentService.java               [Student business logic]
│   └── TeacherService.java               [Teacher business logic]
│
├── src/
│   ├── MainServer.java                   [Main server entry point]
│   ├── TestDB.java                       [Database testing client]
│   │
│   ├── client/
│   │   └── ApiClient.java                [Duplicate or alternative client]
│   │
│   ├── dao/
│   │   ├── AdminDAO.java                 [Duplicate DAO classes]
│   │   ├── CourseDAO.java
│   │   ├── CourseEnrollmentDAO.java
│   │   ├── CourseTeacherDAO.java
│   │   ├── ExamDAO.java
│   │   ├── QuestionDAO.java
│   │   ├── ResultDAO.java
│   │   ├── TeacherDAO.java
│   │   └── UserDAO.java
│   │
│   ├── db/
│   │   └── DBConnection.java             [Duplicate DB connection]
│   │
│   ├── handler/
│   │   ├── AdminHandler.java             [Duplicate handlers]
│   │   ├── AuthHandler.java
│   │   ├── CourseHandler.java
│   │   ├── ExamHandler.java
│   │   ├── ExamListHandler.java
│   │   ├── ResultHandler.java
│   │   ├── ResultListHandler.java
│   │   └── TeacherHandler.java
│   │
│   ├── model/
│   │   ├── Attempt.java                  [Duplicate models]
│   │   ├── Course.java
│   │   ├── CourseEnrollment.java
│   │   ├── CourseTeacher.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   ├── Result.java
│   │   └── User.java
│   │
│   ├── service/
│   │   ├── AdminService.java             [Duplicate services]
│   │   ├── AuthService.java
│   │   ├── CourseService.java
│   │   ├── ExamService.java
│   │   ├── ResultService.java
│   │   ├── StudentService.java
│   │   └── TeacherService.java
│   │
│   └── ui/
│       ├── AdminDashboard.java           [Duplicate UI classes]
│       ├── CourseDetailPanel.java
│       ├── LoginUI.java
│       ├── ModernExamUI.java
│       ├── StudentDashboard.java
│       ├── TeacherDashboard.java
│       └── UITheme.java
│
├── ui/
│   ├── AdminDashboard.java               [UI classes for the application]
│   ├── CourseDetailPanel.java
│   ├── LoginUI.java
│   ├── ModernExamUI.java
│   ├── StudentDashboard.java
│   ├── TeacherDashboard.java
│   └── UITheme.java
│
└── out/                                  [Compiled .class files (generated)]
```

---

## Key Classes & Responsibilities

### Model Layer (4 main classes)

#### User.java
```java
- userId: int
- username: String
- password: String
- role: String (ADMIN | TEACHER | STUDENT)
- getters/setters
```

#### Course.java
```java
- courseId: int
- courseName: String
- description: String
- getters/setters
- Backward compatible: getTitle(), getTeacherId()
```

#### Exam.java
```java
- examId: int
- courseId: int
- title: String
- duration: int (minutes)
- maxAttempts: int
- createdBy: int (teacher ID)
- dateTime: LocalDateTime (optional)
- Multiple constructors for flexibility
- getters/setters
```

#### Question.java
```java
- questionId: int
- examId: int
- questionText: String
- optionA, optionB, optionC, optionD: String
- correctOption: String (A|B|C|D)
- marks: int
- questionOrder: int
- getters/setters
```

---

### DAO Layer (9 classes)

#### AdminDAO.java (15 methods)
```java
// Course operations
public int createCourse(String courseName, String description)
public List<Course> getAllCourses()

// Teacher assignment
public void assignTeacherToCourse(int courseId, int teacherId)
public void removeTeacherFromCourse(int courseId, int teacherId)
public List<User> getTeachersForCourse(int courseId)
public List<Map<String,Object>> getAllTeacherAssignments()

// Student enrollment
public void enrollStudentInCourse(int courseId, int studentId)
public void removeStudentFromCourse(int courseId, int studentId)
public List<User> getStudentsInCourse(int courseId)
public List<Map<String,Object>> getAllEnrollments()

// User management
public List<User> getAllUsers()
public List<User> getStudents()
public List<User> getTeachers()

// Results
public List<Map<String,Object>> getAllResults()
```

#### TeacherDAO.java (13 methods)
```java
// Authorization & Courses
public List<Course> getCoursesByTeacher(int teacherId)
public boolean teacherTeachesCourse(int teacherId, int courseId)

// Exam management
public int createExam(int courseId, String title, int duration, int maxAttempts, int createdBy)
public List<Exam> getExamsByCourse(int courseId)
public Exam getExamById(int examId)
public void deleteExam(int examId)
public int getQuestionCount(int examId)

// Question management
public int addQuestion(int examId, String questionText, String optionA-D, 
                       String correctOption, int marks, int questionOrder)
public List<Question> getQuestionsByExam(int examId)
public void updateQuestion(int questionId, ...)
public void deleteQuestion(int questionId)

// Results
public List<Map<String,Object>> getResultsByExam(int examId)
public List<Map<String,Object>> getResultsByTeacher(int teacherId)
```

#### UserDAO, CourseDAO, ExamDAO, QuestionDAO, etc.
- Each handles specific entity CRUD operations
- Use prepared statements for SQL injection prevention
- Try-with-resources for proper resource management

---

### Service Layer (7 classes)

#### AdminService.java (15 methods)
```java
- Wraps AdminDAO methods
- Validates all inputs (non-empty strings, valid IDs > 0)
- Throws descriptive exceptions
- No business logic beyond validation
```

#### TeacherService.java (15 methods)
```java
// Authorization
public boolean canTeacherAccessCourse(int teacherId, int courseId)

// Courses
public List<Course> getCoursesByTeacher(int teacherId)

// Exams (validates teacher owns course)
public int createExam(int courseId, String title, int duration, int maxAttempts, int createdBy)
public List<Exam> getExamsByCourse(int courseId)

// Questions (validates all parameters)
public int addQuestion(int examId, String questionText, String optionA-D, 
                       String correctOption, int marks, int questionOrder)
public List<Question> getQuestionsByExam(int examId)
public void updateQuestion(...) // validates options, marks
public void deleteQuestion(int questionId)

// Results
public List<Map<String,Object>> getResultsByExam(int examId)
public List<Map<String,Object>> getResultsByTeacher(int teacherId)

// Cleanup
public void deleteExam(int examId)
public int getQuestionCount(int examId)
```

#### Other Services
- **AuthService**: Login validation
- **StudentService**: Student operations
- **CourseService**: Course queries
- **ExamService**: Exam queries
- **ResultService**: Result management

---

### Handler Layer (8 classes)

#### AdminHandler.java
```java
Endpoints:
- POST   /admin/createCourse
- POST   /admin/assignTeacher
- POST   /admin/enrollStudent
- DELETE /admin/removeTeacher
- DELETE /admin/removeStudent
- GET    /admin/allResults

Methods:
- handle(HttpExchange) - main request router
- mapListToJson() - manual JSON serialization
- mapToJson() - Map to JSON object
- escapeJsonString() - JSON string escaping
```

#### TeacherHandler.java
```java
Endpoints:
- GET  /teacher/courses
- POST /teacher/createExam
- GET  /teacher/exams
- POST /teacher/addQuestion
- GET  /teacher/questions
- GET  /teacher/results

Methods:
- handle(HttpExchange) - main request router
- parseQuery() - Extract query parameters
- courseListToJson() - Convert Course list to JSON
- examListToJson() - Convert Exam list to JSON
- questionListToJson() - Convert Question list to JSON
- mapListToJson() - Generic list serialization
- mapToJson() - Map to JSON object
- escapeJsonString() - JSON string escaping
```

#### Other Handlers
- **AuthHandler**: `/login` endpoint
- **ExamHandler**: `/startExam` endpoint
- **CourseHandler**: `/courses` endpoint
- **ExamListHandler**: `/exams` endpoint
- **ResultHandler**: `/submitAnswers` endpoint
- **ResultListHandler**: `/results` endpoint

---

### UI Layer (7 classes)

#### UITheme.java (Constants)
```java
- PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR
- BG_PRIMARY, BG_SECONDARY, TEXT_COLOR
- Custom Swing fonts and styling
```

#### LoginUI.java
```java
- Role selection (ADMIN | TEACHER | STUDENT)
- Username/password input
- Login button with server authentication
- Routes to appropriate dashboard based on role
```

#### StudentDashboard.java
```java
- CardLayout for screen navigation
- Course carousel view (GridLayout of course cards)
- Course detail panel (ExamListHandler integration)
- Back button functional with proper CardLayout management
- Attempt tracking for exams
```

#### CourseDetailPanel.java
```java
- Header with course title and back button
- Exam list with:
  - Status indicators (completed/available)
  - Attempt count display (e.g., "Attempts: 2/3")
  - Action buttons: "Start Exam" or "View Results"
- ApiClient integration for exam operations
```

#### ModernExamUI.java
```java
- Question display with options
- Answer selection (radio buttons)
- Submit button
- Timer (optional)
- Navigation between questions
```

#### AdminDashboard.java (Basic)
```java
- Placeholder for admin operations UI
- Can be enhanced with panels for:
  - Create course form
  - Teacher assignment interface
  - Student enrollment interface
  - Results data table
```

#### TeacherDashboard.java (TODO)
```java
- Assigned courses display
- Exam management interface
- Question management
- Results viewing
```

---

### Client Layer

#### ApiClient.java (18 methods)
```java
// Student methods
- login(username, password)
- startExam(examId, studentId)
- getCourses()
- getExams(courseId)
- submitAnswers(studentId, examId, answers)
- getResults(examId, studentId)

// Admin methods
- adminCreateCourse(courseName, description)
- adminAssignTeacher(courseId, teacherId)
- adminEnrollStudent(courseId, studentId)
- adminRemoveTeacher(courseId, teacherId)
- adminRemoveStudent(courseId, studentId)
- adminGetAllResults()

// Teacher methods
- teacherGetCourses(teacherId)
- teacherCreateExam(courseId, title, duration, maxAttempts, teacherId)
- teacherGetExams(courseId)
- teacherAddQuestion(examId, questionText, optionA-D, correctOption, marks, order)
- teacherGetQuestions(examId)
- teacherGetResultsByExam(examId)
- teacherGetResultsByTeacher(teacherId)

All methods:
- Use HTTPURLConnection (no external libraries)
- Handle URLEncoding for special characters
- Return JSON strings (caller parses)
- Throw Exception on failure
```

---

### Database Layer

#### DBConnection.java
```java
- Singleton pattern
- Static getConnection() method
- MySQL driver: com.mysql.cj.jdbc.Driver
- URL: jdbc:mysql://localhost:3306/exam_system
- User: root, Password: tsukasa911
- Auto-reconnect enabled
```

#### Database Schema (9 tables)
```sql
1. users (user_id, username, password, role)
2. courses (course_id, course_name, description)
3. course_teachers (id, course_id, teacher_id) - UNIQUE(course_id, teacher_id)
4. course_enrollments (enrollment_id, course_id, student_id) - UNIQUE(course_id, student_id)
5. exams (exam_id, course_id, title, duration, max_attempts, created_by)
6. questions (question_id, exam_id, question_text, option_a-d, correct_option, marks, question_order)
7. attempts (attempt_id, student_id, exam_id, attempt_number, score)
8. student_answers (answer_id, attempt_id, question_id, selected_option)
9. results (result_id, student_id, exam_id, attempt_number, score) - LEGACY
```

---

## Validation Rules (Service Layer)

### AdminService Input Validation
- `courseName`: Not empty
- `courseId`, `teacherId`, `studentId`: > 0
- Duplicate assignment checks in DAO

### TeacherService Input Validation
- `teacherId`, `courseId`, `examId`: > 0
- `title`: Not empty
- `duration`: > 0
- `maxAttempts`: >= 1
- `questionText`: Not empty
- `optionA-D`: Not empty
- `correctOption`: Must be A, B, C, or D
- `marks`: > 0
- `questionOrder`: > 0
- **Authorization**: Teacher can only access their courses (verified via DAO)

---

## Security Features

✅ **SQL Injection Prevention**: All queries use PreparedStatement
✅ **Authentication**: Login validates user credentials
✅ **Authorization**: Teachers can only access their courses
✅ **Role-Based Access**: Endpoints available per role
✅ **Input Validation**: All inputs validated at Service layer
✅ **Proper Error Handling**: Descriptive exception messages
✅ **Transaction Management**: Proper commit/rollback

---

## Deployment Checklist

- [x] All source files compile without errors
- [x] Database schema created and test data loaded
- [x] AdminDAO fully implemented with 15 methods
- [x] TeacherDAO fully implemented with 13 methods
- [x] AdminService with full validation
- [x] TeacherService with authorization checks
- [x] AdminHandler with 6 endpoints + JSON serialization
- [x] TeacherHandler with 6 endpoints + JSON serialization
- [x] MainServer registers all handlers correctly
- [x] ApiClient includes all 18 methods
- [x] StudentDashboard functional with back button
- [x] ApiClient startExam includes studentId parameter
- [ ] AdminDashboard UI enhancement (optional)
- [ ] TeacherDashboard UI creation (optional)
- [ ] Integration testing
- [ ] User acceptance testing

---

## How to Build & Run

### Step 1: Compile
```bash
cd /path/to/Online-Exam-JavaProject
javac -cp lib/mysql-connector-j-9.6.0.jar -d out $(find src -name "*.java")
```

### Step 2: Run Server
```bash
cd out
java -cp ../lib/mysql-connector-j-9.6.0.jar MainServer
```

### Step 3: Run Client (from src directory)
```bash
cd ..
java -cp out:lib/mysql-connector-j-9.6.0.jar TestDB
# or run UI
java -cp out:lib/mysql-connector-j-9.6.0.jar ui.LoginUI
```

### Step 4: Test Endpoints
```bash
# Create a course
curl -X POST http://localhost:8080/admin/createCourse \
  -d "courseName=Java%20Advanced&description=Advanced%20Java%20Programming"

# Get teacher's courses
curl http://localhost:8080/teacher/courses?teacherId=2

# Get exam results
curl http://localhost:8080/admin/allResults
```

---

## Test Database Credentials

```
Admin User:
- Username: admin
- Password: pass
- Role: ADMIN

Teacher Users:
- Username: t1 or t2
- Password: pass
- Role: TEACHER

Student Users:
- Username: s1, s2, s3, s4, s5
- Password: pass
- Role: STUDENT
```

---

## Notes for Future Development

1. **AdminDashboard UI**: Create Swing panels for all admin operations with proper form validation
2. **TeacherDashboard UI**: Create new Swing UI for teacher exam/question management
3. **Session Management**: Add session tokens to prevent unauthorized access
4. **Input Sanitization**: HTML-encode user input in responses
5. **Rate Limiting**: Add rate limiting to prevent abuse
6. **Logging**: Add comprehensive logging for all operations
7. **Testing**: Create JUnit test cases for all service methods
8. **API Documentation**: Add Javadoc comments to all public methods
