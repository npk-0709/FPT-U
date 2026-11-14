<%-- 
File Login.jsp
 Author     : Khuong
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <div>
            <h2>Homestay Management</h2>
            <c:if test="${not empty ERROR_MSG}">
                <p style="color:red"><c:out value="${ERROR_MSG}"/></p>
            </c:if>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="login">
                <div>
                    <label>User ID:</label>
                    <input type="text" name="userID" placeholder="userID" required>
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" name="password" placeholder="password" required>
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    </body>
</html>
