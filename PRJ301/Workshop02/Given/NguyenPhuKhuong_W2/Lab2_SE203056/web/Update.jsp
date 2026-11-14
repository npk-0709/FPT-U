<%-- 
File Update.jsp
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
        <title>Update Homestay</title>
    </head>
    <body>
        <div>
            <h2>Update Homestay</h2>

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

            <form action="UpdateController" method="POST">
                <input type="hidden" name="homeID" value="<c:out value="${homestay.homeID}"/>">
                <input type="hidden" name="userID" value="<c:out value="${homestay.userID}"/>">
                <div>
                    <label>Home ID:</label>
                    <input type="text" value="<c:out value="${homestay.homeID}"/>" disabled>
                </div>
                <br>
                <div>
                    <label>Description:</label>
                    <input type="text" name="description" value="<c:out value="${homestay.description}"/>">
                </div>
                <br>
                <div>
                    <label>Price:</label>
                    <c:choose>
                        <c:when test="${not empty priceStr}">
                            <input type="number" name="price" value="<c:out value="${priceStr}"/>">
                        </c:when>
                        <c:otherwise>
                            <input type="number" name="price" value="<c:out value="${homestay.price}"/>">
                        </c:otherwise>
                    </c:choose>
                </div>
                <br>
                <div>
                    <label>Address:</label>
                    <input type="text" name="address" value="<c:out value="${homestay.address}"/>">
                </div>
                <br>
                <div>
                    <label>User ID:</label>
                    <input type="text" value="<c:out value="${homestay.userID}"/>" disabled>
                </div>
                <br>
                <button type="submit">Save</button>
                <a href="MainController?action=list"><button type="button">Cancel</button></a>
            </form>
        </div>
    </body>
</html>
