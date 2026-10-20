<%-- 
File Homestay.jsp
Author     : Khuong
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="dto.HomestayDTO"%>
<%
    if (session.getAttribute("USER_ID") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Homestay Management</title>
    </head>
    <body>
        <div >
            <h2>Welcome, <b style="color:green"><%= session.getAttribute("FULLNAME") != null ? session.getAttribute("FULLNAME") : ""%> !</b></h2>
            <a  href="mainController?action=logout" style="display: inline-block;color:red;border-radius:3px;background-color: #ffc107; border: 2px solid #ffc107;">Logout</a>
        </div>
        <div >
            <h1>Homestay Management</h1>
            <form  action="mainController" method="POST">
                <input type="hidden" name="action" value="search">
                <%
                    String searchParam = (String) session.getAttribute("TXT_SEARCH");
                    if (searchParam == null)
                        searchParam = "";
                %>
                <input type="text" name="txtsearch"
                       placeholder="Search by address..."
                       value="<%= searchParam%>"/>
                <input type="submit" value="Search" style="color:yellow;background: cyan;border-radius:3px"/>
            </form>
            <%
                List<HomestayDTO> homestayList = (List<HomestayDTO>) session.getAttribute("HOMESTAY_LIST");
                if (homestayList != null && !homestayList.isEmpty()) {
            %>
            <br>
            <table style="border: 3px solid #333333;border-radius:5px ">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>HomeID</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Address</th>
                        <th>UserID</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        int no = 1;
                        for (HomestayDTO dto : homestayList) {
                    %>
                    <tr>
                        <td><%= no++%></td>
                        <td><%= dto.getHomeID()%></td>
                        <td><%= dto.getDescription()%></td>
                        <td><%= dto.getPrice()%></td>
                        <td><%= dto.getAddress()%></td>
                        <td><%= dto.getUserID()%></td>
                        <td>
                            <a href="mainController?action=delete&homeID=<%= dto.getHomeID()%>">
                                Delete
                            </a>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
            } else if (homestayList != null && homestayList.isEmpty()) {
            %>
            <p style="color:red">No homestays found!</p>
            <%
                }
            %>
        </div>
    </body>
</html>
