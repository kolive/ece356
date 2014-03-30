<%-- 
    Document   : patient
    Created on : Mar 11, 2014, 10:21:51 AM
    Author     : Kyle
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>
<!DOCTYPE html>
<html>
    <head>
         <jsp:useBean id="patientVM" type="models.PatientViewModel" scope="session" />
         <jsp:useBean id="user" type="models.User" scope="session" />
         <%= patientVM.updatePatientInfo() %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script src="js/footable.js" type="text/javascript"></script>
        <link href="css/footable.core.css" rel="stylesheet" type="text/css" />
        <link href="css/footable.metro.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <script type="text/javascript">
        $(function () {

            $( document ).ready(function() { 
                $('table').footable() 
            });

        });
        </script>
        <title>Patient Homepage</title>
    </head>
    <body class="backgreen">
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
               
       
        <div class="nav"> 
            <p> Logged in as: <%= user.getStringParam("fname") %> <%= user.getStringParam("lname") %> </p>
            <a href="LogoutServlet"> Log Out </a>
        </div>
        <div class="view-container rounded backteal">
            <div class="patient-container">
                <div class="status">
                    <!-- Outlines active prescriptions & diagnoses, next appt -->
                    <%= patientVM.formatSummary() %>    
                </div>
                <div class="personal">
                    <!-- outlines personal info, links to change it -->
                    <%= patientVM.formatPersonalDetails() %>
                    <p><a href="#" onclick="javascript:window.open('UpdatePatientServlet?password=false', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title='Modify Personal Details')" title="Modify Personal Details">
                        Modify Personal Details
                    </a></p>
                    <p><a href="#" onclick="javascript:window.open('UpdatePatientServlet?password=true', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title='Change Password')" title="Change Password">
                        Change Password Details
                    </a></p>
                </div>
            </div>
            <div class="history">
                <h2> Visit History: </h2>
                <!-- outlines visit history, selecting a row will show
                details in the details section -->
                <%= patientVM.formatVisitHistoryTable() %>
            </div>
            
            
        </div>
    </body>
</html>
