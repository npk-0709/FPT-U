<%-- 
    Document   : calc
    Created on : Oct 20, 2026, 4:08:03 PM
    Author     : Khuong
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>

    <c:set var ="num" value="10"/>

    display : <c:out value ="${num}"/></br>
    delete :<c:remove var="num"/><br>
    again : <c:out value ="${num}"/></br>
</body>
</html>
