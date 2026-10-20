/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.InvoiceDAO;
import dto.InvoiceDTO;
import dto.InvoiceError;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Khuong
 */
@WebServlet(name = "AddController", urlPatterns = {"/AddController"})
public class AddController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("USER_ID") : null;
        if (userId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String invId = request.getParameter("invId");
        String invDate = request.getParameter("invDate");
        String customer = request.getParameter("customer");

        InvoiceError error = new InvoiceError();
        boolean hasError = false;

        // 1. Kiểm tra mã hóa đơn (Invoice ID)
        if (invId == null || invId.trim().isEmpty()) {
            error.setInvIDError("Mã hóa đơn không được để trống!");
            hasError = true;
        } else {
            invId = invId.trim();
            if (invId.length() < 2 || invId.length() > 20) {
                error.setInvIDError("Mã hóa đơn phải từ 2 đến 20 ký tự!");
                hasError = true;
            } else {
                try {
                    InvoiceDAO dao = new InvoiceDAO();
                    if (dao.getInvoiceById(invId) != null) {
                        error.setInvIDError("Mã hóa đơn đã tồn tại! Vui lòng nhập mã khác.");
                        hasError = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error.setMessageError("Lỗi hệ thống khi kiểm tra mã hóa đơn!");
                    hasError = true;
                }
            }
        }

        // 2. Kiểm tra ngày lập hóa đơn (Invoice Date)
        if (invDate == null || invDate.trim().isEmpty()) {
            error.setInvDateError("Ngày lập hóa đơn không được để trống!");
            hasError = true;
        } else {
            invDate = invDate.trim();
            if (!invDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                error.setInvDateError("Ngày lập hóa đơn phải đúng định dạng yyyy-mm-dd (ví dụ: 2026-10-16)!");
                hasError = true;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                try {
                    sdf.parse(invDate);
                } catch (ParseException e) {
                    error.setInvDateError("Ngày hóa đơn không hợp lệ trên lịch!");
                    hasError = true;
                }
            }
        }

        // 3. Kiểm tra tên khách hàng (Customer)
        if (customer == null || customer.trim().isEmpty()) {
            error.setCustomerError("Tên khách hàng không được để trống!");
            hasError = true;
        } else {
            customer = customer.trim();
            if (customer.length() < 2 || customer.length() > 50) {
                error.setCustomerError("Tên khách hàng phải từ 2 đến 50 ký tự!");
                hasError = true;
            }
        }

        InvoiceDTO inv = new InvoiceDTO(invId != null ? invId : "", customer != null ? customer : "", userId, invDate != null ? invDate : "");

        if (hasError) {
            request.setAttribute("INVOICE", inv);
            request.setAttribute("INVOICE_ERROR", error);
            request.setAttribute("ERROR", error);
            request.getRequestDispatcher("addInvoice.jsp").forward(request, response);
            return;
        }

        // Thực hiện lưu hóa đơn mới vào cơ sở dữ liệu
        try {
            InvoiceDAO dao = new InvoiceDAO();
            boolean check = dao.addInvoice(inv);
            if (check) {
                response.sendRedirect("Maincontroller?action=search&txtsearch=");
            } else {
                error.setMessageError("Thêm hóa đơn thất bại! Vui lòng thử lại.");
                request.setAttribute("INVOICE", inv);
                request.setAttribute("INVOICE_ERROR", error);
                request.setAttribute("ERROR", error);
                request.getRequestDispatcher("addInvoice.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error.setMessageError("Lỗi hệ thống: " + e.getMessage());
            request.setAttribute("INVOICE", inv);
            request.setAttribute("INVOICE_ERROR", error);
            request.setAttribute("ERROR", error);
            request.getRequestDispatcher("addInvoice.jsp").forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
