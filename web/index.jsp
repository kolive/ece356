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
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script type="text/javascript">
             $( document ).ready(function() { 
                 $("#patientlogin").click(function(){
                     $("input[name='type']").val('patient');
                     $("#patientlogin").css('display', 'none');
                     $("#employeelogin").css('display', 'none');
                     $(".login").css('display', 'block');
                 });
                 
                 $("#employeelogin").click(function(){
                     $("input[name='type']").val('employee');
                     $("#patientlogin").css('display', 'none');
                     $("#employeelogin").css('display', 'none');
                     $(".login").css('display', 'block');
                 });
                 
                 $("#loginsubmit").click(function(){
                     $(".login").submit();
                 });
             });
                
        
        </script>
        <title>ECE356 Hospital MIS Portal</title>
    </head>
    <body>
        <div class="fullcenter rectangle1 rounded">
            <p class="title"> Hospital MIS Portal Login </p>
            <div class="button" id="patientlogin"> Patient Login </div>
            <div class="button" id="employeelogin"> Employee Login </div>
            <form class="login center" method="post" action="UserLoginServlet">
            <div class="padding">
                <div class="field">
                    id:  <input class="textfield" type="text" name="username">
                </div>
                <div class="field">
                    password: <input class="textfield" type="password" name="password">
                </div>
                <input type="hidden" name="type" value="employee">
                <div class="button" id="loginsubmit">
                    Login
                </div>
            </div>
            
            </form>

        </div>
    </body>
</html>
