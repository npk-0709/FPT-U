package pe.controllers;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.OrderDAO;
import pe.model.OrderDTO;
import pe.model.UserDAO;
import pe.model.UserDTO;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {
    private static final String ERROR_PAGE = "login.jsp";
    private static final String SUCCESS_PAGE = "search.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR_PAGE;
        try {
            String userID = request.getParameter("userID");
            String password = request.getParameter("password");
            if (userID == null || userID.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                request.setAttribute("ERROR", "Invalid user or password");
                url = ERROR_PAGE;
            } else {
                UserDAO dao = new UserDAO();
                UserDTO user = dao.checkLogin(userID.trim(), password.trim());
                if (user != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("LOGIN_USER", user);
                    OrderDAO orderDao = new OrderDAO();
                    List<OrderDTO> listOrders = orderDao.getAllOrders();
                    request.setAttribute("LIST_ORDERS", listOrders);
                    
                    url = SUCCESS_PAGE;
                } else {
                    request.setAttribute("ERROR", "Invalid user or password");
                    url = ERROR_PAGE;
                }
            }
        } catch (Exception e) {
            log("Error at LoginController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
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
        return "Login Controller";
    }
}
