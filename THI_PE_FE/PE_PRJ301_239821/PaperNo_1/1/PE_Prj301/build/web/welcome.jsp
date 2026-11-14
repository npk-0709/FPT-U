<%-- 
    Document   : welcome
    Created on : Apr 26, 2025, 8:58:34 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.model.UserDto"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome Page</title>
        <link rel="stylesheet" href="styles/bootstrap.min.css">
    </head>
    <body>
        <%
            UserDto account = (UserDto) session.getAttribute("USER");
            String xxx = "";
            if (account.getGender() == 1) {
                xxx = "Mr";
            } else {
                 xxx = "Ms";
            }
        %>
        <div class="container">
            <h2> <span style="color:red;">Welcome to<span style="color:red;"> <%= xxx %> <%= account.getFullName()%></span></span>!</h2>
            <ul>
                <li><a href="MainController?action=createuser">Create user</a></li>
                <li><a href="MainController?action=filterbystatus">Filter by status</a></li>
                <li><a href="MainController?action=logout">Logout</a></li>
            </ul>
        </div>
        <script src="scripts/jquery.min.js"></script>
        <script src="scripts/bootstrap.min.js"></script>
    </body>
</html>
