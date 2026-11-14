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
 * SearchController
 *
 * @author Khuong
 */
@WebServlet(name = "SearchController", urlPatterns = {"/SearchController", "/searchController", "/listController"})
public class SearchController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER_ID") == null) {
                response.sendRedirect("Login.jsp");
                return;
            }
            String userID = (String) session.getAttribute("USER_ID");
            String txtsearch = request.getParameter("txtsearch");
            try {
                HomestayDAO homestayDAO = new HomestayDAO();
                List<HomestayDTO> homestayList;
                if (txtsearch == null || txtsearch.trim().isEmpty()) {
                    homestayList = homestayDAO.getHomestayByUserID(userID);
                    txtsearch = "";
                } else {
                    homestayList = homestayDAO.getHomestayByAddress(userID, txtsearch.trim());
                }
                session.setAttribute("HOMESTAY_LIST", homestayList);
                session.setAttribute("TXT_SEARCH", txtsearch);
                response.sendRedirect("Homestay.jsp");
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { processRequest(request, response); }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {processRequest(request, response); }
    @Override
    public String getServletInfo() { return "Search / List Controller"; }
}
