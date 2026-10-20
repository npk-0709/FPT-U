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
import javax.servlet.http.HttpSession;

/**
 *
 * @author Khuong
 */
public class Maincontroller extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            String action = request.getParameter("action");
            if (action.equals("login")) {
                RequestDispatcher rd = request.getRequestDispatcher("/Logincontroller");
                rd.forward(request, response);
            }
            if (action.equals("logout")) {
                RequestDispatcher rd = request.getRequestDispatcher("/LogoutController");
                rd.forward(request, response);

            }
            if (action.equals("search")) {
                RequestDispatcher rd = request.getRequestDispatcher("/Searchcontroller");
                rd.forward(request, response);
            }
            if (action.equals("update")) {
                RequestDispatcher rd = request.getRequestDispatcher("/UpdateController");
                rd.forward(request, response);
            }
            if (action.equals("delete")) {
                RequestDispatcher rd = request.getRequestDispatcher("/DeleteController");
                rd.forward(request, response);
            }
            if (action.equals("add")) {
                RequestDispatcher rd = request.getRequestDispatcher("/AddController");
                rd.forward(request, response);
            }

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
