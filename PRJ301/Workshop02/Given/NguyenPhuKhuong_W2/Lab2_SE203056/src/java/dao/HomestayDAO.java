package dao;
import dto.HomestayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtil;
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
            conn = DBUtil.getConnection();
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
            if (rs != null) {rs.close();}
            if (p != null) { p.close(); }
            if (conn != null) {conn.close();}
        }
        return homestayList;
    }

    public List<HomestayDTO> getHomestayByUserID(String userID) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        List<HomestayDTO> homestayList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            p = conn.prepareStatement("SELECT * FROM tblHomestays WHERE userID=?");
            p.setString(1, userID);
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


    public boolean existsHomestay(String homeID) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            conn = DBUtil.getConnection();
            p = conn.prepareStatement("SELECT 1 FROM tblHomestays WHERE homeID=?");
            p.setString(1, homeID);
            rs = p.executeQuery();
            exists = rs.next();
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
        return exists;
    }

 
    public HomestayDTO getHomestayByID(String homeID) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        HomestayDTO homestay = null;
        try {
            conn = DBUtil.getConnection();
            p = conn.prepareStatement("SELECT * FROM tblHomestays WHERE homeID=?");
            p.setString(1, homeID);
            rs = p.executeQuery();
            if (rs.next()) {
                String description = rs.getString("description");
                int price = rs.getInt("price");
                String address = rs.getString("address");
                String uid = rs.getString("userID");
                homestay = new HomestayDTO(homeID, description, price, address, uid);
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
        return homestay;
    }


    public boolean addHomestay(HomestayDTO homestay) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        boolean result = false;
        try {
            conn = DBUtil.getConnection();
            p = conn.prepareStatement("INSERT INTO tblHomestays (homeID, description, price, address, userID) VALUES (?, ?, ?, ?, ?)");
            p.setString(1, homestay.getHomeID());
            p.setString(2, homestay.getDescription());
            p.setInt(3, homestay.getPrice());
            p.setString(4, homestay.getAddress());
            p.setString(5, homestay.getUserID());
            result = p.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error access to DB: " + e);
        } catch (Exception e) {
            System.out.println("Error !");
        } finally {
            if (p != null) { p.close(); }
            if (conn != null) { conn.close();}
        }
        return result;
    }

   
    public boolean updateHomestay(HomestayDTO homestay) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        boolean result = false;
        try {
            conn = DBUtil.getConnection();
            p = conn.prepareStatement("UPDATE tblHomestays SET description=?, price=?, address=?, userID=? WHERE homeID=?");
            p.setString(1, homestay.getDescription());
            p.setInt(2, homestay.getPrice());
            p.setString(3, homestay.getAddress());
            p.setString(4, homestay.getUserID());
            p.setString(5, homestay.getHomeID());
            result = p.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error access to DB: " + e);
        } catch (Exception e) {
            System.out.println("Error !");
        } finally {
            if (p != null) { p.close(); }
            if (conn != null) { conn.close();}
        }
        return result;
    }


    public boolean deleteHomestay(String homeID) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        boolean result = false;
        try {
            conn = DBUtil.getConnection();
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
