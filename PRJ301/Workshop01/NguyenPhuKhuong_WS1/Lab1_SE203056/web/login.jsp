<%-- 
File Login
 Author     : Khuong
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <div>
            <h2>Homestay Management</h2>
            <%
                String errorMsg = (String) request.getAttribute("ERROR_MSG");
                if (errorMsg == null)
                    errorMsg = "";
            %>
            <p style="color:red"><%= errorMsg%></p>
            <form action="mainController" method="POST">
                <input type="hidden" name="action" value="login">
                <div >
                    <label>User ID:</label>
                    <input type="text" name="userID" placeholder="userID" required>
                </div>
                <div >
                    <label>Password:</label>
                    <input type="password" name="password" placeholder="password" required>
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    </body>
</html>
