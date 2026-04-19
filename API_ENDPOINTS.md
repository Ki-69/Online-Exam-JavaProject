# Online Exam System - Admin & Teacher API Endpoints

## Compilation Status ✅
- **Exit Code**: 0 (SUCCESS)
- **Source Files**: 41 Java files
- **Generated Classes**: 58 class files
- **Status**: All compiled successfully with no errors

---

## API Endpoint Summary

### 1. ADMIN ENDPOINTS (`/admin/`)

#### Create Course
```
POST /admin/createCourse
Body: courseName=<string>&description=<string>
Response: {"success": true, "courseId": <int>}
```

#### Assign Teacher to Course
```
POST /admin/assignTeacher
Body: courseId=<int>&teacherId=<int>
Response: {"success": true, "message": "Teacher assigned to course"}
```

#### Enroll Student in Course
```
POST /admin/enrollStudent
Body: courseId=<int>&studentId=<int>
Response: {"success": true, "message": "Student enrolled in course"}
```

#### Remove Teacher from Course
```
DELETE /admin/removeTeacher
Body: courseId=<int>&teacherId=<int>
Response: {"success": true, "message": "Teacher removed from course"}
```

#### Remove Student from Course
```
DELETE /admin/removeStudent
Body: courseId=<int>&studentId=<int>
Response: {"success": true, "message": "Student removed from course"}
```

#### Get All Results
```
GET /admin/allResults
Response: [
  {"student_id": <int>, "student_name": "<string>", "exam_id": <int>, "exam_title": "<string>", "attempt_number": <int>, "score": <int>},
  ...
]
```

---

### 2. TEACHER ENDPOINTS (`/teacher/`)

#### Get Teacher's Courses
```
GET /teacher/courses?teacherId=<int>
Response: [
  {"courseId": <int>, "courseName": "<string>", "description": "<string>"},
  ...
]
```

#### Create Exam
```
POST /teacher/createExam
Body: courseId=<int>&title=<string>&duration=<int>&maxAttempts=<int>&createdBy=<int>
Response: {"success": true, "examId": <int>}
```

#### Get Exams by Course
```
GET /teacher/exams?courseId=<int>
Response: [
  {"examId": <int>, "courseId": <int>, "title": "<string>", "duration": <int>, "maxAttempts": <int>, "createdBy": <int>},
  ...
]
```

#### Add Question to Exam
```
POST /teacher/addQuestion
Body: examId=<int>&questionText=<string>&optionA=<string>&optionB=<string>&optionC=<string>&optionD=<string>&correctOption=[A|B|C|D]&marks=<int>&questionOrder=<int>
Response: {"success": true, "questionId": <int>}
```

#### Get Questions by Exam
```
GET /teacher/questions?examId=<int>
Response: [
  {"questionId": <int>, "examId": <int>, "questionText": "<string>", "optionA": "<string>", "optionB": "<string>", "optionC": "<string>", "optionD": "<string>", "correctOption": "[A|B|C|D]", "marks": <int>, "questionOrder": <int>},
  ...
]
```

#### Get Results by Exam
```
GET /teacher/results?examId=<int>
Response: [
  {"attempt_id": <int>, "student_id": <int>, "student_name": "<string>", "exam_id": <int>, "attempt_number": <int>, "score": <int>},
  ...
]
```

#### Get Results by Teacher
```
GET /teacher/results?teacherId=<int>
Response: [
  {"attempt_id": <int>, "student_id": <int>, "student_name": "<string>", "exam_id": <int>, "exam_title": "<string>", "attempt_number": <int>, "score": <int>},
  ...
]
```

---

### 3. STUDENT ENDPOINTS (Existing)

#### Login
```
POST /login
Body: username=<string>&password=<string>
Response: {"success": true, "userId": <int>, "role": "[ADMIN|TEACHER|STUDENT]"}
```

#### Start Exam
```
GET /startExam?examId=<int>&studentId=<int>
Response: [Question JSON array]
```

#### Get Courses
```
GET /courses
Response: [Course JSON array]
```

#### Get Exams
```
GET /exams?courseId=<int>
Response: [Exam JSON array]
```

#### Submit Answers
```
POST /submitAnswers
Body: studentId=<int>&examId=<int>&answers=<json_string>
Response: {"success": true, "score": <int>}
```

#### Get Results
```
GET /results?examId=<int>&studentId=<int>
Response: [Result JSON array]
```

---

## ApiClient Java Methods

### Admin Methods
```java
// Create course, returns {"success": true, "courseId": ...}
ApiClient.adminCreateCourse(String courseName, String description)

// Assign teacher to course
ApiClient.adminAssignTeacher(int courseId, int teacherId)

// Enroll student in course
ApiClient.adminEnrollStudent(int courseId, int studentId)

// Remove teacher from course
ApiClient.adminRemoveTeacher(int courseId, int teacherId)

// Remove student from course
ApiClient.adminRemoveStudent(int courseId, int studentId)

// Get all system results
ApiClient.adminGetAllResults()
```

### Teacher Methods
```java
// Get teacher's courses
ApiClient.teacherGetCourses(int teacherId)

// Create exam
ApiClient.teacherCreateExam(int courseId, String title, int duration, int maxAttempts, int teacherId)

// Get exams in course
ApiClient.teacherGetExams(int courseId)

// Add question to exam
ApiClient.teacherAddQuestion(int examId, String questionText, String optionA, String optionB, 
                             String optionC, String optionD, String correctOption, int marks, int questionOrder)

// Get questions in exam
ApiClient.teacherGetQuestions(int examId)

// Get results for an exam
ApiClient.teacherGetResultsByExam(int examId)

// Get all results for a teacher
ApiClient.teacherGetResultsByTeacher(int teacherId)
```

---

## Key Implementation Details

### Security & Validation
- ✅ TeacherService validates authorization (teacher can only access their courses)
- ✅ AdminService validates input (non-empty strings, valid IDs)
- ✅ Handler layer performs request parsing and JSON serialization
- ✅ All database operations use prepared statements (no SQL injection)
- ✅ Duplicate check before inserts (e.g., same teacher-course assignment)

### JSON Response Format
- All responses are valid JSON
- Manual JSON serialization to avoid external dependencies
- Error responses: `{"success": false, "error": "<message>"}`
- Success responses: `{"success": true, ...additional fields...}`

### Handler Organization
- AdminHandler: Registered at `/admin/` context (handles all /admin/* paths)
- TeacherHandler: Registered at `/teacher/` context (handles all /teacher/* paths)
- Query parameter parsing with URLDecoder for special characters
- POST body parsing with split("&") and URLDecoder.decode()

---

## Architecture (3-Layer)

```
┌─────────────────────────────────┐
│      UI / API Client            │
│  (AdminDashboard, ApiClient)    │
└────────────────┬────────────────┘
                 │ Calls HTTP endpoints
┌────────────────▼─────────────────┐
│      Handler Layer               │
│  (AdminHandler, TeacherHandler)  │
│  - Parse requests                │
│  - Call Service layer            │
│  - Return JSON responses         │
└────────────────┬────────────────┘
                 │
┌────────────────▼─────────────────┐
│      Service Layer               │
│  (AdminService, TeacherService)  │
│  - Validate input                │
│  - Check authorization           │
│  - Call DAO layer                │
└────────────────┬───────────────────┘
                 │
┌────────────────▼──────────────────┐
│      Data Access Layer (DAO)      │
│  (AdminDAO, TeacherDAO)           │
│  - Execute SQL queries            │
│  - Build result objects           │
│  - Handle transactions            │
└────────────────┬───────────────────┘
                 │
┌────────────────▼──────────────────┐
│      Database                     │
│  (MySQL exam_system)              │
└───────────────────────────────────┘
```

---

## Deployment Instructions

1. **Compile**:
   ```bash
   cd /path/to/Online-Exam-JavaProject
   javac -cp lib/mysql-connector-j-9.6.0.jar -d out $(find src -name "*.java")
   ```

2. **Run Server**:
   ```bash
   cd out
   java -cp ../lib/mysql-connector-j-9.6.0.jar MainServer
   ```
   
   Expected output:
   ```
   Server running on port 8080
   Endpoints:
     Student: /login, /startExam, /courses, /exams, /submitAnswers, /results
     Admin:   /admin/* (createCourse, assignTeacher, enrollStudent, removeTeacher, removeStudent, allResults)
     Teacher: /teacher/* (courses, createExam, exams, addQuestion, questions, results)
   ```

3. **Test Admin API**:
   ```bash
   curl -X POST http://localhost:8080/admin/createCourse \
     -d "courseName=Java%20Advanced&description=Advanced%20Java%20Programming"
   ```

4. **Test Teacher API**:
   ```bash
   curl http://localhost:8080/teacher/courses?teacherId=2
   ```

---

## Database Relationships

- **users** → **course_teachers** ← **courses** → **course_enrollments** → **users**
- **courses** → **exams** → **questions** → **attempts** → **student_answers**
- **exams** → **results** (legacy table, can be deprecated)

All relationships enforce CASCADE DELETE for data integrity.
