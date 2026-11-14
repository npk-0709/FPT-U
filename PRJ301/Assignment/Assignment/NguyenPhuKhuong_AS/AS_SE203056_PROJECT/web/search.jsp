<%-- 
    Document   : search
    Created on : Apr 26, 2025, 8:59:02 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page import="java.util.List"%>
<%@page import="pe.model.UserDTO"%>
<%@page import="pe.model.OrderDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Page</title>
    </head>
    <body>
        <%
            UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
            if (loginUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            String searchValue = (String) request.getAttribute("SEARCH_VALUE");
            if (searchValue == null) {
                searchValue = request.getParameter("search");
            }
            if (searchValue == null) {
                searchValue = "";
            }
        %>
        
        <h2>Welcome to <font color="blue"><b><%= loginUser.getFullName() %></b></font></h2>
        <ul>
            <li>Search by address</li>
            <li><a href="MainController?action=Logout">Logout</a></li>
        </ul>
        
        <form action="MainController" method="GET">
            Criteria for searching: 
            <input type="text" name="search" value="<%= searchValue %>" />
            <input type="submit" name="action" value="Search" />
        </form>
        <br/>
        
        <%
            List<OrderDTO> listOrders = (List<OrderDTO>) request.getAttribute("LIST_ORDERS");
            if (listOrders != null && !listOrders.isEmpty()) {
        %>
        <table border="1">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Order Date</th>
                    <th>Customer</th>
                    <th>Address</th>
                    <th>Total Amount</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (OrderDTO order : listOrders) {
                %>
                <tr>
                    <td><%= order.getOrderID() %></td>
                    <td><%= order.getOrderDate() %></td>
                    <td><%= order.getCustomer() %></td>
                    <td><%= order.getAddress() %></td>
                    <td><%= (long) order.getTotalAmount() %></td>
                    <td><%= order.getStatus() %></td>
                    <td>
                        <%
                            if ("AD".equals(loginUser.getRoleID()) && "Pending".equalsIgnoreCase(order.getStatus())) {
                        %>
                        <form action="MainController" method="POST" style="display:inline;">
                            <input type="hidden" name="orderID" value="<%= order.getOrderID() %>" />
                            <input type="hidden" name="search" value="<%= searchValue %>" />
                            <input type="submit" name="action" value="Delete" />
                        </form>
                        <%
                            }
                        %>
                    </td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <%
            }
            String message = (String) request.getAttribute("MESSAGE");
            if (message != null) {
        %>
            <font color="red"><%= message %></font>
        <%
            }
        %>
    </body>
</html>
