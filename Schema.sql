-- Dropping tables if they exist to reset the database.
-- The order is important to avoid foreign key errors.DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS classes;
DROP TABLE IF EXISTS students;

-- Table to store student information.
-- Username and university_id must be unique (no duplicates allowed).
CREATE TABLE students (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64) NOT NULL UNIQUE,
    university_id VARCHAR(32) NOT NULL UNIQUE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL
);

-- Table to store class details.
-- Defines the course, term, section, and description.
CREATE TABLE classes (
    class_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_number  VARCHAR(16) NOT NULL,
    term           VARCHAR(16) NOT NULL,
    section_number INT NOT NULL,
    description    VARCHAR(255) NOT NULL,
    -- Unique constraint ensures the same section is not created twice.
    UNIQUE (course_number, term, section_number)
);

-- Table for grade categories (e.g., Homework, Exam).
-- Each category is linked to a specific class using class_id.
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id    INT NOT NULL,
    name        VARCHAR(64) NOT NULL,
    weight      DECIMAL(6,3) NOT NULL,
    -- Ensures a class cannot have two categories with the same name.
    UNIQUE (class_id, name),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
);

-- Table for assignments within a category.
-- Includes the max points possible for the assignment.
CREATE TABLE assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id      INT NOT NULL,
    category_id   INT NOT NULL,
    name          VARCHAR(128) NOT NULL,
    description   TEXT,
    points        DECIMAL(8,3) NOT NULL,
    -- Constraint to prevent duplicate assignment names in the same class.
    UNIQUE (class_id, name),
    FOREIGN KEY (class_id) REFERENCES classes(class_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Junction table to link students and classes.
-- Represents the enrollment of a student in a class.
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id      INT NOT NULL,
    student_id    INT NOT NULL,
    -- Ensures a student is enrolled in a specific class only once.
    UNIQUE (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- Table to store the grades earned by students.
-- Links a specific student to a specific assignment.
CREATE TABLE grades (
    assignment_id INT NOT NULL,
    student_id    INT NOT NULL,
    points_earned DECIMAL(8,3) NOT NULL,
    -- Primary key ensures one grade per student per assignment.
    PRIMARY KEY (assignment_id, student_id),
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
