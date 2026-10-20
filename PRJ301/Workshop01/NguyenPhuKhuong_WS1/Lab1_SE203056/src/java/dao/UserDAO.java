package dao;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DButil;
/**
 * UserDAO
 * @author Khuong
 */
public class UserDAO {

    public UserDTO checkLogin(String userID, String password) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        UserDTO user = null;
        try {
            conn = DButil.getConnection();
            p = conn.prepareStatement("SELECT * FROM tblUsers WHERE userID=? AND password=?");
            p.setString(1, userID);
            p.setString(2, password);
            rs = p.executeQuery();
            if (rs.next()) {
                String uid = rs.getString("userID");
                String fullName = rs.getString("fullname");
                String roleID = rs.getString("roleID");
                String pass = rs.getString("password");
                user = new UserDTO(uid, fullName, roleID, pass);
            }
        } catch (SQLException e) {
            System.out.println("Error accessing tblUsers: " + e);
        } catch (Exception e) {
            System.out.println("Error not defined!");
        } finally {
            if (rs != null) rs.close();
            if (p != null) p.close();
            if (conn != null) conn.close();
        }
        return user;
    }
}
