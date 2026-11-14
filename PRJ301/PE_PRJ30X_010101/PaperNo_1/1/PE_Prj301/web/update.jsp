<%-- 
    Document   : update
    Created on : Apr 26, 2025, 8:59:02 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.model.UserDto"%>
<%@page import="pe.model.FashionDto"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Page</title>
        <link rel="stylesheet" href="styles/bootstrap.min.css">
    </head>
    <body>
        <%
            UserDto account = (UserDto) session.getAttribute("account");
            if (account == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            FashionDto fashion = (FashionDto) request.getAttribute("fashion");
            String keyword = request.getParameter("keyword");
            if (keyword == null) keyword = "";
        %>
        <div class="container">
            <h2>Welcome to <span style="color:blue;"><%= account.getFullName() %></span>!</h2>
            <ul>
                <li><a href="MainController?action=search">Search by name</a></li>
                <li><a href="MainController?action=logout">Logout</a></li>
            </ul>

            <h3>Update Fashion Product</h3>

            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
                <div class="alert alert-danger"><%= error %></div>
            <%
                }
            %>

            <% if (fashion != null) { %>
            <form action="MainController" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="<%= fashion.getId() %>">
                <input type="hidden" name="keyword" value="<%= keyword %>">

                <div class="form-group">
                    <label>ID:</label>
                    <input type="text" class="form-control" value="<%= fashion.getId() %>" readonly>
                </div>
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" class="form-control" value="<%= fashion.getName() %>" required>
                </div>
                <div class="form-group">
                    <label>Description:</label>
                    <input type="text" name="description" class="form-control" value="<%= fashion.getDescription() %>" required>
                </div>
                <div class="form-group">
                    <label>Price:</label>
                    <input type="number" step="0.01" name="price" class="form-control" value="<%= fashion.getPrice() %>" required min="0">
                </div>
                <div class="form-group">
                    <label>Size:</label>
                    <input type="text" name="size" class="form-control" value="<%= fashion.getSize() %>" required>
                </div>
                <div class="form-group">
                    <label>Status:</label>
                    <select name="status" class="form-control">
                        <option value="1" <%= fashion.isStatus() ? "selected" : "" %>>In business (1)</option>
                        <option value="0" <%= !fashion.isStatus() ? "selected" : "" %>>Temporarily suspended (0)</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
                <a href="MainController?action=search&keyword=<%= keyword %>" class="btn btn-default">Cancel</a>
            </form>
            <% } %>
        </div>
        <script src="scripts/jquery.min.js"></script>
        <script src="scripts/bootstrap.min.js"></script>
    </body>
</html>
