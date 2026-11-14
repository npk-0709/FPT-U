<%-- 
    Document   : addNewInvoice
    Created on : Oct 2, 2026, 4:37:27 PM
    Author     : ADMIN
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="dto.InvoiceError"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
    <c:set var="invErr" value="${sessionScope.INVOICEERROR}"/>
        
        <form action="Maincontroller">
            <h2 class="infor">Add New invoice information</h2>

            <div class="box">
                <label>Invoice ID</label>
                <input type="text" name="invId" required="true"/>
                <span style="color:red;">${invErr.getInvIDErr()}</span>
            </div>
            <div class="box">
                <label>Invoice Date</label>
                <input type="text" name="invDate" required="true"/>
                <span style="color:red;">${invErr. getInvDateErr()}</span>
            </div>
            <div class="box">
                <label>Customer</label>
                <input type="text" name="customer" required="true"/>
                <span style="color:red;">${invErr.getCustomerErr()}</span>
            </div>
            <div class="box">
                <input type="submit" name="action" value="Save" class="marginleft"/>
                <a href="Maincontroller?action=Cancel">Cancel</a>
            </div>

            <style>
                .box label{
                    display: inline-block;
                    width: 100px;
                }
                .box{
                    margin-bottom: 8px;
                }
            </style>
        </form>

    </body>
</html>
