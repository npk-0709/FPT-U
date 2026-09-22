package dao;

import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.util;

/**
 *
 * @author Khuong
 */
public class UserDAO {

    public UserDTO checklogin(String userId, String password) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        UserDTO user = null;
        String userID;
        String fullName;
        String pass;
        String roleID;
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblUsers where userId=? and password=?");
            p.setString(1, userId);
            p.setString(2, password);
            rs = p.executeQuery();
            if (rs.next()) {
                userID = rs.getString("UserID");
                fullName = rs.getString("fullName");
                pass = rs.getString("password");
                roleID = rs.getString("roleID");
                user = new UserDTO(userID, fullName, pass, roleID);

            }
        } catch (SQLException e) {
            System.out.println("Error access to DB");

        } catch (Exception e) {
            System.out.println("Error not defind !");
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return user;
    }
}
