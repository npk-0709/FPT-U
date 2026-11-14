package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dao.InvoiceDAO;
import dto.InvoiceDTO;
import dto.InvoiceError;
import dto.UserDTO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ADMIN
 */
@WebServlet(urlPatterns = {"/SaveAddNewController"})
public class SaveAddNewController extends HttpServlet {

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
        try ( PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            System.out.println("Login");
            String invID = request.getParameter("invId");
            String invDate = request.getParameter("invDate");
            String customer = request.getParameter("customer");
            UserDTO user = (UserDTO) session.getAttribute("USER");
            InvoiceDAO invDAO = new InvoiceDAO();

            InvoiceError invErr = new InvoiceError();
            boolean check = true;
            if (!invID.matches("I\\d{5}$")) {
                invErr.setInvIDErr("Invalid format InvID!!!");
                check = false;
            } else if (invDAO.checkDuplicate(invID)) {
                invErr.setInvIDErr("Invalid format InvID!!!");
                check = false;
            }

            SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
            fm.setLenient(false);

            String sqlDate = null;

            try {
                Date date = fm.parse(invDate);

                SimpleDateFormat sqlFm = new SimpleDateFormat("yyyy-MM-dd");
                sqlDate = sqlFm.format(date);

            } catch (ParseException e) {
                invErr.setInvDateErr("Invalid format InvDate!!!");
                check = false;
            }
            if (customer.length() < 2 || customer.length() > 30) {
                invErr.setCustomerErr("Customer must be length more than 2");
                check = false;
            }
            if (check) {
                String userID = user.getUser_ID();
                InvoiceDTO invNew = new InvoiceDTO(invID, sqlDate, customer, userID);
                if (invDAO.addNewInvoice(invNew) >= 0) {
                    session.setAttribute("LISTINVOICE", invDAO.getListInvoices(userID));
                    response.sendRedirect("search.jsp");
                } else {
                    System.out.println("Save ADD ERROR");
                }
            } else {
                session.setAttribute("INVOICEERROR", invErr);
                response.sendRedirect("addNewInvoice.jsp");
            }

        }
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
