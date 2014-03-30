<%-- 
    Document   : patient
    Created on : Mar 11, 2014, 10:21:51 AM
    Author     : Kyle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:useBean id="financeVM" type="models.FinanceViewModel" scope="session" />
        <jsp:useBean id="user" type="models.User" scope="session" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
                $('table').footable();
                $('.doctorrow').click(dClickHandler);
                
                $( '#date1' ).datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2000 00:00:00"));
                $( '#date2' ).datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2100 00:00:00"));
            });
            
            var dClickHandler = function(){
                $('.doctorrow').removeClass('dselected');
                $(this).addClass('dselected');
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'PatientRequest', 
                                dId : $(this).find('td:first').text(),
                                date1 : $("#date1").val(),
                                date2 : $("#date2").val()
                               }
                        
                }).done(function(msg){
                    $('.patlist tbody').html(msg).trigger('footable_initialize');
                    //clear visit list
                    $('.vlist tbody').html('');
                    //refresh events
                    $('.patientrow').click(pClickHandler);
                });
                
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'DoctorDetailRequest', dId : $(this).find('td:first').text() }
                        
                }).done(function(msg){
                    $('.ddetails').html(msg);
                    //clear visit and patient details
                    $('.vdetails').html('<h2> Visit Details </h2>');
                    $('.pdetails').html('<h2> Patient Summary </h2>');
                    //refresh events
                });
            };
            
            var pClickHandler = function() {
                $('.patientrow').removeClass('pselected');
                $(this).addClass('pselected');
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'VisitRequest', 
                                pId : $(this).find('td:first').text(), 
                                dId : $('.dselected').find('td:first').text(),
                                date1 : $("#date1").val(),
                                date2 : $("#date2").val() }
                        
                }).done(function(msg){
                    $('.vlist tbody').html(msg).trigger('footable_initialize');
                    $('.visitrow').click(vClickHandler);
                });
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'PatientDetailRequest', 
                                pId : $(this).find('td:first').text()
                              }
                        
                }).done(function(msg){
                    $('.pdetails').html(msg);
                    $('.vdetails').html('<h2> Visit Details </h2>');
                });
            };
            
            var vClickHandler = function() {
                $('.visitrow').removeClass('vselected');
                $(this).addClass('vselected');
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'VisitDetailRequest', 
                                vId : $(this).find('td:first').text() }
                        
                }).done(function(msg){
                    $('.vdetails').html(msg);
                });
            };

        });
        </script>
        <title>Financial Officer Homepage</title>
    </head>
    <body class="backgreen">
        <!--
        ____________________________________
        |           |           |          |
        | d_select  |  p_select | v_select |
        |___________|___________|__________|
        |           |           |          |
        | d_summary | p_summary | v_summary|
        |___________|___________|__________|
        
        -->
               
       
        <div class="nav"> 
            <p> Logged in as: <%= user.getStringParam("fname") %> <%= user.getStringParam("lname") %> </p>
            <a href="LogoutServlet"> Log Out </a>
        </div>
        <div class="view-container rounded backteal">
            <h1 class="third"> Welcome, <%= user.getStringParam("fname") %> </h1>
             <div class="third" style='vertical-align:bottom'> 
                Show only Patients with visits between dates: <br/> <input id="date1" type="text">
                and <input id="date2" type="text">
            </div>
            <div class="finance-container">
                <div class="vhalf">
                    <div class="third">
                        <%= financeVM.formatDoctorList() %>
                    </div>
                    <div class="third"> 
                       
                        <%= financeVM.formatPatientList(-1, false, "2000-01-01", "2100-01-01") %>
                    </div>
                    <div class="third">
                        <%= financeVM.formatVisitList(-1,-1,false, "2000-01-01", "2100-01-01" ) %>
                    </div>
                </div>
                <div class="vhalf">
                    <div class="third ddetails">
                        <h2> Doctor Summary </h2>
                    </div>
                    <div class="third">
                        <div class='pdetails'>
                            <h2> Patient Summary </h2>
                        </div>
                        
                    </div>
                    <div class="third vdetails">
                        <h2> Visit Details </h2>
                    </div>
                </div>
                
                
            </div> 
        </div>
    </body>
</html>
