/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Vo Binh Thanh Hau - SE203185
 */
public class Maincontroller extends HttpServlet {

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
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            String action = request.getParameter("action");
            String checkSave = request.getParameter("checkSave");
            if ("Login".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("loginController");
                rd.forward(request, response);
                
            } else if ("Search".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("searchNameController");
                rd.forward(request, response);
            } else if ("Addnew".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("addNewInvoice.jsp");
                rd.forward(request, response);
            } else if ("Save".equals(action)) {
                if ("saveUpdate".equals(checkSave)) {
                    RequestDispatcher rd = request.getRequestDispatcher("SaveUpdateController");
                    rd.forward(request, response);
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("SaveAddNewController");
                    rd.forward(request, response);
                }
            } else if ("Update".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("UpdateController");
                rd.forward(request, response);
            } else if ("Delete".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("DeleteController");
                rd.forward(request, response);
            } else if ("Logout".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("LogoutController");
                rd.forward(request, response);
            } else if ("cancel".equals(action)) {
                RequestDispatcher rd = request.getRequestDispatcher("searchNameController");
                rd.forward(request, response);
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
