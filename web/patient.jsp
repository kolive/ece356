<%-- 
    Document   : patient
    Created on : Mar 11, 2014, 10:21:51 AM
    Author     : Kyle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Patient Homepage</title>
    </head>
    <body>
        <!--
        _________________________
        |           |           |
        |   status  |  personal |
        |___________|___________|
        |       history         |
        |_______________________|
        |       details         |
        |_______________________|
        
        -->
        <jsp:useBean id="user" type="models.User" scope="session" />
        <div class="view-container">
            <div class="status">
                <!-- Outlines active perscriptions & diagnoses, next appt -->
                <p> Welcome, <%= user.getStringParam("fname") %>  </p>
                <p> Listed below is a summary of your current medical status, active perscriptions, and your next scheduled appointment. </p>
                <div class="summary">
                    Status: <%= user.getStringParam("current_health") %>
                </div>
                
                
            </div>
            <div class="personal">
                <!-- outlines personal info, links to change it -->
            </div>
            <div class="history">
                <!-- outlines visit history, selecting a row will show
                details in the details section -->
            </div>
            <div class="details">
                <!-- shows details about a visit -->
            </div>
        </div>
    </body>
</html>
