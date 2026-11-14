/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Utils.util;
import dto.InvoiceDTO;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class InvoiceDAO {

    public List<InvoiceDTO> getListInvoices(String userID) {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        InvoiceDTO invoice = null;
        SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
        String formatDate = "";
        String invID;
        String invDate;
        String customer;
        List<InvoiceDTO> invoices = new ArrayList<>();

        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblInvoices " + "where userId=?");
            p.setString(1, userID);
            rs = p.executeQuery();
            while (rs.next()) {
                invID = rs.getString("invID");
                invDate = rs.getString("invDate");
                customer = rs.getString("customer");
                userID = rs.getString("userID");
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                String outputDate = "";
                try {
                    Date date = inputFormat.parse(invDate);
                    outputDate = outputFormat.format(date);
                } catch (Exception e) {
                    System.out.println("Invalid Date");
                }
                invoice = new InvoiceDTO(invID, outputDate, customer, userID);
                invoices.add(invoice);

            }

            if (rs != null) {
                rs.close();
            }
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error access database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database");
        }

        return invoices;
    }

    public List<InvoiceDTO> getListInvoicesSearch(String userID, String txtCustomer) {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        InvoiceDTO invoice = null;
        String invID;
        String invDate;
        String customer;
        List<InvoiceDTO> invoices = new ArrayList<>();

        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblInvoices where userId=? and customer like ?");
            p.setString(1, userID);
            p.setString(2, '%' + txtCustomer + '%');
            rs = p.executeQuery();
            while (rs.next()) {
                invID = rs.getString("invID");
                invDate = rs.getString("invDate");
                customer = rs.getString("customer");
                userID = rs.getString("userID");
                invoice = new InvoiceDTO(invID, invDate, customer, userID);
                invoices.add(invoice);

            }
            if (rs != null) {
                rs.close();
            }
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error access database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database");
        }

        return invoices;
    }

    public int addNewInvoice(InvoiceDTO invoice) {
        Connection conn = null;
        PreparedStatement p = null;
        int result = -1;
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("Insert tblInvoices values(?,?,?,?)");
            p.setString(1, invoice.getInvID());
            System.out.println(invoice.getInvID());
            p.setString(2, invoice.getInvDate());
            System.out.println(invoice.getInvDate());
            p.setString(3, invoice.getCustomer());
            System.out.println(invoice.getCustomer());
            p.setString(4, invoice.getUserID());
            System.out.println(invoice.getUserID());
            result = p.executeUpdate();
            System.out.println("Connected");
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database class not found");
        }

        return result;
    }

    public int updateInvoice(InvoiceDTO invoice) {
        Connection conn = null;
        PreparedStatement p = null;
        int result = -1;
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("update tblInvoices set invDate=?, customer=?, invID=? where userID=? and invID=?");
            p.setString(1, invoice.getInvDate());
            p.setString(2, invoice.getCustomer());
            p.setString(3, invoice.getInvID());
            p.setString(4, invoice.getUserID());
            p.setString(5, invoice.getInvID());
            result = p.executeUpdate();
            System.out.println("Connected");

        } catch (SQLException e) {
            System.out.println("Error access database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database class not found");
        }
        return result;
    }

    public int deleteInvoice(String invID) {
        Connection conn = null;
        PreparedStatement p = null;
        int result = -1;
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("delete from tblInvoices where invID=?");
//            p.setString(1, invoice.getUserID());
            p.setString(1, invID);
            result = p.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error access database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database class not found");
        }
        return result;
    }

    public boolean checkDuplicate(String invID) {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        InvoiceDTO invoice = null;
        boolean result = false;
        try {
            conn = util.getConnection();
            p = conn.prepareStatement("select * from tblInvoices where userId=?");
            p.setString(1, invID);
            rs = p.executeQuery();
            if(rs.next()){
                result = true;
            }
            if (rs != null) {
                rs.close();
            }
            if (p != null) {
                p.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error access database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error access database");
        }

        return result;
    }
}
