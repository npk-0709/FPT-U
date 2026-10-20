package controller;

import dao.HomestayDAO;
import dto.HomestayDTO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * searchController
 * @author Khuong
 */
@WebServlet(name = "searchController", urlPatterns = {"/searchController"})
public class searchController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            String txtsearch = request.getParameter("txtsearch");
            if (txtsearch == null) {
                txtsearch = "";
            }
            String userID = (String) session.getAttribute("USER_ID");
            try {
                HomestayDAO homestayDAO = new HomestayDAO();
                List<HomestayDTO> homestayList = homestayDAO.getHomestayByAddress(userID, txtsearch);
                session.setAttribute("HOMESTAY_LIST", homestayList);
                session.setAttribute("TXT_SEARCH", txtsearch);
                response.sendRedirect("homestay.jsp");
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println(ex);
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
