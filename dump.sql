DELETE FROM grades;
DELETE FROM enrollments;
DELETE FROM assignments;
DELETE FROM categories;
DELETE FROM classes;
DELETE FROM students;

INSERT INTO students (student_id, username, university_id, first_name, last_name) VALUES
  (1, 'jdoe',  'U0001', 'John',  'Doe'),
  (2, 'asmith','U0002', 'Alice', 'Smith'),
  (3, 'bchen', 'U0003', 'Bao',   'Chen');

INSERT INTO classes (class_id, course_number, term, section_number, description) VALUES
  (1, 'CS410', 'FA25', 1, 'Databases'),
  (2, 'CS121', 'FA25', 1, 'Intro to Computer Science');

INSERT INTO categories (category_id, class_id, name, weight) VALUES
  (1, 1, 'Homework', 40.000),
  (2, 1, 'Exam',     40.000),
  (3, 1, 'Project',  20.000),
  (4, 2, 'Homework', 60.000),
  (5, 2, 'Exam',     40.000);

INSERT INTO assignments (assignment_id, class_id, category_id, name, description, points) VALUES
  (1, 1, 1, 'HW1',   'Relational model exercises', 20.000),
  (2, 1, 1, 'HW2',   'SQL practice', 20.000),
  (3, 1, 2, 'Midterm', 'Midterm exam', 100.000),
  (4, 1, 2, 'Final', 'Final exam', 100.000),
  (5, 1, 3, 'Project', 'Course project', 100.000),
  (6, 2, 4, 'HW1', 'Intro programming homework', 10.000),
  (7, 2, 5, 'Final', 'Final exam', 100.000);

INSERT INTO enrollments (enrollment_id, class_id, student_id) VALUES
  (1, 1, 1),
  (2, 1, 2),
  (3, 1, 3),
  (4, 2, 1),
  (5, 2, 2);

INSERT INTO grades (assignment_id, student_id, points_earned) VALUES
  (1, 1, 18.000),
  (1, 2, 20.000),
  (1, 3, 16.000),
  (2, 1, 19.500),
  (2, 2, 18.000),
  (3, 1, 85.000),
  (3, 2, 92.000),
  (3, 3, 70.000),
  (4, 1, 90.000),
  (5, 1, 95.000),
  (5, 3, 88.000),
  (6, 1, 9.000),
  (6, 2, 8.000),
  (7, 1, 93.000),
  (7, 2, 88.000);
