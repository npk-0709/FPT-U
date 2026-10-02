<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>




<!DOCTYPE html>
<html>
    <head>
        <title>JSP Interactive Demo</title>
        <style>
            body {
                font-family: sans-serif;
                margin: 40px;
            }
            .greeting {
                color: #2c3e50;
                background: #ecf0f1;
                padding: 15px;
                border-radius: 5px;
            }
        </style>
    </head>
    <body>
        <h2>Welcome to the JSP Demo!</h2>

        <!-- Standard HTML Form -->
        <form method="POST" action="#">
            <label for="name">Enter your name:</label>
            <input type="text" id="name" name="userName" required>
            <input type="submit" value="Say Hello">
        </form>
        <%
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String formattedDate = sdf.format(new Date());
        %>  

        <p>Today is: <strong><%= formattedDate%></strong></p>
    </body>
</html>