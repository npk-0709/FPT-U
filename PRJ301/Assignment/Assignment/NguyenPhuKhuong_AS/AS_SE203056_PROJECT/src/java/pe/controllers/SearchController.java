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

@WebServlet(name = "SearchController", urlPatterns = {"/SearchController"})
public class SearchController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String search = request.getParameter("search");
            request.setAttribute("SEARCH_VALUE", search != null ? search : "");

            if (search != null && !search.trim().isEmpty()) {
                OrderDAO dao = new OrderDAO();
                List<OrderDTO> list = dao.searchByAddress(search.trim());
                if (list != null && !list.isEmpty()) {
                    request.setAttribute("LIST_ORDERS", list);
                } else {
                    request.setAttribute("MESSAGE", "No data matching the search criteria found !");
                }
            }
        } catch (Exception e) {
            log("Error at SearchController: " + e.toString());
        } finally {
            request.getRequestDispatcher("search.jsp").forward(request, response);
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
        return "Search Controller";
    }
}
