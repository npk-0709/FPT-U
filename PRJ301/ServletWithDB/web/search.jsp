<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="dto.InvoiceDTO"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Invoices</title>
        <style>
            /* Reset & Typography */
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f7f6;
                margin: 0;
                padding: 0;
                color: #333;
            }

            /* Header (Thanh điều hướng trên cùng) */
            .top-header {
                background-color: #ffffff;
                padding: 15px 40px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }

            .top-header h2 {
                margin: 0;
                font-size: 18px;
                color: #2c3e50;
            }

            .logout-btn {
                text-decoration: none;
                background-color: #e74c3c;
                color: white;
                padding: 8px 16px;
                border-radius: 4px;
                font-weight: 600;
                font-size: 14px;
                transition: background-color 0.3s;
            }

            .logout-btn:hover {
                background-color: #c0392b;
            }

            /* Container chính (Khung chứa nội dung) */
            .container {
                max-width: 900px;
                margin: 40px auto;
                background: #ffffff;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            }

            .container h1 {
                margin-top: 0;
                text-align: center;
                color: #34495e;
                margin-bottom: 30px;
                text-transform: capitalize;
            }

            /* Form tìm kiếm */
            .search-form {
                display: flex;
                gap: 10px;
                justify-content: center;
                margin-bottom: 30px;
            }

            .search-form input[type="text"] {
                width: 400px;
                padding: 10px 15px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 15px;
                outline: none;
                transition: border-color 0.3s;
            }

            .search-form input[type="text"]:focus {
                border-color: #3498db;
            }

            .search-form input[type="submit"] {
                padding: 10px 25px;
                background-color: #3498db;
                color: white;
                border: none;
                border-radius: 4px;
                font-size: 15px;
                font-weight: bold;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            .search-form input[type="submit"]:hover {
                background-color: #2980b9;
            }

            /* Bảng dữ liệu (Table) */
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }

            table thead {
                background-color: #34495e;
                color: white;
            }

            table th, table td {
                padding: 12px 15px;
                text-align: center;
                border-bottom: 1px solid #eeeeee;
            }

            table th {
                font-weight: 600;
                letter-spacing: 0.5px;
            }

            table tbody tr:hover {
                background-color: #f9f9f9;
            }

            /* Thông báo lỗi */
            .empty-msg {
                text-align: center;
                color: #e74c3c;
                font-size: 16px;
                font-weight: bold;
                margin-top: 20px;
                padding: 15px;
                background-color: #fdf0ed;
                border-radius: 4px;
                border: 1px solid #f9dcd8;
            }
        </style>
    </head>
    <body>
        <!-- Phần Header -->
        <div class="top-header">
            <h2>Welcome, <%= session.getAttribute("FULLNAME") != null ? session.getAttribute("FULLNAME") : ""%> !</h2>
            <a class="logout-btn" href="/ServletWithDB/Maincontroller?action=logout">Đăng xuất</a>
        </div>

        <!-- Phần Nội Dung Chính -->
        <div class="container">
            <h1>Search Page</h1>

            <form class="search-form" action="Searchcontroller" method="POST" >
                <% String searchParam = (String) session.getAttribute("TXT_SEARCH");%>
                <input type="text" name="txtsearch" placeholder="Enter keyword to search..." value="<%= searchParam != null ? searchParam : ""%>" />
                <input type="submit" value="Search" name="action"/>
            </form>

            <%
                List<InvoiceDTO> invoiceList = (List<InvoiceDTO>) session.getAttribute("INVOICE_LIST");
                if (invoiceList != null && !invoiceList.isEmpty()) {
            %>
            <table>
                <thead>
                    <tr>
                        <th>Invoice ID</th>
                        <th>Invoice Date</th>
                        <th>Customer</th>
                        <th>User ID</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (InvoiceDTO dto : invoiceList) {
                    %>
                    <tr>
                        <td><%= dto.getInvID()%></td>
                        <td><%= dto.getInvDate()%></td>
                        <td><%= dto.getCustomer()%></td>
                        <td><%= dto.getUserID()%></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
            } else if (invoiceList != null && invoiceList.isEmpty()) {
            %>
            <p class="empty-msg">Không tìm thấy hóa đơn nào!</p>
            <%
                }
            %>
        </div>
    </body>
</html>