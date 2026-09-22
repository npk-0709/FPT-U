import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        // Update these credentials for your local or remote SQL Server
        String server = "localhost";
        String port = "1433";
        String database = "master";
        String user = "sa";
        String password = "khuong07092005";

        // Construct the JDBC connection URL
        String connectionUrl = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=true;",
                server, port, database, user, password
        );

        System.out.println("Attempting to connect to SQL Server...");

        // Use try-with-resources to automatically close connections
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            System.out.println("Connection successful!\n");

            // Execute a simple query
            String sql = "SELECT @@VERSION AS ServerVersion";
            ResultSet resultSet = statement.executeQuery(sql);

            // Process the results
            while (resultSet.next()) {
                System.out.println("--- Database Version ---");
                System.out.println(resultSet.getString("ServerVersion"));
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception occurred!");
            e.printStackTrace();
        }
    }
}