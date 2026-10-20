package controller;

import dao.HomestayDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * deleteController
 * @author Khuong
 */
@WebServlet(name = "deleteController", urlPatterns = {"/deleteController"})
public class deleteController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);

            String homeID = request.getParameter("homeID");
            if (homeID != null) {
                try {
                    HomestayDAO homestayDAO = new HomestayDAO();
                    homestayDAO.deleteHomestay(homeID);
                } catch (SQLException | ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            }
            String txtSearch = (String) session.getAttribute("TXT_SEARCH");
            if (txtSearch == null) {
                txtSearch = "";
            }
            response.sendRedirect("mainController?action=search&txtsearch=" + URLEncoder.encode(txtSearch, "UTF-8"));
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
