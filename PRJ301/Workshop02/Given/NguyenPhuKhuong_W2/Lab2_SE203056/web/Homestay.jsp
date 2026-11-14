<%-- 
File Homestay.jsp
Author     : Khuong
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- Check login --%>
<c:if test="${empty sessionScope.USER_ID}">
    <c:redirect url="Login.jsp"/>
</c:if>

<c:if test="${empty sessionScope.HOMESTAY_LIST}">
    <c:redirect url="MainController?action=list"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Homestay Management</title>
    </head>
    <body>
        <div>
            <h2>Welcome, <b style="color:green"><c:out value="${sessionScope.FULLNAME}"/>!</b></h2>
            <a href="MainController?action=logout"
               style="display: inline-block; color: red; border-radius: 3px; background-color: #ffc107; 
               border: 2px solid #ffc107; padding: 4px 8px; text-decoration: none;">Logout</a>
        </div>

        <div>
            <h1>Homestay Management</h1>

            <p><a href="MainController?action=add">Add New</a></p>

            <c:choose>
                <c:when test="${not empty sessionScope.HOMESTAY_LIST}">
                    <table border="1" style="border: 2px solid #333333; border-collapse: collapse; min-width: 600px;">
                        <thead>
                            <tr style="background-color: #f2f2f2;">
                                <th>No.</th>
                                <th>Home ID</th>
                                <th>Description</th>
                                <th>Price</th>
                                <th>Address</th>
                                <th>User ID</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="totalPrice" value="0"/>
                            <c:forEach var="dto" items="${sessionScope.HOMESTAY_LIST}" varStatus="status">
                                <tr>
                                    <td style="text-align: center;"><c:out value="${status.count}"/></td>
                                    <td><c:out value="${dto.homeID}"/></td>
                                    <td><c:out value="${dto.description}"/></td>
                                    <td style="text-align: right;"><c:out value="${dto.price}"/></td>
                                    <td><c:out value="${dto.address}"/></td>
                                    <td><c:out value="${dto.userID}"/></td>
                                    <td style="text-align: center;">
                                        <a href="MainController?action=update&homeID=<c:out value="${dto.homeID}"/>">Update</a>
                                    </td>
                                </tr>
                                <c:set var="totalPrice" value="${totalPrice + dto.price}"/>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr style="background-color: #e6f7ff; font-weight: bold;">
                                <td colspan="3" style="text-align: right;">Total Price:</td>
                                <td style="text-align: right;"><c:out value="${totalPrice}"/></td>
                                <td colspan="3"></td>
                            </tr>
                        </tfoot>
                    </table>
                </c:when>
                <c:otherwise>
                    <p style="color:red">No homestays found!</p>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>
