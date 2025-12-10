import java.util.Scanner;

/**
 * The Main class serves as the entry point for the Gradebook application.
 * It runs the command-line interface (shell), reads user input,
 * and directs commands to the GradebookSystem.
 */
public class Main {

    /**
     * The main method that starts the application.
     * It establishes the database connection and enters a loop to process user commands.
     * * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        System.out.println("Connecting to database...");
        
        // Initialize the system logic
        GradebookSystem system = new GradebookSystem();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Gradebook Shell!");
        System.out.println("Type 'help' for commands or 'quit' to exit.");

        boolean running = true;
        
        // The main application loop
        while (running) {
            // Display the current prompt (e.g., "CS410 > ")
            System.out.print(system.getPrompt());
            
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            // Split input into command and arguments
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "quit": 
                    case "exit":
                        running = false; 
                        system.close(); 
                        System.out.println("Goodbye!"); 
                        break;

                    case "help":
                        printHelp(); 
                        break;
                    
                    // --- Class Management Commands ---
                    case "list-classes":
                        system.listClasses(); 
                        break;

                    case "select-class":
                        if (parts.length < 2) {
                            System.out.println("Usage: select-class <Course> [Term] [Sec]");
                        } else {
                            // Pass optional arguments (Term and Section) if they exist
                            system.selectClass(parts[1], (parts.length > 2 ? parts[2] : null), (parts.length > 3 ? parts[3] : null));
                        }
                        break;

                    case "new-class":
                        if (parts.length < 5) {
                            System.out.println("Usage: new-class <Num> <Term> <Sec> <Desc>");
                        } else {
                            // Reconstruct the description string (which might contain spaces)
                            StringBuilder desc = new StringBuilder();
                            for(int i=4; i<parts.length; i++) desc.append(parts[i]).append(" ");
                            system.createClass(parts[1], parts[2], Integer.parseInt(parts[3]), desc.toString().trim().replace("\"", ""));
                        }
                        break;

                    case "show-class": 
                        system.showActiveClass(); 
                        break;

                    // --- Category & Assignment Commands ---
                    case "show-categories": 
                        system.showCategories(); 
                        break;

                    case "add-category":
                        if (parts.length < 3) {
                            System.out.println("Usage: add-category <Name> <Weight>");
                        } else {
                            system.addCategory(parts[1], Double.parseDouble(parts[2]));
                        }
                        break;

                    case "show-assignment": 
                        system.showAssignments(); 
                        break;

                    case "add-assignment":
                        if (parts.length < 5) {
                            System.out.println("Usage: add-assignment <Name> <Cat> <Desc> <Points>");
                        } else {
                            system.addAssignment(parts[1], parts[2], parts[3].replace("\"", ""), Double.parseDouble(parts[4]));
                        }
                        break;

                    // --- Student & Grade Commands ---
                    case "show-students":
                        system.showStudents(parts.length > 1 ? parts[1] : null); 
                        break;

                    case "add-student":
                        // Logic to handle both short (enroll existing) and long (add new) commands [cite: 45-50]
                        if (parts.length == 2) {
                            // Short version: add-student username
                            system.enrollExistingStudent(parts[1]);
                        } else if (parts.length >= 5) {
                            // Long version: add-student user ID Last First
                            system.addStudentFull(parts[1], parts[2], parts[3], parts[4]);
                        } else {
                            System.out.println("Usage: add-student <User> OR add-student <User> <ID> <Last> <First>");
                        }
                        break;

                    case "grade":
                        if (parts.length < 4) {
                            System.out.println("Usage: grade <Assign> <User> <Points>");
                        } else {
                            system.assignGrade(parts[1], parts[2], Double.parseDouble(parts[3]));
                        }
                        break;

                    case "gradebook": 
                        system.showGradebook(); 
                        break;

                    case "student-grades":
                        if (parts.length < 2) {
                            System.out.println("Usage: student-grades <User>");
                        } else {
                            system.showStudentGrades(parts[1]);
                        }
                        break;

                    default: 
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) { 
                System.out.println("Error processing command: " + e.getMessage()); 
            }
        }
        scanner.close();
    }

    /**
     * Prints a list of all available commands to the console.
     * Used to guide the user on how to interact with the system.
     */
    private static void printHelp() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("  list-classes, select-class, new-class");
        System.out.println("  show-categories, add-category");
        System.out.println("  show-assignment, add-assignment");
        System.out.println("  show-students [search]");
        System.out.println("  add-student <User> (Enroll existing)");
        System.out.println("  add-student <User> <ID> <Last> <First> (Add new)");
        System.out.println("  grade, student-grades, gradebook");
        System.out.println("  quit");
    }
}