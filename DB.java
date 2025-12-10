import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class handles the database connection.
 * It is a separate class to keep the code clean.
 */
public class DB {
    
    // Port from deploydb.sh output
    private static final String PORT = "57737";

    // Database name created on Onyx
    private static final String DB_NAME = "gradebook";

    // JDBC URL for the Onyx sandbox.
    // useSSL/verifyServerCertificate settings according to the given guide in the instructions
    private static final String URL = "jdbc:mysql://localhost:" + PORT + "/" + DB_NAME + "?useSSL=true&verifyServerCertificate=false";

    // Sandbox username
    private static final String USER = "msandbox";

    // Sandbox password chosen when running deploydb.sh
    private static final String PASS = "password";

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