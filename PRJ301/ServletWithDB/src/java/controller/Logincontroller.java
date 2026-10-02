package controller;

import dao.InvoiceDAO;
import dto.InvoiceDTO;
import dao.UserDAO;
import dto.UserDTO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author Khuong
 */
@WebServlet(name = "Logincontroller", urlPatterns = {"/Logincontroller"})
public class Logincontroller extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            HttpSession session = request.getSession();
            UserDAO objDAO = new UserDAO();
            UserDTO user = objDAO.checklogin(username, password);
            if (user != null) {
                InvoiceDAO objInvoice = new InvoiceDAO();
                List<InvoiceDTO> getListInvoice = objInvoice.getInvoice(username);
                session.setAttribute("USER", user);
                session.setAttribute("INVOICE_LIST", getListInvoice);
                session.setAttribute("USER_ID", user.getUser_ID());
                session.setAttribute("FULLNAME", user.getFullName());
                response.sendRedirect("search.jsp");

            } else {
                session.setAttribute("ERROR_MSG", "Login failed! Incorrect username or password.");
                RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
                rd.forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("error !");
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
