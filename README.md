# Online Exam System - Complete Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Database Schema](#database-schema)
5. [API Documentation](#api-documentation)
6. [Data Flow](#data-flow)
7. [Installation & Setup](#installation--setup)
8. [Running the Application](#running-the-application)
9. [User Roles & Permissions](#user-roles--permissions)
10. [Code Structure](#code-structure)
11. [Development Guidelines](#development-guidelines)

---

## Project Overview

The Online Exam System is a comprehensive multi-role web application built in Java that allows administrators, teachers, and students to manage and participate in online examinations. The system supports multiple courses, configurable exams with time limits and attempt limits, and detailed result tracking.

### Key Features
- **Role-based Access Control**: Admin, Teacher, and Student roles with different permissions
- **Multi-course Support**: Students can enroll in multiple courses, teachers can teach multiple courses
- **Flexible Exam Configuration**: Configurable duration, maximum attempts, and question ordering
- **Real-time Exam Taking**: Timer-based exams with automatic submission
- **Comprehensive Result Tracking**: Detailed results per attempt with individual question answers
- **Modern UI**: Swing-based desktop application with intuitive interfaces

### System Responsibilities

#### Admin Role
**Controls structure and assignments only. Admin does NOT create questions or exams.**

**Admin Features:**
1. **Create Course**: Input course name and description
2. **Assign Teacher to Course**: Select teacher and course from dropdowns
3. **Enroll Student in Course**: Select student and course from dropdowns
4. **Remove Assignments**: Remove teacher/course or student/course assignments
5. **View System Data**: Read-only view of all users, courses, enrollments, and results

#### Teacher Role
**Controls academic content only.**

**Teacher Features:**
1. **View Assigned Courses**: See only courses they are assigned to teach
2. **Create Exam**: Create exams for their courses with title, duration, max attempts
3. **View Exams per Course**: List all exams for courses they teach
4. **Add Questions to Exam**: Create multiple choice questions with 4 options
5. **View Questions**: See all questions for exams they created
6. **View Student Results**: View results for all exams they created

#### Student Role
**Access learning materials and take exams.**

**Student Features:**
- View enrolled courses
- Take exams with time limits and attempt limits
- View personal results and attempt history

---

## System Architecture

### 3-Tier Architecture

```
┌─────────────────┐    HTTP/JSON     ┌─────────────────┐    JDBC    ┌─────────────────┐
│   Presentation  │◄────────────────►│   Application   │◄──────────►│      Data       │
│     Layer       │                  │     Layer       │            │     Layer       │
│                 │                  │                 │            │                 │
│ • LoginUI       │                  │ • Handlers      │            │ • MySQL         │
│ • Dashboards    │                  │ • Services      │            │ • DAOs          │
│ • ApiClient     │                  │ • Business Logic│            │ • Models        │
└─────────────────┘                  └─────────────────┘            └─────────────────┘
```

### Layer Details

#### 1. Presentation Layer (UI)
- **Swing-based desktop application**
- **Components**: LoginUI, StudentDashboard, TeacherDashboard, AdminDashboard
- **Communication**: HTTP requests to backend via ApiClient
- **Features**: Modern UI with custom themes, responsive design

#### 2. Application Layer (Backend)
- **HTTP Server**: Java HttpServer running on port 8080
- **Handlers**: Process HTTP requests and return JSON responses
- **Services**: Business logic validation and orchestration
- **Protocol**: RESTful HTTP API with URL-encoded form data

#### 3. Data Layer (Database)
- **Database**: MySQL 5.7+
- **Connection**: JDBC with connection pooling
- **DAOs**: Data Access Objects for CRUD operations
- **Models**: Java objects representing database entities

---

## Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | Java | 11+ | Core application logic |
| **UI Framework** | Swing | - | Desktop GUI components |
| **HTTP Server** | Java HttpServer | - | REST API backend |
| **Database** | MySQL | 5.7+ | Data persistence |
| **JDBC Driver** | MySQL Connector/J | 8.0.33 | Database connectivity |
| **JSON Library** | Built-in Java | - | JSON parsing/serialization |
| **Build Tool** | javac | - | Java compilation |
| **OS Support** | Cross-platform | - | Windows/Linux/macOS |

---

## Database Schema

### Complete Schema SQL

```sql
-- =========================
-- 1. CREATE DATABASE
-- =========================
DROP DATABASE IF EXISTS exam_system;
CREATE DATABASE exam_system;
USE exam_system;

-- =========================
-- 2. USERS
-- =========================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL
);

-- =========================
-- 3. COURSES
-- =========================
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- =========================
-- 4. COURSE ↔ TEACHERS
-- =========================
CREATE TABLE course_teachers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    teacher_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =========================
-- 5. COURSE ↔ STUDENTS
-- =========================
CREATE TABLE course_enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =========================
-- 6. EXAMS
-- =========================
CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    date_time DATETIME,
    duration INT,
    max_attempts INT DEFAULT 1,
    created_by INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- =========================
-- 7. QUESTIONS (MCQ A–D)
-- =========================
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a TEXT,
    option_b TEXT,
    option_c TEXT,
    option_d TEXT,
    correct_option ENUM('A', 'B', 'C', 'D') NOT NULL,
    marks INT DEFAULT 1,
    question_order INT,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- =========================
-- 8. ATTEMPTS
-- =========================
CREATE TABLE attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    attempt_number INT NOT NULL,
    score INT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- =========================
-- 9. STUDENT ANSWERS
-- =========================
CREATE TABLE student_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option ENUM('A', 'B', 'C', 'D'),
    FOREIGN KEY (attempt_id) REFERENCES attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

-- =========================
-- 10. INDEXES (PERFORMANCE)
-- =========================
CREATE INDEX idx_questions_exam ON questions(exam_id);
CREATE INDEX idx_attempts_student_exam ON attempts(student_id, exam_id);
CREATE INDEX idx_enrollment_student ON course_enrollments(student_id);
```

---

## API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication
- **Method**: POST
- **Endpoint**: `/login`
- **Body**: `username=<string>&password=<string>`
- **Response**: `userId,role` (e.g., `1,STUDENT`)
- **Error**: HTTP 401 Unauthorized

### Student Endpoints

#### Get Available Courses
- **Method**: GET
- **Endpoint**: `/courses`
- **Response**: JSON array of courses

#### Get Exams for Course
- **Method**: GET
- **Endpoint**: `/exams?courseId=<int>`
- **Response**: JSON array of exams

#### Start Exam
- **Method**: GET
- **Endpoint**: `/startExam?examId=<int>&studentId=<int>`
- **Response**: JSON array of questions
- **Validation**: Student enrolled, attempts remaining

#### Submit Answers
- **Method**: POST
- **Endpoint**: `/submitAnswers`
- **Body**: `studentId=<int>&examId=<int>&answers=<json_string>`
- **Response**: `{"success": true, "score": <int>}`

#### Get Results
- **Method**: GET
- **Endpoint**: `/results?examId=<int>&studentId=<int>`
- **Response**: JSON array of attempt results

### Admin Endpoints (Prefix: `/admin/`)

- **POST** `/admin/createCourse` - Create new course
- **POST** `/admin/assignTeacher` - Assign teacher to course
- **POST** `/admin/enrollStudent` - Enroll student in course
- **DELETE** `/admin/removeTeacher` - Remove teacher from course
- **DELETE** `/admin/removeStudent` - Remove student from course
- **GET** `/admin/allUsers` - Get all users
- **GET** `/admin/allCourses` - Get all courses
- **GET** `/admin/allEnrollments` - Get all enrollments
- **GET** `/admin/allResults` - Get all system results

### Teacher Endpoints (Prefix: `/teacher/`)

- **GET** `/teacher/courses?teacherId=X` - Get teacher's courses
- **POST** `/teacher/createExam` - Create new exam
- **GET** `/teacher/exams?courseId=X` - Get exams for course
- **POST** `/teacher/addQuestion` - Add question to exam
- **GET** `/teacher/questions?examId=X` - Get questions for exam
- **GET** `/teacher/results?examId=X` or `?teacherId=X` - Get results

### Student Endpoints

- **POST** `/login` - User authentication
- **GET** `/courses` - Get available courses
- **GET** `/exams?courseId=X` - Get exams for course
- **GET** `/startExam?examId=X&studentId=Y` - Start exam attempt
- **POST** `/submitAnswers` - Submit exam answers
- **GET** `/results?examId=X&studentId=Y` - Get results

---

## Data Flow

### User Authentication Flow
```
1. User enters credentials in LoginUI
2. LoginUI calls ApiClient.login()
3. ApiClient sends POST /login
4. AuthHandler receives request
5. AuthHandler calls AuthService.login()
6. AuthService calls UserDAO.authenticate()
7. UserDAO queries users table
8. Response flows back: User object → JSON → UI
9. UI redirects to appropriate dashboard based on role
```

### Exam Taking Flow
```
1. Student selects exam from StudentDashboard
2. StudentDashboard calls ApiClient.startExam()
3. ApiClient sends GET /startExam?examId=X&studentId=Y
4. ExamHandler validates enrollment and attempts
5. ExamHandler calls ExamService.startExam()
6. ExamService creates new attempt in attempts table
7. ExamService retrieves questions via QuestionDAO
8. Questions returned as JSON array
9. ModernExamUI displays questions with timer
10. Student answers questions
11. On submit/time up: ApiClient.submitAnswers()
12. ResultHandler processes answers
13. ResultService evaluates each answer
14. Scores saved to student_answers and attempts tables
15. Result returned to UI
```

### Admin Course Management Flow
```
1. Admin creates course via AdminDashboard
2. AdminDashboard calls ApiClient.adminCreateCourse()
3. ApiClient sends POST /admin/createCourse
4. AdminHandler receives request
5. AdminHandler calls AdminService.createCourse()
6. AdminService validates input
7. AdminService calls CourseDAO.create()
8. Course inserted into courses table
9. Course ID returned through chain
10. UI updates course list
```

---

## Installation & Setup

### Prerequisites
- **Java**: JDK 11 or higher
- **MySQL**: Version 5.7 or higher
- **Git**: For cloning repository


### Application Setup

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd Online-Exam-JavaProject
   ```

2. **Update Database Configuration**
   Edit `src/db/DBConnection.java`:
   ```java
   String url = "jdbc:mysql://localhost:3306/exam_system";
   String user = "exam_user";  // Change to your MySQL username
   String password = "password";  // Change to your MySQL password
   ```

3. **Place JDBC Driver**
   Ensure `mysql-connector-j-8.0.33.jar` is in the `lib/` directory

---

## Running the Application

### Method 1: Using Scripts (Recommended)

1. **Start Server** (Terminal 1)
   ```bash
   ./run.sh
   ```
   This will:
   - Clean previous build
   - Compile all Java files
   - Start the HTTP server on port 8080

2. **Start Client** (Terminal 2)
   ```bash
   ./app.sh
   ```
   This will launch the Swing GUI application

### Method 2: Manual Commands

1. **Compile**
   ```bash
   javac -cp "lib/*" -d out $(find src -name "*.java")
   ```

2. **Run Server**
   ```bash
   java -cp "lib/*;out" MainServer
   ```

3. **Run Client** (New Terminal)
   ```bash
   java -cp "lib/*;out" ui.LoginUI
   ```


### Default Login Credentials
- **Admin**: username: `admin`, password: `admin`
- **Teacher**: username: `t1`, password: `t1`
- **Student**: username: `s1`, password: `s1`

---

## User Roles & Permissions

### Admin Role
**Permissions:**
- Create and manage courses
- Assign teachers to courses
- Enroll students in courses
- Remove teacher/course assignments
- Remove student/course enrollments
- View all system results across all exams

**Dashboard Features:**
- Course management panel
- Teacher assignment panel
- Student enrollment panel
- System-wide results viewer

### Teacher Role
**Permissions:**
- View assigned courses
- Create exams for assigned courses
- Add/edit/delete questions for owned exams
- View results for exams they created
- View results for all their courses

**Dashboard Features:**
- Course list (assigned courses only)
- Exam creation and management
- Question bank management
- Results viewer for owned exams

### Student Role
**Permissions:**
- View enrolled courses
- Take exams (with attempt limits)
- View personal results
- Submit answers during exam time

**Dashboard Features:**
- Enrolled courses list
- Available exams list
- Exam taking interface with timer
- Personal results history

---

## Validation & Logic Rules

### Validation Rules (Critical)

**MUST ENFORCE - These are strictly implemented:**

- **Teacher Authorization**: Teacher can ONLY access their assigned courses
- **Exam Ownership**: Teacher can ONLY modify exams they created
- **Role Restrictions**: Students cannot access admin/teacher endpoints
- **Input Validation**: All inputs are validated for type, range, and required fields

**Examples:**
- Teacher editing another teacher's course → BLOCKED
- Student calling teacher API → BLOCKED
- Admin can override anything

### Logic Rules

#### Exam Creation
- Must belong to a course the teacher is assigned to
- Must have max_attempts ≥ 1
- Duration must be > 0 minutes
- Title cannot be empty

#### Question Creation
- Must belong to an exam the teacher created
- Must have exactly one correct option (A, B, C, or D)
- All 4 options must be provided
- Question text cannot be empty
- Marks must be > 0

#### Results
- Read-only for teachers
- Stored per attempt with individual question answers
- Teachers can only view results for their own exams

---

## Code Structure

### Directory Structure
```
Online-Exam-JavaProject/
├── src/                          # Source code
│   ├── MainServer.java          # HTTP server entry point
│   ├── TestDB.java              # Database testing utility
│   ├── client/
│   │   └── ApiClient.java       # HTTP client for API calls
│   ├── dao/                     # Data Access Objects
│   │   ├── AdminDAO.java
│   │   ├── CourseDAO.java
│   │   ├── CourseEnrollmentDAO.java
│   │   ├── CourseTeacherDAO.java
│   │   ├── ExamDAO.java
│   │   ├── QuestionDAO.java
│   │   ├── ResultDAO.java
│   │   ├── TeacherDAO.java
│   │   └── UserDAO.java
│   ├── db/
│   │   └── DBConnection.java    # Database connection manager
│   ├── handler/                 # HTTP request handlers
│   │   ├── AdminHandler.java
│   │   ├── AuthHandler.java
│   │   ├── CourseHandler.java
│   │   ├── ExamHandler.java
│   │   ├── ExamListHandler.java
│   │   ├── ResultHandler.java
│   │   ├── ResultListHandler.java
│   │   └── TeacherHandler.java
│   ├── model/                   # Data models
│   │   ├── Attempt.java
│   │   ├── Course.java
│   │   ├── CourseEnrollment.java
│   │   ├── CourseTeacher.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   ├── Result.java
│   │   └── User.java
│   ├── service/                 # Business logic
│   │   ├── AdminService.java
│   │   ├── AuthService.java
│   │   ├── CourseService.java
│   │   ├── ExamService.java
│   │   ├── ResultService.java
│   │   ├── StudentService.java
│   │   └── TeacherService.java
│   └── ui/                      # User interface
│       ├── AdminDashboard.java
│       ├── CourseDetailPanel.java
│       ├── LoginUI.java
│       ├── ModernExamUI.java
│       ├── StudentDashboard.java
│       ├── TeacherDashboard.java
│       └── UITheme.java
├── lib/                         # External libraries
│   └── mysql-connector-j-8.0.33.jar
├── out/                         # Compiled classes (generated)
├── API_ENDPOINTS.md             # API documentation
├── ARCHITECTURE.md              # Architecture details
├── DATABASE_REDESIGN.md         # Database schema info
├── app.sh                       # Client launcher script
└── run.sh                       # Server build/run script
```

### Key Classes Overview

#### MainServer.java
- Entry point for the HTTP server
- Registers all endpoint handlers
- Starts server on port 8080

#### ApiClient.java
- Static methods for all API calls
- Handles HTTP connections and responses
- Used by UI components to communicate with backend

#### DBConnection.java
- Manages MySQL database connections
- Uses connection pooling
- Handles transaction management

#### Handler Classes
- Extend `HttpHandler` interface
- Process specific API endpoints
- Parse requests and format responses
- Call appropriate service methods

#### Service Classes
- Contain business logic
- Validate inputs and permissions
- Orchestrate DAO calls
- Handle exceptions and errors

#### DAO Classes
- Data Access Objects
- Encapsulate SQL queries
- Handle database operations
- Use prepared statements for security

#### Model Classes
- Represent database entities
- Contain getters/setters
- Support multiple constructors

### UI Structure

#### Admin Dashboard
```
AdminDashboard
├── Create Course Panel
├── Assign Teacher Panel
├── Enroll Student Panel
├── View Data Panel
```
- Simple forms with dropdowns for selections
- Tables for viewing system data
- Clean, intuitive interface

#### Teacher Dashboard
```
TeacherDashboard
├── Menu Panel
│   ├── My Courses
│   ├── Create Exam
│   ├── Manage Questions
│   └── View Results
├── Course Cards (only assigned courses)
│   └── Course Management Page
│       ├── Create Exam
│       ├── Exam List
│       │   └── Exam Management Page
│       │       ├── Add Question
│       │       ├── View Questions
│       │       └── View Results
```
- Role-based access control
- Hierarchical navigation
- Question creation with multiple choice options

---