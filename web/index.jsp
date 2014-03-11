<%-- 
    Document   : index
    Created on : Mar 11, 2014, 9:40:51 AM
    Author     : Kyle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>ECE356 Hospital MIS Portal</title>
    </head>
    <body>
        <div class="fullcenter rectangle1 rounded">
            <p class="title"> Hospital MIS Portal Login </p>
            <form class="login center">
            <div class="padding">
                id: <input type="text" name="firstname"><br>
                password: <input type="text" name="lastname"><br>
                <input type="radio" name="type" value="employee">Employee
                <input type="radio" name="type" value="patient">Patient <br>
                <br>
                <div class="center" style="width:50px">
                    <input class="center" style="width:50px" type="submit" value="Login">
                </div>
            </div>
            
            </form>

        </div>
    </body>
</html>
