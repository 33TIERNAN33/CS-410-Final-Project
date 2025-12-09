DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS classes;
DROP TABLE IF EXISTS students;

CREATE TABLE students (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64) NOT NULL UNIQUE,
    university_id VARCHAR(32) NOT NULL UNIQUE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL
);

CREATE TABLE classes (
    class_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_number  VARCHAR(16) NOT NULL,
    term           VARCHAR(16) NOT NULL,
    section_number INT NOT NULL,
    description    VARCHAR(255) NOT NULL,
    UNIQUE (course_number, term, section_number)
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id    INT NOT NULL,
    name        VARCHAR(64) NOT NULL,
    weight      DECIMAL(6,3) NOT NULL,
    UNIQUE (class_id, name),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
);

CREATE TABLE assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id      INT NOT NULL,
    category_id   INT NOT NULL,
    name          VARCHAR(128) NOT NULL,
    description   TEXT,
    points        DECIMAL(8,3) NOT NULL,
    UNIQUE (class_id, name),
    FOREIGN KEY (class_id) REFERENCES classes(class_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id      INT NOT NULL,
    student_id    INT NOT NULL,
    UNIQUE (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE grades (
    assignment_id INT NOT NULL,
    student_id    INT NOT NULL,
    points_earned DECIMAL(8,3) NOT NULL,
    PRIMARY KEY (assignment_id, student_id),
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
