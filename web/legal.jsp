<%-- 
    Document   : legal
    Created on : 30-Mar-2014, 3:19:31 PM
    Author     : Steven
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:useBean id="legalVM" type="models.LegalViewModel" scope="session" />
        <jsp:useBean id="user" type="models.User" scope="session" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js" type="text/javascript"></script>
        <script src="js/footable.js" type="text/javascript"></script>
        <script src="js/footable.paginate.js" type="text/javascript"></script>
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
                
                var dClickHandler = function() {
                    $('.doctorrow').removeClass('dselected');
                    $(this).addClass('dselected');
                    $.ajax({
                            type : 'POST',
                            url : '/ece356/LegalServlet',
                            data : { type: 'PatientRequest',
                                     dId: $(this).find('td:first').text(),
                                     date1: $("#date1").val(),
                                     date2: $("#date2").val()
                                 }
                    }).done(function(msg) {
                        $('.patlist tbody').html(msg).trigger('footable_initialize');
                        $('.vlist tbody').html('');
                        $('.audit-trail').html('<h1> Visit Audit Trail for Visit ID:  </h1>');
                        $('.patientrow').click(pClickHandler);
                    });
                };
                
                var pClickHandler = function() {
                    $('.patientrow').removeClass('pselected');
                    $(this).addClass('pselected');
                    $.ajax({
                            type: 'POST',
                            url: '/ece356/LegalServlet',
                            data: { type: 'VisitRequest',
                                    pId: $(this).find('td:first').text(),
                                    dId: $('.dselected').find('td:first').text(),
                                    date1: $("#date1").val(),
                                    date2: $("#date2").val()
                            }
                    }).done(function(msg){
                        $('.vlist tbody').html(msg).trigger('footable_initialize');
                        $('.audit-trail').html('<h1> Visit Audit Trail for Visit ID:  </h1>');
                        $('.visitrow').click(vClickHandler);
                    });
                
                };
                
                var vClickHandler = function() {
                    $('.visitrow').removeClass('vselected');
                    $(this).addClass('vselected');
                    $.ajax({
                            type: 'GET',
                            url: '/ece356/VisitHistoryServlet',
                            data: { vid: $(this).find('.vid').text() }
                    }).done(function(msg){
                        $('.audit-trail').html(msg);
                    });
                };
            });
        </script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Legal Homepage</title>
    </head>
    <body class="backgreen">
        <!--
        ____________________________________
        |           |           |          |
        | d_select  |  p_select | v_select |
        |___________|___________|__________|
        |                                  |
        |             audit_log            |
        |__________________________________|
        
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
            <div class="legal-container">
                <div class="vhalf">
                    <div class="third">
                        <%= legalVM.formatDoctorList() %>
                    </div>
                    <div class="third"> 
                       
                        <%= legalVM.formatPatientList(-1, false, "2000-01-01", "2100-01-01") %>
                    </div>
                    <div class="third">
                        <%= legalVM.formatVisitList(-1,-1,false, "2000-01-01", "2100-01-01" ) %>
                    </div>
                </div>
                <div class="vhalf">
                    <div class="audit-trail">
                        <h1> Visit Audit Trail for Visit ID:  </h1>
                    </div>
                </div>
            </div> 
        </div>
    </body>
</html>
