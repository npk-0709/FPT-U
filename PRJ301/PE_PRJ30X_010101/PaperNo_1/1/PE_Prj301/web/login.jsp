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
        <link rel="stylesheet" href="styles/bootstrap.min.css">
        <style>
            body { padding-top: 60px; }
            .login-form { max-width: 400px; margin: 0 auto; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="login-form">
                <h2 class="text-center">Login</h2>
                <%
                    String error = (String) request.getAttribute("error");
                    if (error != null) {
                %>
                    <div class="alert alert-danger"><%= error %></div>
                <%
                    }
                %>
                <form action="MainController" method="post">
                    <input type="hidden" name="action" value="login">
                    <div class="form-group">
                        <label>User ID:</label>
                        <input type="text" name="userID" class="form-control" required placeholder="Enter user ID">
                    </div>
                    <div class="form-group">
                        <label>Password:</label>
                        <input type="password" name="password" class="form-control" required placeholder="Enter password">
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Login</button>
                </form>
            </div>
        </div>
        <script src="scripts/jquery.min.js"></script>
        <script src="scripts/bootstrap.min.js"></script>
    </body>
</html>
