<%-- 
    Document   : doctor
    Created on : 23-Mar-2014, 4:34:14 PM
    Author     : Herbert
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
         <jsp:useBean id="doctorVM" type="models.DoctorViewModel" scope="session" />
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
        <title>Doctor Homepage</title>
    </head>
    <body class="backgreen">
        <div class="view-container rounded backteal">
            <div class="patient-container">
                <div class="patients-list-container">
                    <div class="patients-list-filter">
                    </div>
                    <div class="patients-list">
                        <%= doctorVM.formatPatientsList() %>
                    </div>
                </div>
                <div class="patient-details">
                    <%= doctorVM.formatPatientDetails() %>
                </div>
            </div>
            <div class="patient-visits-container">
                <div class="patient-visits-table">
                    <%= doctorVM.formatPatientVisitsTable() %>
                </div>
            </div>
        </div>
    </body>
</html>