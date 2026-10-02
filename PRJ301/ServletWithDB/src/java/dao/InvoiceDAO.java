/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.InvoiceDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.util;

/**
 *
 * @author Khuong
 */
public class InvoiceDAO {

    public List<InvoiceDTO> getInvoice(String userId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        InvoiceDTO Invoice = null;
        String invID;
        String invDate;
        String customer;
        List<InvoiceDTO> invoiceList = new ArrayList<>();
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblInvoices where userID=?");
            p.setString(1, userId);
            rs = p.executeQuery();
            while (rs.next()) {
                invID = rs.getString("invID");
                invDate = rs.getString("invDate");
                customer = rs.getString("customer");
                InvoiceDTO invoice = new InvoiceDTO(invID, invDate, userId, customer);
                invoiceList.add(invoice);
            }
        } catch (SQLException e) {
            System.out.println("Error access to tblInvoices" + e);

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
        return invoiceList;
    }

    public List<InvoiceDTO> getInvoiceByCustomer(String userId, String customer_input) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        InvoiceDTO Invoice = null;
        String invID;
        String invDate;
        String customer;
        List<InvoiceDTO> invoiceList = new ArrayList<>();
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblInvoices where userID=? and customer like ?");
            p.setString(1, userId);
            p.setString(2, '%'+customer_input+'%');
            rs = p.executeQuery();
            while (rs.next()) {
                invID = rs.getString("invID");
                invDate = rs.getString("invDate");
                customer = rs.getString("customer");
                InvoiceDTO invoice = new InvoiceDTO(invID, invDate, userId, customer);
                invoiceList.add(invoice);
            }
        } catch (SQLException e) {
            System.out.println("Error access to tblInvoices" + e);

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
        return invoiceList;
    }

}
