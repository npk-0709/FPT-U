package dao;

import dto.HomestayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DButil;

/**
 * HomestayDAO
 *
 * @author Khuong
 */
public class HomestayDAO {

    public List<HomestayDTO> getHomestayByAddress(String userID, String addressKeyword) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        List<HomestayDTO> homestayList = new ArrayList<>();
        try {
            conn = DButil.getConnection();
            p = conn.prepareStatement("SELECT * FROM tblHomestays WHERE userID=? AND address LIKE ?");
            p.setString(1, userID);
            p.setString(2, "%" + addressKeyword + "%");
            rs = p.executeQuery();
            while (rs.next()) {
                String homeID = rs.getString("homeID");
                String description = rs.getString("description");
                int price = rs.getInt("price");
                String address = rs.getString("address");
                String uid = rs.getString("userID");
                HomestayDTO homestay = new HomestayDTO(homeID, description, price, address, uid);
                homestayList.add(homestay);
            }
        } catch (SQLException e) {
            System.out.println("Error access to DB: " + e);
        } catch (Exception e) {
            System.out.println("Error !");
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
        return homestayList;
    }

    /**
     * HomestayDAO
     *
     * @author Khuong
     */
    public boolean deleteHomestay(String homeID) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        boolean result = false;
        try {
            conn = DButil.getConnection();
            p = conn.prepareStatement("DELETE FROM tblHomestays WHERE homeID=?");
            p.setString(1, homeID);
            result = p.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error access to DB: " + e);
        } catch (Exception e) {
            System.out.println("Error !");
        } finally {
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }
}
