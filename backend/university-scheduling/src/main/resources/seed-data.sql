-- ============================================================
-- SEED DATA FOR TESTING
-- Run AFTER schema.sql 
-- ============================================================

USE university_scheduling;

-- ============================================================
-- ROOMS
-- ============================================================
INSERT INTO room (room_code, building, capacity, has_projector, has_computers, room_type, is_active) VALUES
('SCI-101', 'Science Block',     120, TRUE,  FALSE, 'LECTURE_HALL', TRUE),
('SCI-102', 'Science Block',      40, TRUE,  TRUE,  'LAB',          TRUE),
('ENG-201', 'Engineering Block',  80, TRUE,  FALSE, 'LECTURE_HALL', TRUE),
('ENG-205', 'Engineering Block',  25, FALSE, TRUE,  'SEMINAR_ROOM', TRUE),
('ART-301', 'Arts Building',      60, TRUE,  FALSE, 'LECTURE_HALL', TRUE),
('ICT-101', 'ICT Centre',         30, TRUE,  TRUE,  'LAB',          TRUE),
('LIB-001', 'Library Block',      20, FALSE, FALSE, 'SEMINAR_ROOM', TRUE),
('MAIN-001','Main Building',     200, TRUE,  FALSE, 'LECTURE_HALL', TRUE);

-- ============================================================
-- TEACHERS
-- ============================================================
INSERT INTO teacher (employee_id, first_name, last_name, email, department, specialization, max_hours_per_week) VALUES
('EMP001', 'James',   'Okafor',    'j.okafor@university.ac.uk',    'Computer Science',   'Software Engineering',   20),
('EMP002', 'Amaka',   'Nwosu',     'a.nwosu@university.ac.uk',     'Computer Science',   'Databases & AI',         18),
('EMP003', 'David',   'Williams',  'd.williams@university.ac.uk',  'Mathematics',        'Calculus & Algebra',     22),
('EMP004', 'Sarah',   'Johnson',   's.johnson@university.ac.uk',   'Information Tech',   'Networking & Security',  20),
('EMP005', 'Michael', 'Brown',     'm.brown@university.ac.uk',     'Engineering',        'Electronics',            20),
('EMP006', 'Grace',   'Adeyemi',   'g.adeyemi@university.ac.uk',   'Business',           'Management & Strategy',  16);

-- ============================================================
-- STUDENTS
-- ============================================================
INSERT INTO student (first_name, last_name, email, program, year_of_study, enrolment_status) VALUES
('Chinonso', 'Ajagba',   'c.ajagba@student.ac.uk',   'BSc Information Technology', 3, 'ACTIVE'),
('Emeka',    'Eze',      'e.eze@student.ac.uk',       'BSc Computer Science',       2, 'ACTIVE'),
('Fatima',   'Bello',    'f.bello@student.ac.uk',     'BSc Computer Science',       1, 'ACTIVE'),
('Tobi',     'Adeyemi',  't.adeyemi@student.ac.uk',   'BSc Information Technology', 1, 'ACTIVE'),
('Linda',    'Okonkwo',  'l.okonkwo@student.ac.uk',   'BEng Engineering',           2, 'ACTIVE'),
('Chukwudi', 'Nnaji',    'c.nnaji@student.ac.uk',     'BSc Mathematics',            3, 'ACTIVE'),
('Halima',   'Musa',     'h.musa@student.ac.uk',      'BSc Computer Science',       2, 'ACTIVE'),
('Peter',    'Ogbonnaya','p.ogbonnaya@student.ac.uk', 'BEng Engineering',           1, 'ACTIVE'),
('Ngozi',    'Obi',      'n.obi@student.ac.uk',       'BSc Information Technology', 4, 'ACTIVE'),
('Sola',     'Adebayo',  's.adebayo@student.ac.uk',   'BSc Computer Science',       3, 'ACTIVE');

-- ============================================================
-- COURSES
-- ============================================================
INSERT INTO course (course_code, course_name, credits, department, level) VALUES
('CST1100', 'Introduction to Programming',        3, 'Computer Science',   1),
('CST1200', 'Foundations of Databases',           3, 'Computer Science',   1),
('CST2100', 'Object Oriented Programming',        3, 'Computer Science',   2),
('CST2200', 'Data Structures and Algorithms',     3, 'Computer Science',   2),
('CST3350', 'Advanced Database Systems',          3, 'Computer Science',   3),
('CST3400', 'Software Engineering',               3, 'Computer Science',   3),
('ITT1100', 'Introduction to Networking',         3, 'Information Tech',   1),
('ITT2100', 'Web Development',                    3, 'Information Tech',   2),
('ITT3100', 'Cybersecurity Fundamentals',         3, 'Information Tech',   3),
('MTH1100', 'Calculus I',                         4, 'Mathematics',        1),
('MTH2100', 'Linear Algebra',                     4, 'Mathematics',        2),
('ENG1100', 'Engineering Mathematics',            4, 'Engineering',        1);

-- ============================================================
-- CLASSES (class sections)
-- ============================================================
INSERT INTO class (course_id, section_number, academic_year, semester, room_id, max_enrolment, current_enrolment, status) VALUES
(1,  '01', '2024/2025', 'SPRING', 1, 100, 3, 'ONGOING'),   -- CST1100 Section 01
(1,  '02', '2024/2025', 'SPRING', 5, 50,  2, 'ONGOING'),   -- CST1100 Section 02
(2,  '01', '2024/2025', 'SPRING', 3, 70,  2, 'ONGOING'),   -- CST1200 Section 01
(3,  '01', '2024/2025', 'SPRING', 3, 70,  2, 'ONGOING'),   -- CST2100 Section 01
(5,  '01', '2024/2025', 'SPRING', 1, 100, 1, 'ONGOING'),   -- CST3350 Section 01
(6,  '01', '2024/2025', 'SPRING', 4, 20,  1, 'ONGOING'),   -- CST3400 Section 01 (small seminar)
(7,  '01', '2024/2025', 'SPRING', 5, 50,  1, 'ONGOING'),   -- ITT1100 Section 01
(9,  '01', '2024/2025', 'SPRING', 6, 25,  1, 'ONGOING'),   -- ITT3100 Section 01
(10, '01', '2024/2025', 'SPRING', 8, 150, 2, 'ONGOING'),   -- MTH1100 Section 01
(12, '01', '2024/2025', 'SPRING', 3, 70,  1, 'ONGOING');   -- ENG1100 Section 01

-- ============================================================
-- SCHEDULES (time slots for each class)
-- ============================================================
INSERT INTO schedule (class_id, day_of_week, start_time, end_time, frequency) VALUES
(1,  'MON', '09:00:00', '11:00:00', 'WEEKLY'),   -- CST1100 S01 Mon 9-11
(1,  'WED', '09:00:00', '11:00:00', 'WEEKLY'),   -- CST1100 S01 Wed 9-11
(2,  'TUE', '09:00:00', '11:00:00', 'WEEKLY'),   -- CST1100 S02 Tue 9-11
(3,  'MON', '11:00:00', '13:00:00', 'WEEKLY'),   -- CST1200 S01 Mon 11-1
(3,  'THU', '11:00:00', '13:00:00', 'WEEKLY'),   -- CST1200 S01 Thu 11-1
(4,  'TUE', '11:00:00', '13:00:00', 'WEEKLY'),   -- CST2100 S01 Tue 11-1
(4,  'FRI', '09:00:00', '11:00:00', 'WEEKLY'),   -- CST2100 S01 Fri 9-11
(5,  'WED', '13:00:00', '15:00:00', 'WEEKLY'),   -- CST3350 S01 Wed 1-3
(5,  'FRI', '13:00:00', '15:00:00', 'WEEKLY'),   -- CST3350 S01 Fri 1-3
(6,  'THU', '14:00:00', '16:00:00', 'WEEKLY'),   -- CST3400 S01 Thu 2-4
(7,  'MON', '13:00:00', '15:00:00', 'WEEKLY'),   -- ITT1100 S01 Mon 1-3
(8,  'TUE', '14:00:00', '16:00:00', 'WEEKLY'),   -- ITT3100 S01 Tue 2-4
(9,  'MON', '09:00:00', '11:00:00', 'WEEKLY'),   -- MTH1100 S01 Mon 9-11 (different room, no clash)
(10, 'WED', '11:00:00', '13:00:00', 'WEEKLY');   -- ENG1100 S01 Wed 11-1

-- ============================================================
-- ENROLMENTS (linking students to classes)
-- ============================================================
INSERT INTO enrolment (student_id, class_id, enrolment_date, status) VALUES
-- Fatima (Year 1, CS) enrolled in Year 1 courses
(3, 1,  '2025-01-10', 'ENROLLED'),  -- CST1100
(3, 3,  '2025-01-10', 'ENROLLED'),  -- CST1200
(3, 9,  '2025-01-10', 'ENROLLED'),  -- MTH1100
-- Tobi (Year 1, IT) enrolled in Year 1 courses
(4, 2,  '2025-01-11', 'ENROLLED'),  -- CST1100 S02
(4, 7,  '2025-01-11', 'ENROLLED'),  -- ITT1100
-- Emeka (Year 2, CS) enrolled in Year 2 courses
(2, 4,  '2025-01-09', 'ENROLLED'),  -- CST2100
-- Chinonso (Year 3, IT) enrolled in Year 3 courses
(1, 5,  '2025-01-08', 'ENROLLED'),  -- CST3350
(1, 8,  '2025-01-08', 'ENROLLED'),  -- ITT3100
-- Sola (Year 3, CS)
(10, 5, '2025-01-10', 'ENROLLED'),  -- CST3350
(10, 6, '2025-01-10', 'ENROLLED');  -- CST3400

-- ============================================================
-- CLASS_TEACHER (assigning teachers to classes)
-- ============================================================
INSERT INTO class_teacher (class_id, teacher_id, role) VALUES
(1,  1, 'LECTURER'),            -- James teaches CST1100 S01
(2,  1, 'LECTURER'),            -- James teaches CST1100 S02
(3,  2, 'LECTURER'),            -- Amaka teaches CST1200
(4,  1, 'LECTURER'),            -- James teaches CST2100
(4,  2, 'TEACHING_ASSISTANT'),  -- Amaka assists CST2100
(5,  2, 'LECTURER'),            -- Amaka teaches CST3350
(6,  1, 'LECTURER'),            -- James teaches CST3400
(7,  4, 'LECTURER'),            -- Sarah teaches ITT1100
(8,  4, 'LECTURER'),            -- Sarah teaches ITT3100
(9,  3, 'LECTURER'),            -- David teaches MTH1100
(10, 5, 'LECTURER');            -- Michael teaches ENG1100

SELECT 'Seed data inserted successfully!' AS message;
