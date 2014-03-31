<%-- 
    Document   : staff
    Created on : Mar 11, 2014, 10:29:21 AM
    Author     : Kyle
--%>

<%@page import="models.User.UserType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:useBean id="staffVM" type="models.StaffViewModel" scope="session" />
        <jsp:useBean id="user" type="models.User" scope="session" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
        <script src="js/footable.js" type="text/javascript"></script>
        <script src="js/footable.paginate.js" type="text/javascript"></script>
        <script src="js/footable.filter.js" type="text/javascript"></script>
        <link href="css/footable.core.css" rel="stylesheet" type="text/css" />
        <link href="css/footable.metro.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/start/jquery-ui.css">
        <script type="text/javascript">
        $(function () {

            $( document ).ready(function() { 
                $.ajaxSetup({ cache: false });
                $('table').footable();
                $('.userrow').on('click.getData', clickHandler);
            });
            var clickHandler = function(){
                $('.userrow').off('click.getData', clickHandler);
                $('.userrow').removeClass('dselected');
                $(this).addClass('dselected');
                
                var dId = $(this).find('td:first').text();
                var userType = $(this).find('td:last').text();
                
                $.ajax({
                        type : 'POST',
                        url : '/ece356/StaffServlet',
                        data: { type: 'UserRequest', 
                                dId : dId,
                                userType : userType 
                               }
                        
                }).done(function(msg){
                    $('.info').html(msg);
                    $('.dynamicTable').footable();
                    $.ajax({
                            type : 'POST',
                            url : '/ece356/StaffServlet',
                            data: { type: 'AppointmentsRequest', 
                                    dId : dId,
                                    userType : userType
                                   }

                    }).done(function(msg){
                        $('.appointments').html(msg);
                        $('.dynamicTable').footable();
                        $('.userrow').on('click.getData', clickHandler);
                    });
                });
            }
        });
        </script>
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
        <div class="nav"> 
            <p> Logged in as: <%= user.getStringParam("fname") %> <%= user.getStringParam("lname") %> </p>
            <a href="LogoutServlet"> Log Out </a>
        </div>
        <div class="view-container rounded backteal">
            <p><a href="#" onclick="javascript:window.open('NewPatientServlet', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title='New Patient')" title="New Patient">
                        Add new patient
            </a></p>
            <div class="users">
                <!-- 
                   A list of doctors and patients
                   When one is selected, changes info pane
                   Button to bring up ui for adding new patient 
                   or assigning patient by id
                -->
                <%= staffVM.formatUserList() %>  
            </div>
            <div class="info">
                <!-- 
                outlines personal info, links to change it 
                when a doctor is selected, shows a list of assigned patients
                if the doctor is staff's boss, can also get record info
                -->
                <%= staffVM.formatInfo(false, 0, 0, UserType.STAFF) %>
            </div>
            <div class="appointments">
                <!-- outlines appointments for selected user,
                button to bring up new appointment interface
                or to delete an appointment -->
                <%= staffVM.formatAppointments(false, 0, 0, UserType.STAFF) %>
            </div>

        </div>
    </body>
</html>
