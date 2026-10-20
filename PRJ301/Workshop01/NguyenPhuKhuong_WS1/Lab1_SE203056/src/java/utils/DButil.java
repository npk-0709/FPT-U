
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ThongNT2025
 */
public class DButil {
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        
        String DB_URL = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=HomestayMNG;";
        Connection conn = null;
            // connnect to database
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, "sa", "12345");
        return conn;
    }
    
}
