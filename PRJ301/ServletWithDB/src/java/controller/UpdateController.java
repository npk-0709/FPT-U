/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Khuong
 */
@WebServlet(name = "UpdateController", urlPatterns = {"/UpdateController"})
public class UpdateController extends HttpServlet {

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
        String actionSubmit = request.getParameter("actionSubmit");
        
        if (actionSubmit == null || !actionSubmit.equals("Update")) {
            // Load the invoice for editing
            String invId = request.getParameter("invID");
            dao.InvoiceDAO dao = new dao.InvoiceDAO();
            try {
                dto.InvoiceDTO inv = dao.getInvoiceById(invId);
                request.setAttribute("INVOICE", inv);
                request.getRequestDispatcher("updateInvoice.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Process the update
            String invId = request.getParameter("invId");
            String invDate = request.getParameter("invDate");
            String customer = request.getParameter("customer");
            javax.servlet.http.HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("USER_ID");
            
            dto.InvoiceDTO inv = new dto.InvoiceDTO(invId, customer, userId, invDate);
            dao.InvoiceDAO dao = new dao.InvoiceDAO();
            try {
                dao.updateInvoice(inv);
                String txtSearch = (String) session.getAttribute("TXT_SEARCH");
                if (txtSearch == null) txtSearch = "";
                response.sendRedirect("Maincontroller?action=search&txtsearch=" + java.net.URLEncoder.encode(txtSearch, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
