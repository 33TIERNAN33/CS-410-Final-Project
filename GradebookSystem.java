import java.sql.*;

/**
 * Manages the core logic of the Gradebook application.
 * Handles database interactions for classes, students, assignments, and grades.
 */
public class GradebookSystem {
    
    /** The ID of the currently selected class. Null if no class is selected. */
    private Integer currentClassId = null;
    
    /** The name of the currently selected course (e.g., "CS410"). */
    private String currentCourseName = null;
    
    /** The active database connection. */
    private Connection conn;

    /**
     * Initializes the system and establishes a database connection.
     */
    public GradebookSystem() {
        try { 
            this.conn = DB.connect(); 
        } catch (SQLException e) { 
            System.out.println("Connection failed: " + e.getMessage()); 
        }
    }

    /**
     * Closes the active database connection.
     * Should be called when the program terminates.
     */
    public void close() {
        try { 
            if (conn != null) conn.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Generates the command prompt string based on the active class.
     * * @return A string like "CS410 > " if a class is selected, or "> " otherwise.
     */
    public String getPrompt() {
        return (currentCourseName == null) ? "> " : currentCourseName + " > ";
    }

    // --- CLASS MANAGEMENT ---

    /**
     * Lists all available classes and the number of enrolled students.
     * Prints the list to the console.
     */
    public void listClasses() {
        String sql = "SELECT c.class_id, c.course_number, c.term, c.section_number, count(e.student_id) as student_count " +
                     "FROM classes c LEFT JOIN enrollments e ON c.class_id = e.class_id " +
                     "GROUP BY c.class_id, c.course_number, c.term, c.section_number";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Class List ---");
            while (rs.next()) {
                System.out.printf("%d: %s %s (Sec: %d) - Students: %d\n",
                    rs.getInt("class_id"), rs.getString("course_number"),
                    rs.getString("term"), rs.getInt("section_number"), rs.getInt("student_count"));
            }
            System.out.println();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Selects a class to be the active context.
     * Validates that only one section matches the criteria.
     * * @param courseNum The course number (e.g., "CS410").
     * @param term      The term (optional, e.g., "Sp20").
     * @param section   The section number (optional, e.g., "1").
     */
    public void selectClass(String courseNum, String term, String section) {
        String sql = "SELECT class_id, course_number, term, section_number FROM classes WHERE course_number = ?";
        if (term != null) sql += " AND term = ?";
        if (section != null) sql += " AND section_number = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseNum);
            int idx = 2;
            if (term != null) pstmt.setString(idx++, term);
            if (section != null) pstmt.setInt(idx, Integer.parseInt(section));

            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Class not found.");
            } else {
                int firstId = rs.getInt("class_id");
                String firstCourse = rs.getString("course_number");
                String firstTerm = rs.getString("term");
                int firstSec = rs.getInt("section_number");
                
                if (rs.next()) {
                    System.out.println("Error: Multiple sections found. Please specify term and/or section.");
                } else {
                    currentClassId = firstId;
                    currentCourseName = firstCourse;
                    System.out.printf("Class Selected: %s %s (Sec: %d)\n", currentCourseName, firstTerm, firstSec);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Creates a new class in the database.
     * * @param num   The course number (e.g., "CS101").
     * @param term  The term (e.g., "FA25").
     * @param sec   The section number.
     * @param desc  The description of the course.
     */
    public void createClass(String num, String term, int sec, String desc) {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO classes (course_number, term, section_number, description) VALUES (?, ?, ?, ?)")) {
            p.setString(1, num); p.setString(2, term); p.setInt(3, sec); p.setString(4, desc);
            p.executeUpdate(); 
            System.out.println("New class created successfully.");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }
    
    /**
     * Prints the currently active class to the console.
     */
    public void showActiveClass() {
        if (currentClassId != null) System.out.println("Active Class ID: " + currentClassId + " (" + currentCourseName + ")");
        else System.out.println("No class selected.");
    }

    // --- ASSIGNMENTS & CATEGORIES ---

    /**
     * Lists all categories and their weights for the current class.
     */
    public void showCategories() {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try (PreparedStatement p = conn.prepareStatement("SELECT name, weight FROM categories WHERE class_id = ?")) {
            p.setInt(1, currentClassId); ResultSet rs = p.executeQuery();
            System.out.println("\n--- Categories ---");
            while (rs.next()) System.out.printf("%s (Weight: %.2f)\n", rs.getString("name"), rs.getDouble("weight"));
            System.out.println();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Adds a new category to the active class.
     * * @param name   The name of the category (e.g., "Homework").
     * @param weight The weight of the category.
     */
    public void addCategory(String name, double weight) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO categories (class_id, name, weight) VALUES (?, ?, ?)")) {
            p.setInt(1, currentClassId); p.setString(2, name); p.setDouble(3, weight);
            p.executeUpdate(); 
            System.out.println("Category added.");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Lists assignments grouped by category for the current class.
     */
    public void showAssignments() {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        String sql = "SELECT c.name AS cat_name, a.name, a.points FROM assignments a JOIN categories c ON a.category_id = c.category_id WHERE a.class_id = ? ORDER BY c.name, a.name";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, currentClassId); ResultSet rs = p.executeQuery();
            System.out.println("\n--- Assignments ---");
            while (rs.next()) System.out.printf("[%s] %s (Points: %.2f)\n", rs.getString("cat_name"), rs.getString("name"), rs.getDouble("points"));
            System.out.println();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Adds a new assignment to a specific category.
     * * @param name    The name of the assignment (e.g., "HW1").
     * @param catName The name of the category it belongs to.
     * @param desc    A brief description.
     * @param points  The maximum points possible.
     */
    public void addAssignment(String name, String catName, String desc, double points) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try {
            int catId = -1;
            try(PreparedStatement p = conn.prepareStatement("SELECT category_id FROM categories WHERE class_id=? AND name=?")) {
                p.setInt(1, currentClassId); p.setString(2, catName); ResultSet rs = p.executeQuery();
                if(rs.next()) catId = rs.getInt(1); else { System.out.println("Category not found."); return; }
            }
            try(PreparedStatement p = conn.prepareStatement("INSERT INTO assignments (class_id, category_id, name, description, points) VALUES (?,?,?,?,?)")) {
                p.setInt(1, currentClassId); p.setInt(2, catId); p.setString(3, name); p.setString(4, desc); p.setDouble(5, points);
                p.executeUpdate(); 
                System.out.println("Assignment added.");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // --- STUDENTS & GRADES ---

    /**
     * Lists students in the active class.
     * * @param search Optional string to filter students by name or username.
     */
    public void showStudents(String search) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        String sql = "SELECT s.username, s.student_id, s.first_name, s.last_name FROM students s JOIN enrollments e ON s.student_id = e.student_id WHERE e.class_id = ?";
        if (search != null) sql += " AND (s.first_name LIKE ? OR s.username LIKE ?)";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, currentClassId);
            if (search != null) { p.setString(2, "%"+search+"%"); p.setString(3, "%"+search+"%"); }
            ResultSet rs = p.executeQuery();
            System.out.println("\n--- Students ---");
            while (rs.next()) System.out.printf("%s (%s) - %s %s\n", rs.getString("username"), rs.getString("student_id"), rs.getString("first_name"), rs.getString("last_name"));
            System.out.println();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Adds a new student (or updates name if exists) and enrolls them.
     * * @param username The student's username.
     * @param uniId    The university ID.
     * @param last     Last name.
     * @param first    First name.
     */
    public void addStudentFull(String username, String uniId, String last, String first) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try {
            int dbStudentId = -1;
            try(PreparedStatement p = conn.prepareStatement("SELECT student_id, first_name, last_name FROM students WHERE username = ?")) {
                p.setString(1, username); ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    dbStudentId = rs.getInt("student_id");
                    if (!rs.getString("first_name").equalsIgnoreCase(first) || !rs.getString("last_name").equalsIgnoreCase(last)) {
                        System.out.println("Warning: Name mismatch. Updating student name.");
                        try(PreparedStatement pUp = conn.prepareStatement("UPDATE students SET first_name=?, last_name=? WHERE student_id=?")) {
                            pUp.setString(1, first); pUp.setString(2, last); pUp.setInt(3, dbStudentId); pUp.executeUpdate();
                        }
                    }
                } else {
                    try(PreparedStatement p2 = conn.prepareStatement("INSERT INTO students (username, university_id, first_name, last_name) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        p2.setString(1, username); p2.setString(2, uniId); p2.setString(3, first); p2.setString(4, last); p2.executeUpdate();
                        ResultSet keys = p2.getGeneratedKeys(); if(keys.next()) dbStudentId = keys.getInt(1);
                    }
                }
            }
            enrollStudent(dbStudentId);
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Enrolls an existing student into the active class.
     * * @param username The username of the existing student.
     */
    public void enrollExistingStudent(String username) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try {
            int dbStudentId = -1;
            try(PreparedStatement p = conn.prepareStatement("SELECT student_id FROM students WHERE username = ?")) {
                p.setString(1, username); ResultSet rs = p.executeQuery();
                if (rs.next()) dbStudentId = rs.getInt("student_id");
                else { System.out.println("Error: Student '" + username + "' does not exist."); return; }
            }
            enrollStudent(dbStudentId);
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Helper method to insert an enrollment record.
     * @param studentId The database ID of the student.
     */
    private void enrollStudent(int studentId) throws SQLException {
        try(PreparedStatement p = conn.prepareStatement("INSERT IGNORE INTO enrollments (class_id, student_id) VALUES (?, ?)")) {
            p.setInt(1, currentClassId); p.setInt(2, studentId);
            if(p.executeUpdate() > 0) System.out.println("Student enrolled in current class.");
            else System.out.println("Student was already enrolled.");
        }
    }

    /**
     * Assigns a grade to a student.
     * * @param assignName The name of the assignment.
     * @param username   The username of the student.
     * @param points     The points earned.
     */
    public void assignGrade(String assignName, String username, double points) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        try {
            int aId = -1, sId = -1; double max = 0;
            try(PreparedStatement p = conn.prepareStatement("SELECT a.assignment_id, s.student_id, a.points FROM assignments a JOIN students s ON s.username = ? WHERE a.class_id = ? AND a.name = ?")) {
                p.setString(1, username); p.setInt(2, currentClassId); p.setString(3, assignName);
                ResultSet rs = p.executeQuery();
                if(rs.next()) { aId=rs.getInt(1); sId=rs.getInt(2); max=rs.getDouble(3); } else { System.out.println("Assignment/Student not found."); return; }
            }
            if(points > max) System.out.printf("Warning: Points (%.2f) exceed max (%.2f).\n", points, max);
            try(PreparedStatement p = conn.prepareStatement("INSERT INTO grades (assignment_id, student_id, points_earned) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE points_earned = ?")) {
                p.setInt(1, aId); p.setInt(2, sId); p.setDouble(3, points); p.setDouble(4, points);
                p.executeUpdate(); 
                System.out.println("Grade assigned.");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Displays the gradebook with weighted averages calculated in SQL.
     */
    public void showGradebook() {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }
        
        /**
         * SQL Logic Explanation:
         * 1. cat_totals: Calculates the total possible points for each category.
         * 2. class_totals: Calculates the sum of all category weights to handle scaling (e.g., if sum is 80, scale to 100).
         * 3. The main query joins students, assignments, and grades.
         * 4. Calculation: (Points Earned / Total Category Points) * (Category Weight / Total Class Weight * 100).
         */
        String sql = "SELECT s.username, s.first_name, s.last_name, " +
                     "SUM( " +
                     "  (COALESCE(g.points_earned, 0) / cat_totals.total_points) * " +
                     "  (c.weight / class_totals.total_weight * 100) " +
                     ") as total_grade " +
                     "FROM students s " +
                     "JOIN enrollments e ON s.student_id = e.student_id " +
                     "JOIN assignments a ON a.class_id = e.class_id " +
                     "JOIN categories c ON a.category_id = c.category_id " +
                     "LEFT JOIN grades g ON g.assignment_id = a.assignment_id AND g.student_id = s.student_id " +
                     "JOIN (SELECT category_id, SUM(points) as total_points FROM assignments WHERE class_id = ? GROUP BY category_id) cat_totals ON a.category_id = cat_totals.category_id " +
                     "CROSS JOIN (SELECT SUM(weight) as total_weight FROM categories WHERE class_id = ?) class_totals " +
                     "WHERE e.class_id = ? GROUP BY s.student_id, s.username, s.first_name, s.last_name";

        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, currentClassId); // For cat_totals
            p.setInt(2, currentClassId); // For class_totals
            p.setInt(3, currentClassId); // For WHERE clause
            
            ResultSet rs = p.executeQuery();
            System.out.println("\n--- Gradebook (Total Grades Scaled to 100) ---");
            System.out.printf("%-15s %-25s %-15s\n", "Username", "Name", "Total Grade");
            while (rs.next()) {
                System.out.printf("%-15s %-25s %-15.2f\n", 
                    rs.getString("username"), 
                    rs.getString("first_name") + " " + rs.getString("last_name"), 
                    rs.getDouble("total_grade"));
            }
            System.out.println();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Shows detailed grades for a specific student.
     * * @param username The username of the student.
     */
    public void showStudentGrades(String username) {
        if (currentClassId == null) { System.out.println("Error: No class selected."); return; }

        // First, get the total weight of the class for scaling
        double totalClassWeight = 0;
        try (PreparedStatement p = conn.prepareStatement("SELECT SUM(weight) FROM categories WHERE class_id = ?")) {
            p.setInt(1, currentClassId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) totalClassWeight = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); return; }

        if (totalClassWeight == 0) {
            System.out.println("Error: Total class weight is 0.");
            return;
        }

        String sql = "SELECT c.name as cat_name, c.weight as cat_weight, a.name as assign_name, a.points as max_points, g.points_earned " +
                     "FROM assignments a " +
                     "JOIN categories c ON a.category_id = c.category_id " +
                     "JOIN students s ON s.username = ? " +
                     "LEFT JOIN grades g ON a.assignment_id = g.assignment_id AND g.student_id = s.student_id " +
                     "WHERE a.class_id = ? " +
                     "ORDER BY c.name, a.name";

        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, username); 
            p.setInt(2, currentClassId); 
            ResultSet rs = p.executeQuery();
            
            System.out.println("\n--- Grades for " + username + " ---");
            
            String currentCat = "";
            double catEarned = 0;
            double catMax = 0;
            double catWeight = 0;
            
            double finalTotalGrade = 0;
            double finalAttemptedGrade = 0;
            double totalWeightAttempted = 0;

            while (rs.next()) {
                String rowCat = rs.getString("cat_name");
                
                // If category changes, print subtotal for the previous category
                if (!rowCat.equals(currentCat)) {
                    if (!currentCat.isEmpty()) {
                        printCategorySubtotal(currentCat, catEarned, catMax, catWeight, totalClassWeight);
                        
                        // Add to final grades
                        double scaleFactor = (catWeight / totalClassWeight) * 100;
                        if (catMax > 0) {
                            finalTotalGrade += (catEarned / catMax) * scaleFactor;
                            if (catEarned > 0) { // Assuming if they earned points, they attempted it
                                finalAttemptedGrade += (catEarned / catMax) * scaleFactor;
                                totalWeightAttempted += scaleFactor;
                            }
                        }
                    }
                    // Reset for new category
                    currentCat = rowCat;
                    catEarned = 0;
                    catMax = 0;
                    catWeight = rs.getDouble("cat_weight");
                    System.out.println("[" + currentCat + " - Weight: " + catWeight + "]");
                }

                double maxP = rs.getDouble("max_points");
                double earnedP = 0;
                boolean isGraded = false;

                // Check if grade exists (it might be null)
                if (rs.getObject("points_earned") != null) {
                    earnedP = rs.getDouble("points_earned");
                    isGraded = true;
                }

                catMax += maxP;
                catEarned += earnedP;

                String gradeDisplay = !isGraded ? "--" : String.format("%.1f", earnedP);
                System.out.printf("  %-15s : %s / %.1f\n", rs.getString("assign_name"), gradeDisplay, maxP);
            }

            // Print the last category subtotal
            if (!currentCat.isEmpty()) {
                printCategorySubtotal(currentCat, catEarned, catMax, catWeight, totalClassWeight);
                double scaleFactor = (catWeight / totalClassWeight) * 100;
                if (catMax > 0) {
                    finalTotalGrade += (catEarned / catMax) * scaleFactor;
                     // Simple logic for attempted: if they have points in the category
                    if (catEarned > 0) {
                        finalAttemptedGrade += (catEarned / catMax) * scaleFactor;
                        totalWeightAttempted += scaleFactor;
                    }
                }
            }
            
            // Normalize Attempted Grade 
            if (totalWeightAttempted > 0) {
                finalAttemptedGrade = (finalAttemptedGrade / totalWeightAttempted) * 100;
            }

            System.out.println("-------------------------------------");
            System.out.printf("Total Grade (All assignments): %.2f / 100\n", finalTotalGrade);
            System.out.printf("Attempted Grade (Graded only): %.2f / 100\n", finalAttemptedGrade);
            System.out.println();

        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Helper method to print category subtotals.
     * Calculates the percentage and the weighted contribution.
     * * @param name Name of the category.
     * @param earned Total points earned in this category.
     * @param max Total possible points in this category.
     * @param weight Weight of the category.
     * @param totalClassWeight Total weight of all categories in the class.
     */
    private void printCategorySubtotal(String name, double earned, double max, double weight, double totalClassWeight) {
        double percentage = (max == 0) ? 0 : (earned / max);
        // Scale the weight: (Category Weight / Total Class Weight) * 100
        double effectiveWeight = (weight / totalClassWeight) * 100;
        double contribution = percentage * effectiveWeight;
        
        System.out.printf("  >> Subtotal for %s: %.1f / %.1f (%.1f%%)\n", name, earned, max, percentage * 100);
        System.out.printf("  >> Contribution to final grade: %.2f / %.2f\n", contribution, effectiveWeight);
    }

}
