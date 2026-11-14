package pe.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.utils.DbUtils;

public class OrderDAO {

    public List<OrderDTO> getAllOrders() throws ClassNotFoundException, SQLException {
        List<OrderDTO> list = new ArrayList<OrderDTO>();
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DbUtils.getConnection();
            if (conn != null) {
                String query = "SELECT orderID, orderDate, customer, address, totalAmount, status FROM tblOrders";
                ptm = conn.prepareStatement(query);
                rs = ptm.executeQuery();
                while (rs.next()) {
                    String orderID = rs.getString("orderID") != null ? rs.getString("orderID").trim() : "";
                    Date orderDate = rs.getDate("orderDate");
                    String customer = rs.getString("customer");
                    String addr = rs.getString("address");
                    double totalAmount = rs.getDouble("totalAmount");
                    String status = rs.getString("status");
                    list.add(new OrderDTO(orderID, orderDate, customer, addr, totalAmount, status));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public List<OrderDTO> searchByAddress(String address) throws ClassNotFoundException, SQLException {
        List<OrderDTO> list = new ArrayList<OrderDTO>();
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DbUtils.getConnection();
            if (conn != null) {
                String query = "SELECT orderID, orderDate, customer, address, totalAmount, status FROM tblOrders WHERE address LIKE ?";
                ptm = conn.prepareStatement(query);
                ptm.setString(1, "%" + address + "%");
                rs = ptm.executeQuery();
                while (rs.next()) { 
                    String orderID = rs.getString("orderID") != null ? rs.getString("orderID").trim() : "";
                    Date orderDate = rs.getDate("orderDate");
                    String customer = rs.getString("customer");
                    String addr = rs.getString("address");
                    double totalAmount = rs.getDouble("totalAmount");
                    String status = rs.getString("status");
                    list.add(new OrderDTO(orderID, orderDate, customer, addr, totalAmount, status));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public boolean delete(String orderID) throws ClassNotFoundException, SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DbUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement("DELETE FROM tblOrders WHERE orderID = ?");
                ptm.setString(1, orderID);
                check = ptm.executeUpdate() > 0;
            }
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
}
