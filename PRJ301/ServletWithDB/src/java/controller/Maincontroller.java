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
            } else if (action.equals("logout")) {
                HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới nếu không tồn tại
                if (session != null) {
                    session.invalidate(); // Hủy toàn bộ session và xóa mọi thuộc tính bên trong
                    response.sendRedirect("/ServletWithDB");
                }

            } else if (action.equals("search")) {
                RequestDispatcher rd = request.getRequestDispatcher("/Searchcontroller");
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
