<%@page import="dto.InvoiceDTO"%>
<%@page import="dto.InvoiceError"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Invoice</title>
        <style>
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
            form { background-color: #ffffff; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); width: 450px; }
            h2.infor { text-align: center; color: #333333; margin-bottom: 25px; margin-top: 0; font-size: 24px; }
            .box { margin-bottom: 20px; display: flex; align-items: center; flex-wrap: wrap; }
            .box label { width: 120px; font-weight: 600; color: #555555; }
            .box input[type="text"] { flex: 1; padding: 10px; border: 1px solid #cccccc; border-radius: 4px; font-size: 14px; transition: border-color 0.3s; }
            .box input[type="text"]:focus { border-color: #4CAF50; outline: none; }
            .box span { color: #e74c3c; font-size: 13px; width: 100%; margin-left: 120px; margin-top: 5px; display: block; }
            .action-buttons { margin-top: 30px; margin-left: 120px; display: flex; gap: 15px; }
            input[type="submit"], button { padding: 10px 25px; border: none; border-radius: 4px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background-color 0.3s, transform 0.1s; }
            input[type="submit"]:active, button:active { transform: scale(0.95); }
            input[type="submit"] { background-color: #3498db; color: white; }
            input[type="submit"]:hover { background-color: #2980b9; }
            button { background-color: #e74c3c; padding: 0; }
            button a { display: inline-block; padding: 10px 20px; text-decoration: none; color: white; }
            button:hover { background-color: #c0392b; }
        </style>
    </head>
    <body>
        <%
            InvoiceDTO inv = (InvoiceDTO) request.getAttribute("INVOICE");
            if (inv == null) inv = new InvoiceDTO("", "", "", "");
            InvoiceError error = (InvoiceError) request.getAttribute("INVOICE_ERROR");
            if (error == null) error = new InvoiceError();
        %>
        <form action="Maincontroller" method="POST">
            <h2 class="infor">Update Invoice Information</h2>
            
            <input type="hidden" name="action" value="update" />
            <input type="hidden" name="actionSubmit" value="Update" />
            
            <div class="box">
                <label>Invoice ID</label>
                <input type="text" name="invId" value="<%= inv.getInvID() != null ? inv.getInvID() : "" %>" readonly="readonly" style="background-color: #eee;" />
                <% if (error.getInvIDError() != null && !error.getInvIDError().isEmpty()) { %>
                    <span><%= error.getInvIDError() %></span>
                <% } %>
            </div>
            
            <div class="box">
                <label>Invoice Date</label>
                <input type="text" name="invDate" value="<%= inv.getInvDate() != null ? inv.getInvDate() : "" %>" placeholder="yyyy-mm-dd" />
                <% if (error.getInvDateError() != null && !error.getInvDateError().isEmpty()) { %>
                    <span><%= error.getInvDateError() %></span>
                <% } %>
            </div>
            
            <div class="box">
                <label>Customer</label>
                <input type="text" name="customer" value="<%= inv.getCustomer() != null ? inv.getCustomer() : "" %>" placeholder="Customer Name" />
                <% if (error.getCustomerError() != null && !error.getCustomerError().isEmpty()) { %>
                    <span><%= error.getCustomerError() %></span>
                <% } %>
            </div>

            <% if (error.getMessageError() != null && !error.getMessageError().isEmpty()) { %>
                <div style="color: #e74c3c; text-align: center; margin-bottom: 15px; font-size: 14px;">
                    <%= error.getMessageError() %>
                </div>
            <% } %>
            
            <div class="action-buttons">
                <input type="submit" value="Update" />
                <button type="button"><a href="Searchcontroller">Cancel</a></button>
            </div>
            
        </form>
    </body>
</html>
