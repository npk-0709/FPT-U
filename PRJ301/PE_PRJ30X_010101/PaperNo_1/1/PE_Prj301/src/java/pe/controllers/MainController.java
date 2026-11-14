/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.controllers;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.FashionDao;
import pe.model.FashionDto;
import pe.model.UserDao;
import pe.model.UserDto;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class MainController extends HttpServlet {

    private static final String WELCOME = "login.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = WELCOME;
        try {
            String action = request.getParameter("action");
            //-----            your code here   --------------------------------
            if (action == null) {
                action = "";
            }

            switch (action) {
                case "login":
                    url = handleLogin(request, response);
                    break;
                case "logout":
                    url = handleLogout(request, response);
                    break;
                case "search":
                    url = handleSearch(request, response);
                    break;
                case "showUpdate":
                    url = handleShowUpdate(request, response);
                    break;
                case "update":
                    url = handleUpdate(request, response);
                    break;
                default:
                    url = WELCOME;
                    break;
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("error at MainController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private String handleLogin(HttpServletRequest request, HttpServletResponse response) {
        String userID = request.getParameter("userID");
        String password = request.getParameter("password");

        UserDao userDao = new UserDao();
        UserDto user = userDao.login(userID, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("account", user);
            return "welcome.jsp";
        } else {
            request.setAttribute("error", "Invalid user or password");
            return WELCOME;
        }
    }

    private String handleLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return WELCOME;
    }

    private String handleSearch(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            return WELCOME;
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        request.setAttribute("keyword", keyword);

        if (!keyword.trim().isEmpty()) {
            FashionDao fashionDao = new FashionDao();
            List<FashionDto> list = fashionDao.searchByName(keyword.trim());
            request.setAttribute("fashionList", list);
        }
        return "search.jsp";
    }

    private String handleShowUpdate(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            return WELCOME;
        }

        String id = request.getParameter("id");
        FashionDao fashionDao = new FashionDao();
        FashionDto fashion = fashionDao.getById(id);
        request.setAttribute("fashion", fashion);
        return "update.jsp";
    }

    private String handleUpdate(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            return WELCOME;
        }

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String size = request.getParameter("size");
        String statusStr = request.getParameter("status");

        // Validate
        boolean hasError = false;
        StringBuilder errorMsg = new StringBuilder();

        if (name == null || name.trim().isEmpty()) {
            errorMsg.append("Name must not be empty. ");
            hasError = true;
        }
        if (description == null || description.trim().isEmpty()) {
            errorMsg.append("Description must not be empty. ");
            hasError = true;
        }
        if (size == null || size.trim().isEmpty()) {
            errorMsg.append("Size must not be empty. ");
            hasError = true;
        }

        float price = 0;
        try {
            price = Float.parseFloat(priceStr);
            if (price < 0) {
                errorMsg.append("Price must be a positive number. ");
                hasError = true;
            }
        } catch (NumberFormatException e) {
            errorMsg.append("Price must be a valid number. ");
            hasError = true;
        }

        // Validate id format: F-XXX (F + dash + 3 digits)
        if (id == null || !id.matches("F-\\d{3}")) {
            errorMsg.append("Invalid product ID format. ");
            hasError = true;
        }

        if (hasError) {
            FashionDao fashionDao = new FashionDao();
            FashionDto fashion = fashionDao.getById(id);
            if (fashion != null) {
                // Keep user-entered values
                fashion.setName(name != null ? name : "");
                fashion.setDescription(description != null ? description : "");
                fashion.setPrice(price);
                fashion.setSize(size != null ? size : "");
                fashion.setStatus("1".equals(statusStr));
            }
            request.setAttribute("fashion", fashion);
            request.setAttribute("error", errorMsg.toString().trim());
            return "update.jsp";
        }

        boolean statusVal = "1".equals(statusStr);
        FashionDto fashion = new FashionDto(id, name.trim(), description.trim(), price, size.trim(), statusVal);

        FashionDao fashionDao = new FashionDao();
        fashionDao.update(fashion);

        // After update, redirect to search with current keyword
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        request.setAttribute("keyword", keyword);

        if (!keyword.trim().isEmpty()) {
            List<FashionDto> list = fashionDao.searchByName(keyword.trim());
            request.setAttribute("fashionList", list);
        }
        return "search.jsp";
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
