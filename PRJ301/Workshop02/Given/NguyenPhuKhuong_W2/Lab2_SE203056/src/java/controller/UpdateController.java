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
 * UpdateController
 *
 * @author Khuong
 */
@WebServlet(name = "UpdateController", urlPatterns = {"/UpdateController", "/updateController"})
public class UpdateController extends HttpServlet {
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
            String homeID = request.getParameter("homeID");
            if (homeID == null || homeID.trim().isEmpty()) {
                response.sendRedirect("Homestay.jsp");
                return;
            }
            try {
                HomestayDAO homestayDAO = new HomestayDAO();
                HomestayDTO homestay = homestayDAO.getHomestayByID(homeID.trim());
                if (homestay == null) {
                    response.sendRedirect("Homestay.jsp");
                    return;
                }
                request.setAttribute("homestay", homestay);
                request.getRequestDispatcher("Update.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println(ex);
                response.sendRedirect("Homestay.jsp");
            }

        } else if ("POST".equalsIgnoreCase(method)) {
            String homeID = request.getParameter("homeID");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String address = request.getParameter("address");
            String userID = request.getParameter("userID");
            if (userID == null || userID.trim().isEmpty()) {
                userID = (String) session.getAttribute("USER_ID");
            }

            List<String> errors = new ArrayList<>();
            HomestayDAO homestayDAO = new HomestayDAO();

            if (homeID == null || homeID.trim().isEmpty()) {
                errors.add("Invalid Home ID.");
            } else {
                try {
                    if (!homestayDAO.existsHomestay(homeID.trim())) {
                        errors.add("Home ID does not exist in the system.");
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    System.out.println(ex);
                    errors.add("Error checking Home ID.");
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
                HomestayDTO partialHomestay = new HomestayDTO(
                        homeID != null ? homeID.trim() : "",
                        description != null ? description.trim() : "",
                        price,
                        address != null ? address.trim() : "",
                        userID
                );
                request.setAttribute("homestay", partialHomestay);
                request.setAttribute("priceStr", priceStr);
                request.setAttribute("ERRORS", errors);
                request.getRequestDispatcher("Update.jsp").forward(request, response);
                return;
            }

            try {
                HomestayDTO updated = new HomestayDTO(
                        homeID.trim(),
                        description.trim(),
                        price,
                        address.trim(),
                        userID
                );
                homestayDAO.updateHomestay(updated);

                response.sendRedirect("MainController?action=list");
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println(ex);
                errors.add("Database connection error while updating homestay.");
                HomestayDTO partialHomestay = new HomestayDTO(homeID, description, price, address, userID);
                request.setAttribute("homestay", partialHomestay);
                request.setAttribute("priceStr", priceStr);
                request.setAttribute("ERRORS", errors);
                request.getRequestDispatcher("Update.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { processRequest(request, response);}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {processRequest(request, response);}

    @Override
    public String getServletInfo() { return "Update Controller with Validation";}
}
