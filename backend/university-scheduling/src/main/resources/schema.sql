-- ============================================================
-- UNIVERSITY CLASSROOM AND SCHEDULING MANAGEMENT SYSTEM
-- Database Schema + Seed Data
-- Run this manually in MySQL Workbench or CLI 
-- ============================================================

CREATE DATABASE IF NOT EXISTS university_scheduling;
USE university_scheduling;

-- ============================================================
-- TABLE: room
-- ============================================================
CREATE TABLE IF NOT EXISTS room (
    room_id       INT PRIMARY KEY AUTO_INCREMENT,
    room_code     VARCHAR(20)  NOT NULL UNIQUE,
    building      VARCHAR(50)  NOT NULL,
    capacity      INT          NOT NULL CHECK (capacity > 0),
    has_projector BOOLEAN      NOT NULL DEFAULT FALSE,
    has_computers BOOLEAN      NOT NULL DEFAULT FALSE,
    room_type     ENUM('LECTURE_HALL', 'LAB', 'SEMINAR_ROOM') NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ============================================================
-- TABLE: teacher
-- ============================================================
CREATE TABLE IF NOT EXISTS teacher (
    teacher_id        INT PRIMARY KEY AUTO_INCREMENT,
    employee_id       VARCHAR(20)  NOT NULL UNIQUE,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    department        VARCHAR(50)  NOT NULL,
    specialization    VARCHAR(100),
    max_hours_per_week INT         DEFAULT 20
);

-- ============================================================
-- TABLE: student
-- ============================================================
CREATE TABLE IF NOT EXISTS student (
    student_id        INT PRIMARY KEY AUTO_INCREMENT,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    program           VARCHAR(100) NOT NULL,
    year_of_study     INT          NOT NULL CHECK (year_of_study BETWEEN 1 AND 4),
    enrolment_status  ENUM('ACTIVE', 'GRADUATED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE'
);

-- ============================================================
-- TABLE: course
-- ============================================================
CREATE TABLE IF NOT EXISTS course (
    course_id   INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20)  NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits     INT          NOT NULL,
    department  VARCHAR(50)  NOT NULL,
    level       INT          NOT NULL CHECK (level BETWEEN 1 AND 4)
);

-- ============================================================
-- TABLE: class  (named `class` in DB, ClassSection in Java)
-- ============================================================
CREATE TABLE IF NOT EXISTS class (
    class_id           INT PRIMARY KEY AUTO_INCREMENT,
    course_id          INT          NOT NULL,
    section_number     VARCHAR(10)  NOT NULL,
    academic_year      VARCHAR(9)   NOT NULL,
    semester           ENUM('FALL', 'SPRING', 'SUMMER') NOT NULL,
    room_id            INT,
    max_enrolment      INT          NOT NULL,
    current_enrolment  INT          NOT NULL DEFAULT 0,
    status             ENUM('SCHEDULED', 'ONGOING', 'COMPLETED') NOT NULL DEFAULT 'SCHEDULED',
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE RESTRICT,
    FOREIGN KEY (room_id)   REFERENCES room(room_id)     ON DELETE SET NULL
);

-- ============================================================
-- TABLE: schedule
-- ============================================================
CREATE TABLE IF NOT EXISTS schedule (
    schedule_id  INT PRIMARY KEY AUTO_INCREMENT,
    class_id     INT          NOT NULL,
    day_of_week  ENUM('MON', 'TUE', 'WED', 'THU', 'FRI') NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    frequency    ENUM('WEEKLY', 'BIWEEKLY') NOT NULL DEFAULT 'WEEKLY',
    FOREIGN KEY (class_id) REFERENCES class(class_id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: enrolment  (junction: student <-> class)
-- ============================================================
CREATE TABLE IF NOT EXISTS enrolment (
    enrolment_id   INT PRIMARY KEY AUTO_INCREMENT,
    student_id     INT          NOT NULL,
    class_id       INT          NOT NULL,
    enrolment_date DATE         NOT NULL,
    status         ENUM('ENROLLED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'ENROLLED',
    grade          VARCHAR(2),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id)   REFERENCES class(class_id)     ON DELETE RESTRICT
);

-- ============================================================
-- TABLE: class_teacher  (junction: class <-> teacher)
-- ============================================================
CREATE TABLE IF NOT EXISTS class_teacher (
    class_teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    class_id         INT          NOT NULL,
    teacher_id       INT          NOT NULL,
    role             ENUM('LECTURER', 'TEACHING_ASSISTANT') NOT NULL DEFAULT 'LECTURER',
    FOREIGN KEY (class_id)   REFERENCES class(class_id)     ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id) ON DELETE RESTRICT
);

-- ============================================================
-- USER: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id    INT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','ENROLLMENT_OFFICER','CLASS_HANDLER','STUDENT','TEACHER')
               NOT NULL,
    linked_id  INT DEFAULT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- INDEXES for faster conflict detection queries
-- ============================================================
CREATE INDEX idx_schedule_class     ON schedule(class_id);
CREATE INDEX idx_schedule_day_time  ON schedule(day_of_week, start_time, end_time);
CREATE INDEX idx_enrolment_student  ON enrolment(student_id);
CREATE INDEX idx_enrolment_class    ON enrolment(class_id);
CREATE INDEX idx_class_room         ON class(room_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role  ON users(role);