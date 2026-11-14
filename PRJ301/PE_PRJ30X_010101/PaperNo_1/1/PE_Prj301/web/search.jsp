<%-- 
    Document   : search
    Created on : Apr 26, 2025, 8:59:02 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.model.UserDto"%>
<%@page import="pe.model.FashionDto"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Page</title>
        <link rel="stylesheet" href="styles/bootstrap.min.css">
    </head>
    <body>
        <%
            UserDto account = (UserDto) session.getAttribute("account");
            if (account == null) {
                response.sendRedirect("login.jsp");
                return;
            }
        %>
        <div class="container">
            <h2>Welcome to <span style="color:blue;"><%= account.getFullName() %></span>!</h2>
            <ul>
                <li><a href="MainController?action=search">Search by name</a></li>
                <li><a href="MainController?action=logout">Logout</a></li>
            </ul>

            <form action="MainController" method="post">
                <input type="hidden" name="action" value="search">
                <label>Criteria for searching:</label>
                <input type="text" name="keyword" value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
                <button type="submit" class="btn btn-default">Search</button>
            </form>

            <%
                List<FashionDto> fashionList = (List<FashionDto>) request.getAttribute("fashionList");
                String keyword = (String) request.getAttribute("keyword");
                if (fashionList != null) {
                    if (fashionList.isEmpty()) {
            %>
                        <p style="color:red;">No data matching the search criteria found</p>
            <%
                    } else {
            %>
                        <table class="table table-bordered" style="margin-top:15px;">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Description</th>
                                    <th>Price</th>
                                    <th>Size</th>
                                    <th>Status</th>
                                    <th>Tool</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (FashionDto f : fashionList) {
                                %>
                                <tr>
                                    <td><%= f.getId() %></td>
                                    <td><%= f.getName() %></td>
                                    <td><%= f.getDescription() %></td>
                                    <td><%= f.getPrice() %></td>
                                    <td><%= f.getSize() %></td>
                                    <td><%= f.isStatus() %></td>
                                    <td>
                                        <a href="MainController?action=showUpdate&id=<%= f.getId() %>&keyword=<%= keyword %>">Update</a>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
            <%
                    }
                }
            %>
        </div>
        <script src="scripts/jquery.min.js"></script>
        <script src="scripts/bootstrap.min.js"></script>
    </body>
</html>
