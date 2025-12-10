import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class handles the database connection.
 * It is a separate class to keep the code clean.
 */
public class DB {
    
    // The address of the database (localhost means this computer)
    private static final String URL = "jdbc:mysql://localhost:3306/gradebook";
    
    // The username for the MySQL database
    private static final String USER = "root";
    
    // The password for the MySQL database
    private static final String PASS = "1234"; 

    /**
     * This method establishes a connection to the database.
     * * @return A Connection object if successful.
     * @throws SQLException If the connection fails.
     */
    public static Connection connect() throws SQLException {
        // DriverManager tries to connect using the URL, user, and password
        return DriverManager.getConnection(URL, USER, PASS);
    }
}