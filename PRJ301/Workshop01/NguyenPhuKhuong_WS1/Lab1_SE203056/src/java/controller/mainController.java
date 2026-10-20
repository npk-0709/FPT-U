package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * mainController
 *
 * @author Khuong
 */
public class mainController extends HttpServlet {

    public void redirectTo(HttpServletRequest request, HttpServletResponse response, String controller) throws ServletException, IOException {
        request.getRequestDispatcher(controller).forward(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "login":
                redirectTo(request, response, "loginController");
                break;
            case "logout":
                redirectTo(request, response, "logoutController");
                break;
            case "search":
                redirectTo(request, response, "searchController");
                break;
            case "delete":
                redirectTo(request, response, "deleteController");
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
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
