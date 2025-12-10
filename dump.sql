USE gradebook; -- Ensure this matches your actual database name

-- 1. Disable safety checks to allow bulk deletion
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- 2. Clear existing data (Cleanup)
DELETE FROM grades;
DELETE FROM enrollments;
DELETE FROM assignments;
DELETE FROM categories;
DELETE FROM classes;
DELETE FROM students;

-- 3. Insert Students (7 Students)
INSERT INTO students (student_id, username, university_id, first_name, last_name) VALUES
  (1, 'jdoe',   'U0001', 'John',    'Doe'),
  (2, 'asmith', 'U0002', 'Alice',   'Smith'),
  (3, 'bchen',  'U0003', 'Bao',     'Chen'),
  (4, 'mgarcia','U0004', 'Maria',   'Garcia'),
  (5, 'rjones', 'U0005', 'Robert',  'Jones'),
  (6, 'slee',   'U0006', 'Sarah',   'Lee'),
  (7, 'dkim',   'U0007', 'David',   'Kim');

-- 4. Insert Classes
INSERT INTO classes (class_id, course_number, term, section_number, description) VALUES
  (1, 'CS410', 'FA25', 1, 'Databases'),
  (2, 'CS121', 'FA25', 1, 'Intro to Computer Science');

-- 5. Insert Categories
INSERT INTO categories (category_id, class_id, name, weight) VALUES
  (1, 1, 'Homework', 40.000),
  (2, 1, 'Exam',     40.000),
  (3, 1, 'Project',  20.000),
  (4, 2, 'Homework', 60.000),
  (5, 2, 'Exam',     40.000);

-- 6. Insert Assignments
INSERT INTO assignments (assignment_id, class_id, category_id, name, description, points) VALUES
  -- CS410 Assignments
  (1, 1, 1, 'HW1',     'Relational model exercises', 20.000),
  (2, 1, 1, 'HW2',     'SQL practice', 20.000),
  (3, 1, 2, 'Midterm', 'Midterm exam', 100.000),
  (4, 1, 2, 'Final',   'Final exam', 100.000),
  (5, 1, 3, 'Project', 'Course project', 100.000),
  -- CS121 Assignments
  (6, 2, 4, 'HW1',     'Intro programming homework', 10.000),
  (7, 2, 5, 'Final',   'Final exam', 100.000);

-- 7. Insert Enrollments
INSERT INTO enrollments (enrollment_id, class_id, student_id) VALUES
  -- CS410 Enrollment (Most students enrolled)
  (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
  -- CS121 Enrollment (Some students enrolled)
  (6, 2, 1), (7, 2, 2), (8, 2, 6), (9, 2, 7);

-- 8. Insert Grades
INSERT INTO grades (assignment_id, student_id, points_earned) VALUES
  -- HW1 (CS410)
  (1, 1, 18.0), (1, 2, 20.0), (1, 3, 16.0), (1, 4, 19.0), (1, 5, 15.0),
  -- HW2 (CS410)
  (2, 1, 19.5), (2, 2, 18.0), (2, 3, 20.0), (2, 4, 17.5), (2, 5, 12.0),
  -- Midterm (CS410)
  (3, 1, 85.0), (3, 2, 92.0), (3, 3, 70.0), (3, 4, 88.0), (3, 5, 65.0),
  -- Final (CS410) - Note: Some grades might be missing intentionally
  (4, 1, 90.0), (4, 4, 95.0),
  -- Project (CS410)
  (5, 1, 95.0), (5, 3, 88.0), (5, 5, 80.0),
  
  -- HW1 (CS121)
  (6, 1, 9.0), (6, 2, 8.0), (6, 6, 10.0), (6, 7, 7.5),
  -- Final (CS121)
  (7, 1, 93.0), (7, 2, 88.0), (7, 6, 95.0), (7, 7, 82.0);

-- 9. Re-enable safety checks
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
