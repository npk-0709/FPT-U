/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class util {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        String DB_URL = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=SaleMNG;";
        Connection conn = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(DB_URL, "sa", "12345");
        
        return conn;
    }
}
