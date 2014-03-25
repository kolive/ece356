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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script src="js/footable.js" type="text/javascript"></script>
        <script src="js/footable.paginate.js" type="text/javascript"></script>
        <link href="css/footable.core.css" rel="stylesheet" type="text/css" />
        <link href="css/footable.metro.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <script type="text/javascript">
        $(function () {

            $( document ).ready(function() { 
                $('table').footable();
                $('.doctorrow').click(dClickHandler);
            });
            
            var dClickHandler = function(){
                $('.doctorrow').removeClass('dselected');
                $(this).addClass('dselected');
                $.ajax({
                        type : 'POST',
                        url : '/ece356/FinanceServlet',
                        data: { type: 'PatientRequest', dId : $(this).find('td:first').text() }
                        
                }).done(function(msg){
                    $('.patlist tbody').html(msg).trigger('footable_initialize');
                    //clear visit list
                    $('.vlist tbody').html('');
                    //refresh events
                    $('.patientrow').click(pClickHandler);
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
                                dId : $('.dselected').find('td:first').text() }
                        
                }).done(function(msg){
                    $('.vlist tbody').html(msg).trigger('footable_initialize');
                    $('.visitrow').click(vClickHandler);
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
                    //TODO: should update the summary div
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
               
       
    
        <div class="view-container rounded backteal">
            <div class="finance-container">
                <div class="vhalf">
                    <div class="third">
                        <%= financeVM.formatDoctorList() %>
                    </div>
                    <div class="third">
                        <%= financeVM.formatPatientList(-1, false) %>
                    </div>
                    <div class="third">
                        <%= financeVM.formatVisitList(-1,-1,false) %>
                    </div>
                </div>
                <div class="vhalf">
                    <div class="third">
                        
                    </div>
                    <div class="third">
                        
                    </div>
                    <div class="third">
                        
                    </div>
                </div>
            </div> 
        </div>
    </body>
</html>
