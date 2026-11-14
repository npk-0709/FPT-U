<%-- 
    Document   : login
    Created on : Apr 26, 2025, 8:58:20 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <h1>Login Page</h1>
        <form action="MainController" method="POST">
            User ID: <input type="text" name="userID" required="required"  /><br/>
            Password: <input type="password" name="password" required="required" /><br/>
            <input type="submit" name="action" value="Login" />
        </form>
        <br/>
        <%
            String error = (String) request.getAttribute("ERROR");
            if (error != null) {
        %>
            <font color="red"><%= error %></font>
        <%
            }
        %>
    </body>
</html>
