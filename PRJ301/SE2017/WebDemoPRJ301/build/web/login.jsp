
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>       
        <div>
            <form action="Maincontroller" method="POST">
                <p>
                 ${sessionScope.ERRORLOGIN}
                </p>
                <div style="height:30px">
                    <label>Username</label>
                    <input type="text" name="username"/>
                </div>
                <div style="height:30px">
                    <label>Password</label>
                    <input type="password" name="password"/>
                </div>
                <div style="height:30px">
                    <input type="submit" value="Login" name="action"/>
                    <input type="reset" value="Cancel" name="action"/>
                </div>
            </form>

        </div>
    </body>
</html>
