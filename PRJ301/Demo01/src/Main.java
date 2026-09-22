import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Khuong
 */

public class Main {

    public static void main(String args[]) throws SQLException {
        String DB_URL = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=SaleMNG;";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, "sa", "12345");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from tblUsers");
            while (rs.next()) {
                System.out.println(rs.getString(1) + "  " + rs.getString(2)
                        + "  " + rs.getString(3));
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Error Connection");
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
}
