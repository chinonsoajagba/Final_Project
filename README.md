UNIVERSITY CLASSROOM AND SCHEDULING MANAGEMENT SYSTEM
Middlesex University - BSc Information Technology
Student: Chinonso Ajagba David | ID: M01071973
======================================================

## SYSTEM OVERVIEW

A web-based University Classroom and Scheduling Management System
that allows administrators to manage rooms, teachers, students,
courses, classes and schedules. The system includes automatic
conflict detection to prevent invalid enrolments.

## KEY FEATURES

- Full CRUD management for Rooms, Teachers, Students, Courses,
  Classes and Schedules
- Automatic enrolment conflict detection:
  - Prevents enrolling a student in a full class
  - Prevents enrolling a student in a course above/below their year
  - Prevents enrolling a student in two classes that clash in time
  - Prevents scheduling two classes in the same room at the same time
- Live enrolment progress tracking per class
- Dashboard with real-time system statistics
- Clean admin interface with search and filter on all pages

## SYSTEM REQUIREMENTS

- Java 17 or higher
- MySQL 8.0 or higher
- IntelliJ IDEA (recommended) or any Java IDE
- Any modern web browser (Chrome, Firefox, Edge)
- Maven

## PROJECT STRUCTURE

Final_project/
├── backend/ <- Spring Boot REST API
│ └── src/main/
│ ├── java/com/university/scheduling/
│ │ ├── SchedulingApplication.java
│ │ ├── entity/ <- 8 JPA database entities
│ │ ├── repository/ <- 8 Spring Data repositories
│ │ ├── service/ <- 8 services + conflict logic
│ │ ├── controller/ <- 8 REST API controllers
│ │ ├── exception/ <- Global error handling
│ │ └── config/ <- CORS configuration
│ └── resources/
│ ├── application.properties <- Database config
│ ├── schema.sql <- Database tables
│ └── seed-data.sql <- Sample test data
├── frontend/ <- HTML/CSS/JS interface
│ ├── base.css <- Global styles
│ ├── base.js <- Shared fetch helpers
│ ├── dashboard.html <- System overview
│ ├── rooms.html
│ ├── teachers.html
│ ├── students.html
│ ├── courses.html
│ ├── classes.html
│ ├── schedules.html
│ └── enrolments.html <- Conflict detection UI
└── README.txt

## TECHNOLOGY STACK

Backend : Java 17, Spring Boot 3.4.0, Spring Data JPA, Hibernate
Database : MySQL 8, JDBC
Frontend : HTML5, CSS3, Bootstrap 5, Vanilla JavaScript (Fetch API)
Tools : VSCode/IntelliJ IDEA, Maven, MySQL Workbench, Postman

## DATABASE SETUP (do this first)

1. Open MySQL Workbench and connect to your local MySQL server
2. Open and run on your MySQL Workbench: backend/src/main/resources/schema.sql
   (This creates the university_scheduling database and all 8 tables)
3. Open your project root on terminal and run:
   mysql -u root -p university_scheduling < backend/university-scheduling/src/main/resources/seed-data.sql
   Input your DB password if popped-up
   (This inserts sample rooms, teachers, students, courses and classes)

## BACKEND SETUP

1. Open VSCode or IntelliJ IDEA
2. File -> Open -> select the "backend" folder
3. Wait for Maven to download all dependencies
4. Open: src/main/resources/application-local.properties.example
   (Rename it to application-local.properties).
5. Change this line to match your MySQL password:
   spring.datasource.password=yourpassword
   If your MySQL username is not "root", update that line too
6. Run SchedulingApplication.java (right-click -> Run)
7. Wait for the console to show:
   Started SchedulingApplication in X.XXX seconds
   The API is now running on http://localhost:8080

## FRONTEND SETUP

1. Make sure the backend is running first
2. Open the "frontend" folder
3. Double-click dashboard.html to open in your browser
4. All pages connect to the backend automatically at localhost:8080

## API ENDPOINTS SUMMARY

GET/POST/PUT/DELETE /api/rooms
GET/POST/PUT/DELETE /api/teachers
GET/POST/PUT/DELETE /api/students
GET/POST/PUT/DELETE /api/courses
GET/POST/PUT/DELETE /api/classes
GET/POST/PUT/DELETE /api/schedules
GET/POST /api/enrolments/enrol
GET/POST /api/enrolments/drop
GET/POST/PUT/DELETE /api/class-teachers

## CONFLICT DETECTION LOGIC

All conflict checks are implemented in:
backend/src/main/java/com/chinonso/university_scheduling/service/EnrolmentService.java

The following checks run in order every time a student is enrolled:

1. Student must be ACTIVE status
2. Student must not already be enrolled in the same class
3. Class must not be at full capacity (currentEnrolment >= maxEnrolment)
4. Course level must match student year of study
5. Student must have no existing class at the same day and time

If any check fails, the system returns a clear error message
explaining exactly why the enrolment was rejected.

## SAMPLE LOGIN / TEST DATA

No authentication required — the system is an admin interface.
Sample data is loaded from seed-data.sql and includes:

- 8 Rooms (Science Block, Engineering Block, ICT Centre etc.)
- 6 Teachers
- 10 Students (Years 1 to 4)
- 12 Courses (Level 1 to 3)
- 10 Classes with enrolment
- 14 Schedule slots

## TROUBLESHOOTING

- "Failed to load rooms" on the page:
  Make sure Spring Boot is running on port 8080

- "Access denied" MySQL error:
  Check your password in application-local.properties

- Page shows no data but no error:
  Open browser DevTools (F12) -> Console tab and check for errors

- Port 8080 already in use:
  Change server.port=8081 in application.properties
  Then update API_BASE in frontend/base.js to:
  const API_BASE = 'http://localhost:8081/api';
