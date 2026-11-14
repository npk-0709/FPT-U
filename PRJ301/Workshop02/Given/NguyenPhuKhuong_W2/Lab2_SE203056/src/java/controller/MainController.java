package controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * MainController
 *
 * @author Khuong
 */
public class MainController extends HttpServlet {
    public void redirectTo(HttpServletRequest request, HttpServletResponse response, String controller) throws ServletException, IOException {
            request.getRequestDispatcher(controller).forward(request, response);}
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "login":
                redirectTo(request, response, "LoginController");
                break;
            case "logout":
                redirectTo(request, response, "LogoutController");
                break;
            case "list":
                redirectTo(request, response, "SearchController");
                break;
            case "search":
                redirectTo(request, response, "SearchController");
                break;
            case "add":
                redirectTo(request, response, "AddController");
                break;
            case "update":
                redirectTo(request, response, "UpdateController");
                break;
            case "delete":
                redirectTo(request, response, "DeleteController");
                break;
            default:
                response.sendRedirect("Login.jsp");
                break;
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {processRequest(request, response); }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException { processRequest(request, response);  }
    @Override
    public String getServletInfo() { return "Main Controller";}
}
