
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Invoice</title>
        <style>
            /* Định dạng tổng thể trang */
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f7f6;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }

            /* Định dạng khung chứa form (Card) */
            form {
                background-color: #ffffff;
                padding: 30px 40px;
                border-radius: 8px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                width: 450px;
            }

            /* Tiêu đề */
            h2.infor {
                text-align: center;
                color: #333333;
                margin-bottom: 25px;
                margin-top: 0;
                font-size: 24px;
            }

            /* Các dòng nhập liệu */
            .box {
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                flex-wrap: wrap; /* Cho phép lỗi rớt dòng nếu dài */
            }
            
            .box label {
                width: 120px;
                font-weight: 600;
                color: #555555;
            }

            .box input[type="text"] {
                flex: 1;
                padding: 10px;
                border: 1px solid #cccccc;
                border-radius: 4px;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            .box input[type="text"]:focus {
                border-color: #4CAF50; /* Đổi màu viền khi click vào */
                outline: none;
            }
            
            /* Hiển thị lỗi */
            .box span {
                color: #e74c3c;
                font-size: 13px;
                width: 100%;
                margin-left: 120px; /* Thụt đầu dòng bằng với label */
                margin-top: 5px;
                display: block;
            }

            /* Khu vực nút bấm */
            .action-buttons {
                margin-top: 30px;
                margin-left: 120px; /* Canh lề thẳng với ô input */
                display: flex;
                gap: 15px; /* Khoảng cách giữa 2 nút */
            }

            /* Định dạng nút chung */
            input[type="submit"], button {
                padding: 10px 25px;
                border: none;
                border-radius: 4px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                transition: background-color 0.3s, transform 0.1s;
            }

            input[type="submit"]:active, button:active {
                transform: scale(0.95); /* Hiệu ứng lún khi click */
            }

            /* Nút Save */
            input[type="submit"] {
                background-color: #4CAF50;
                color: white;
            }

            input[type="submit"]:hover {
                background-color: #45a049;
            }

            /* Nút Cancel */
            button {
                background-color: #e74c3c;
                padding: 0; /* Xóa padding mặc định để thẻ <a> bao phủ */
            }

            button a {
                display: inline-block;
                padding: 10px 20px;
                text-decoration: none;
                color: white;
            }

            button:hover {
                background-color: #c0392b;
            }
        </style>
    </head>
    
    <body>
        <%

        %>
        
        <form action="mainController">
            <h2 class="infor">Add New Invoice Information</h2>
            
            <div class="box">
                <label>Invoice ID</label>
                <input type="text" name="invId" required="true" placeholder="Enter Invoice ID" />

            </div>
            
            <div class="box">
                <label>Invoice Date</label>
                <input type="text" name="invDate" required="true" placeholder="yyyy-mm-dd" />

            </div>
            
            <div class="box">
                <label>Customer</label>
                <input type="text" name="customer" required="true" placeholder="Customer Name" />
            </div>
            
            <div class="action-buttons">
                <input type="submit" name="action" value="Save" />
                <button type="button"><a href="mainController?action=Cancel">Cancel</a></button>
            </div>
            
        </form>
    </body>
</html>