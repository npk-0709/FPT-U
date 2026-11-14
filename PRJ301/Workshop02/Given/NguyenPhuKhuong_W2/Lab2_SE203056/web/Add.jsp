<%-- 
File Add.jsp - Add new homestay form
Author     : Khuong
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${empty sessionScope.USER_ID}">
    <c:redirect url="Login.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Homestay</title>
    </head>
    <body>
        <div>
            <h2>Add New Homestay</h2>

            <c:if test="${not empty ERRORS}">
                <div style="color:red; margin-bottom: 10px;">
                    <ul>
                        <c:forEach var="err" items="${ERRORS}">
                            <li><c:out value="${err}"/></li>
                            </c:forEach>
                    </ul>
                </div>
            </c:if>
            <c:if test="${empty ERRORS and not empty ERROR_MSG}">
                <p style="color:red"><c:out value="${ERROR_MSG}"/></p>
            </c:if>

            <form action="AddController" method="POST">
                <div>
                    <label>Home ID:</label>
                    <input type="text" name="homeID" value="<c:out value="${homeID}"/>">
                </div>
                <br>
                <div>
                    <label>Description:</label>
                    <input type="text" name="description" value="<c:out value="${description}"/>">
                </div>
                <br>
                <div>
                    <label>Price:</label>
                    <input type="number" name="price" value="<c:out value="${price}"/>">
                </div>
                <br>
                <div>
                    <label>Address:</label>
                    <input type="text" name="address" value="<c:out value="${address}"/>">
                </div>
                <br>
                <button type="submit">Save</button>
                <a href="MainController?action=list"><button type="button">Cancel</button></a>
            </form>
        </div>
    </body>
</html>
