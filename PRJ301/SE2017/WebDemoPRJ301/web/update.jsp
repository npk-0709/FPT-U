
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="dto.InvoiceDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
    <c:set var="invUpdate" value="${sessionScope.UPDATEINVOICE}" />
        
        <form action="Maincontroller">
            <h2 class="infor">Update invoice information</h2>

            <div class="box">
                <label>Invoice ID</label>
                <input disabled="true" type="text" name="invId" required="true" value="${invUpdate.getInvID()}"/>
                <span></span>
            </div>
            <div class="box">
                <label>Invoice Date</label>
                <input type="text" name="invDate" required="true" value="${invUpdate.getInvDate()}"/>
                <span></span>
            </div>
            <div class="box">
                <label>Customer</label>
                <input type="text" name="customer" required="true" value="${invUpdate.getCustomer()} "/>
                <span></span>
            </div>
            <div class="box">
                <input type="hidden" name="checkSave" value="saveUpdate"/>
                <input type="submit" name="action" value="Save" class="marginleft"/>
                <input type="reset" name="action" value="Reset" class="marginleft"/>
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
