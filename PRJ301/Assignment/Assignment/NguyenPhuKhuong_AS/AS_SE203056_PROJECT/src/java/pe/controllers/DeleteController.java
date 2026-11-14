package pe.controllers;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pe.model.OrderDAO;
import pe.model.OrderDTO;

@WebServlet(name = "DeleteController", urlPatterns = {"/DeleteController"})
public class DeleteController extends HttpServlet {
    private static final String SEARCH_CONTROLLER = "SearchController";
    private static final String SEARCH_PAGE = "search.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String orderID = request.getParameter("orderID");
            String search = request.getParameter("search");
            if (orderID != null && !orderID.trim().isEmpty()) {
                OrderDAO dao = new OrderDAO();
                dao.delete(orderID.trim());
            }
            if (search != null && !search.trim().isEmpty()) {
                request.getRequestDispatcher(SEARCH_CONTROLLER).forward(request, response);
            } else {
                OrderDAO dao = new OrderDAO();
                List<OrderDTO> list = dao.getAllOrders();
                request.setAttribute("LIST_ORDERS", list);
                request.getRequestDispatcher(SEARCH_PAGE).forward(request, response);
            }
        } catch (Exception e) {
            log("Error at DeleteController: " + e.toString());
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
        return "Delete Controller";
    }
}
