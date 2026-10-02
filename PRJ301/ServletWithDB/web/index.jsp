<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Trang Đăng Nhập</title>
        <style>
            /* Reset & Typography */
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f7f6;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }

            /* Khung Form Đăng Nhập */
            .login-container {
                background-color: #ffffff;
                padding: 40px;
                border-radius: 8px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                width: 350px;
            }

            .login-container h2 {
                text-align: center;
                color: #2c3e50;
                margin-top: 0;
                margin-bottom: 25px;
                font-size: 24px;
            }

            /* Khung thông báo lỗi */
            .error-msg {
                color: #e74c3c;
                background-color: #fdf0ed;
                border: 1px solid #f9dcd8;
                padding: 10px;
                border-radius: 4px;
                text-align: center;
                font-size: 14px;
                font-weight: normal;
                margin-bottom: 20px;
                margin-top: 0;
            }

            /* Nhóm các ô nhập liệu */
            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                color: #555555;
                font-weight: 600;
                font-size: 14px;
            }

            .form-group input[type="text"],
            .form-group input[type="password"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #cccccc;
                border-radius: 4px;
                font-size: 14px;
                box-sizing: border-box; /* Giữ input không bị tràn viền khi thêm padding */
                transition: border-color 0.3s;
            }

            .form-group input[type="text"]:focus,
            .form-group input[type="password"]:focus {
                border-color: #3498db;
                outline: none;
            }

            /* Nút Đăng nhập */
            button[type="submit"] {
                width: 100%;
                padding: 12px;
                background-color: #3498db;
                color: white;
                border: none;
                border-radius: 4px;
                font-size: 16px;
                font-weight: bold;
                cursor: pointer;
                transition: background-color 0.3s, transform 0.1s;
                margin-top: 10px;
            }

            button[type="submit"]:hover {
                background-color: #2980b9;
            }

            button[type="submit"]:active {
                transform: scale(0.98);
            }
        </style>
    </head>
    <body>
        <%
            String errorlog = "";
            if (session == null || session.getAttribute("ERROR_MSG") == null) {
                errorlog = "Please fill in the blank !";
            } else {
                errorlog = (String) session.getAttribute("ERROR_MSG");
            }
        %>
        
        <div class="login-container">
            <h2>Đăng nhập hệ thống</h2>
            
            <h4 class="error-msg"><%= errorlog %></h4>
            
            <form action="Maincontroller" method="POST">
                <div class="form-group">
                    <label>Tài khoản:</label>
                    <input type="text" name="username" placeholder="Nhập tài khoản..." required>
                </div>

                <div class="form-group">
                    <label>Mật khẩu:</label>
                    <input type="password" name="password" placeholder="Nhập mật khẩu..." required>
                </div>

                <input type="hidden" value="login" name="action">

                <button type="submit">Đăng nhập</button>
            </form>
        </div>
        
    </body>
</html>