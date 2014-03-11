<%-- 
    Document   : staff
    Created on : Mar 11, 2014, 10:29:21 AM
    Author     : Kyle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Staff Homepage</title>
    </head>
     <body>
        <!--
        _________________________
        |           |           |
        |  users    |  info     |
        |___________|___________|
        |       appointments    |
        |_______________________|

        
        -->
        <div class="view-container">
            <div class="users">
                <!-- 
                   A list of doctors and patients
                   When one is selected, changes info pane
                   Button to bring up ui for adding new patient 
                   or assigning patient by id
                -->
            </div>
            <div class="info">
                <!-- 
                outlines personal info, links to change it 
                when a doctor is selected, shows a list of assigned patients
                if the doctor is staff's boss, can also get record info
                -->
            </div>
            <div class="appointments">
                <!-- outlines appointments for selected user,
                button to bring up new appointment interface
                or to delete an appointment -->
            </div>

        </div>
    </body>
</html>
