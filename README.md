UNIVERSITY CLASSROOM AND SCHEDULING MANAGEMENT SYSTEM
Middlesex University — BSc Information Technology
Student: Chinonso Ajagba David | ID: M01071973
======================================================

## SYSTEM OVERVIEW

A web-based University Classroom and Scheduling Management System
that allows administrators to manage rooms, teachers, students,
courses, classes and schedules. The system includes automatic
conflict detection to prevent invalid enrolments and a full
role-based authentication system.

## KEY FEATURES

- Full CRUD management for Rooms, Teachers, Students, Courses,
  Classes and Schedules
- Automatic enrolment conflict detection:
  - Prevents enrolling a student in a full class
  - Prevents enrolling a student in a course above/below their year
  - Prevents enrolling a student in two classes that clash in time
  - Prevents scheduling two classes in the same room at the same time
- Role-based authentication with JWT tokens
- Five user roles: Administrator, Enrollment Officer, Class Handler,
  Student, Teacher
- Each role sees only what they are permitted to access
- Student portal — personal timetable and enrolled classes
- Teacher portal — assigned classes and enrolled students
- Live enrolment progress tracking per class
- Dashboard with real-time system statistics
- Search and filter on all resource pages

## SYSTEM REQUIREMENTS

- Java 17 or higher
- MySQL 8.0 or higher
- IntelliJ IDEA or VSCode (with Java extensions)
- Any modern web browser (Chrome, Firefox, Edge)
- Maven (bundled with IntelliJ IDEA)
- Live Server extension (if using VSCode for frontend)

## PROJECT STRUCTURE

Final_project/
├── backend/ <- Spring Boot REST API
│ └── src/main/
│ ├── java/com/university/scheduling/
│ │ ├── SchedulingApplication.java
│ │ ├── entity/ <- 9 JPA entities (incl. User)
│ │ ├── repository/ <- 9 Spring Data repositories
│ │ ├── service/ <- 9 services + conflict logic
│ │ ├── controller/ <- 10 REST controllers
│ │ ├── exception/ <- Global error handling
│ │ └── config/ <- Security, JWT, CORS config
│ └── resources/
│ ├── application.properties
│ ├── application-local.properties.example
│ ├── schema.sql <- All 9 database tables
│ └── seed-data.sql <- Sample test data
├── frontend/ <- HTML/CSS/JS interface
│ ├── base.css <- Global styles
│ ├── base.js <- Auth + fetch helpers
│ ├── login.html <- Login page
│ ├── register.html <- Registration page
│ ├── dashboard.html <- Admin/Officer/Handler view
│ ├── rooms.html <- Admin only
│ ├── teachers.html <- Admin only
│ ├── students.html <- Admin + Enrollment Officer
│ ├── courses.html <- Admin only
│ ├── classes.html <- Admin + Class Handler
│ ├── schedules.html <- Admin + Class Handler
│ ├── enrolments.html <- Admin + Enrollment Officer
│ ├── student-dashboard.html <- Student portal
│ └── teacher-dashboard.html <- Teacher portal
└── README.txt

## TECHNOLOGY STACK

Backend : Java 17, Spring Boot 3.4.0, Spring Data JPA, Hibernate,
Spring Security, JWT (jjwt 0.11.5)
Database : MySQL 8, JDBC
Frontend : HTML5, CSS3, Bootstrap 5, Vanilla JavaScript (Fetch API)
Tools : VSCode / IntelliJ IDEA, Maven, MySQL Workbench, Postman

## DATABASE SETUP (do this first)

1. Open MySQL Workbench and connect to your local MySQL server
2. Open and run: backend/src/main/resources/schema.sql
   (Creates the university_scheduling database and all 9 tables
   including the users authentication table)
3. Open your project root on terminal and run:
   mysql -u root -p university_scheduling < backend/src/main/resources/seed-data.sql
   Enter your DB password when prompted
   (Inserts sample rooms, teachers, students, courses and classes)

## BACKEND SETUP

1. Open VSCode or IntelliJ IDEA
2. File -> Open -> select the "backend" folder
3. Wait for Maven to download all dependencies
4. Open: src/main/resources/application-local.properties.example
   Rename it to: application-local.properties
5. Set your MySQL password:
   spring.datasource.password=yourpassword
   Update username if yours is not "root"
6. Run SchedulingApplication.java (right-click -> Run)
7. Console should show:
   Started SchedulingApplication in X.XXX seconds
   API is now running on http://localhost:8080

## FRONTEND SETUP

1. Make sure the backend is running first
2. Open the "frontend" folder in VSCode
3. Right-click login.html -> Open with Live Server
   (Runs on http://127.0.0.1:5500/login.html)
4. OR double-click any HTML file to open directly in browser

## USER ACCOUNTS & ROLES

The system has 5 roles. Each role sees only their permitted pages.

## ROLE ACCESS

Administrator : Full access — all pages and all operations
Enrollment Officer: Students page + Enrolments page only
Class Handler : Classes page + Schedules page only
Student : Personal dashboard — own classes and timetable
Teacher : Personal dashboard — own classes and students

DEFAULT ADMIN ACCOUNT (pre-loaded):
Email : admin@university.ac.uk
Password: admin123

To create accounts for other roles, go to register.html and select
the appropriate role. For Student and Teacher roles, enter the
numeric ID that matches their record in the database.

## REGISTERING USERS

Admin, Enrollment Officer, Class Handler:

- Go to register.html
- Enter email, password and select role
- No linked ID required

Student:

- Go to register.html
- Select role: Student
- Enter their Student ID (numeric ID from the students table)
- Use the email that matches their student record

Teacher:

- Go to register.html
- Select role: Teacher
- Enter their Teacher ID (numeric ID from the teachers table)
- Use the email that matches their teacher record

## API ENDPOINTS SUMMARY

POST /api/auth/login
POST /api/auth/register
GET/POST/PUT/DELETE /api/rooms
GET/POST/PUT/DELETE /api/teachers
GET/POST/PUT/DELETE /api/students
GET/POST/PUT/DELETE /api/courses
GET/POST/PUT/DELETE /api/classes
GET/POST/PUT/DELETE /api/schedules
GET/POST /api/enrolments/enrol
GET/POST /api/enrolments/drop
GET/POST/PUT/DELETE /api/class-teachers
GET /api/student-portal/me
GET /api/student-portal/my-enrolments
GET /api/student-portal/my-schedule
GET /api/teacher-portal/me
GET /api/teacher-portal/my-classes
GET /api/teacher-portal/my-classes/{id}/students

## CONFLICT DETECTION LOGIC

All conflict checks are in:
backend/src/main/java/com/university/scheduling/service/EnrolmentService.java

Checks run in this order on every enrolment attempt:

1. Student must be ACTIVE status
2. Student must not already be enrolled in the same class
3. Class must not be at full capacity
4. Course level must match student year of study
5. No existing class at the same day and time

Each failed check returns a specific error message shown clearly
in the enrolment UI.

## SAMPLE TEST DATA

After running seed-data.sql the database contains:

- 8 Rooms (Science Block, Engineering Block, ICT Centre etc.)
- 6 Teachers
- 10 Students (Years 1 to 4)
- 12 Courses (Levels 1 to 3)
- 10 Classes with enrolment data
- 14 Schedule slots

## TROUBLESHOOTING

"Failed to load rooms / stats" on page:
-> Make sure Spring Boot is running on port 8080

"Access denied" MySQL error:
-> Check password in application-local.properties

"403 Forbidden" on API call:
-> You are logged in with a role that cannot access that endpoint
-> Log out and log in with the correct role

Page redirects to login.html unexpectedly:
-> Your session token has expired — log in again

Page shows no data but no error:
-> Open browser DevTools (F12) -> Console tab for details

Port 8080 already in use:
-> Change server.port=8081 in application.properties
-> Update API_BASE in frontend/base.js to:
const API_BASE = 'http://localhost:8081/api';

```

```
