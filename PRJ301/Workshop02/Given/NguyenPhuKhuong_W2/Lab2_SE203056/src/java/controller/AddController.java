package controller;

import dao.HomestayDAO;
import dto.HomestayDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AddController
 *
 * @author Khuong
 */
@WebServlet(name = "AddController", urlPatterns = {"/AddController", "/addController"})
public class AddController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            request.getRequestDispatcher("Add.jsp").forward(request, response);

        } else if ("POST".equalsIgnoreCase(method)) {
            String homeID = request.getParameter("homeID");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String address = request.getParameter("address");
            String userID = (String) session.getAttribute("USER_ID");
            List<String> errors = new ArrayList<>();
            HomestayDAO homestayDAO = new HomestayDAO();
            if (homeID == null || homeID.trim().isEmpty()) {
                errors.add("Home ID cannot be empty.");
            } else {
                try {
                    if (homestayDAO.existsHomestay(homeID.trim())) {
                        errors.add("Home ID '" + homeID.trim() + "' already exists, please choose another ID.");
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    System.out.println(ex);
                    errors.add("Error checking duplicate Home ID.");
                }
            }
            if (description == null || description.trim().isEmpty()) {
                errors.add("Description cannot be empty.");
            }
            int price = 0;
            if (priceStr == null || priceStr.trim().isEmpty()) {
                errors.add("Price cannot be empty.");
            } else {
                try {
                    price = Integer.parseInt(priceStr.trim());
                    if (price < 0) {
                        errors.add("Price cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Price must be a valid integer.");
                }
            }
            if (address == null || address.trim().isEmpty()) {
                errors.add("Address cannot be empty.");
            }
            if (!errors.isEmpty()) {
                request.setAttribute("ERRORS", errors);
                request.setAttribute("homeID", homeID);
                request.setAttribute("description", description);
                request.setAttribute("price", priceStr);
                request.setAttribute("address", address);
                request.getRequestDispatcher("Add.jsp").forward(request, response);
                return;
            }
            try {
                HomestayDTO newHomestay = new HomestayDTO(
                        homeID.trim(),
                        description.trim(),
                        price,
                        address.trim(),
                        userID
                );
                homestayDAO.addHomestay(newHomestay);

                response.sendRedirect("MainController?action=list");
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println(ex);
                errors.add("Database connection error while adding homestay.");
                request.setAttribute("ERRORS", errors);
                request.setAttribute("homeID", homeID);
                request.setAttribute("description", description);
                request.setAttribute("price", priceStr);
                request.setAttribute("address", address);
                request.getRequestDispatcher("Add.jsp").forward(request, response);
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
        return "Add Controller with Validation";
    }
}
