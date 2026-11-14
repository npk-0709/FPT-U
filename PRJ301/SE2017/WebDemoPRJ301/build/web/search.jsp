

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="dto.InvoiceDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>       
        <h1>${sessionScope.FULLNAME}</h1> 
        <h3>
            So nguoi dang dang online:
            ${applicationScope.onlineUsers}
        </h3>
        <div>
            <form action="Maincontroller" method="post">

                <input type="text" name="txtsearch" value="${sessionScope.CUSTOMERSEARCH}"/>
                <input type="submit" value="Search" name="action"/>
            </form>
        </div>
        <p>
            <a href="Maincontroller?action=Addnew">Add New</a>
        </p>
        <table>
            <tr>
                ${counter.count}
            </tr>
            <c:forEach var="x" varStatus="counter" items="${sessionScope.LISTINVOICE}">

                <tr>
                    <td>
                        ${x.getInvID()}
                    </td>
                    <td>
                        ${x.getInvDate()}
                    </td>
                    <td>
                        ${x.getCustomer()}
                    </td>
                    <td>
                        ${x.getCustomer()}
                    </td>
                    <td>
                        <a href="Maincontroller?action=Update&invID=${x.getInvID()}">Update</a>
                    </td>
                    <td>
                        <a href="Maincontroller?action=Delete&invID=${x.getInvID()}">Delete</a>
                    </td>

                </tr>   
            </c:forEach>
        </table>


        <p>
            <a href="Maincontroller?action=Logout">Logout</a>
        </p>
    </body>
</html>
