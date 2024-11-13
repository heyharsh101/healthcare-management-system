import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_db", "root", "password");
            UserDAO userDAO = new UserDAO(connection);

            // Add a new user
            userDAO.addUser("Alice Doe", "alice@example.com", "password123", "Patient");

            // Get a user by ID
            User user = userDAO.getUserById(1);
            System.out.println(user);

            // Update a user
            userDAO.updateUser(1, "Alice Smith", "alice.smith@example.com", "newpassword123", "Patient");

            // Delete a user
            userDAO.deleteUser(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
