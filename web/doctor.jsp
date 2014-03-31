<%-- 
    Document   : doctor
    Created on : 23-Mar-2014, 4:34:14 PM
    Author     : Herbert
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:useBean id="doctorVM" type="models.DoctorViewModel" scope="session" />
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
                $('tr').click(function(){
                    $('.dselected').removeClass('dselected');
                    $(this).addClass('dselected');
                
                });
                $('table').footable();
                $('.patientrow').click(pClickHandler); 
                $('.adviseerow').click(aClickHandler);
                
                $('#patient-lastvisitstart-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2000 00:00:00"));
                $('#patient-lastvisitend-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2100 00:00:00"));
                
                $('#patient-pid-filter').change(patientsFilterChange).val("");
                $('#patient-fname-filter').change(patientsFilterChange).val("");
                $('#patient-lname-filter').change(patientsFilterChange).val("");
                $('#patient-currenthealth-filter').change(patientsFilterChange).val("");
                $('#patient-lastvisitstart-filter').change(patientsFilterChange);
                $('#patient-lastvisitend-filter').change(patientsFilterChange);     
                
                $('#advisee-lastvisitstart-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2000 00:00:00"));
                $('#advisee-lastvisitend-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2100 00:00:00"));
                
                $('#advisee-pid-filter').change(adviseesFilterChange).val("");
                $('#advisee-fname-filter').change(adviseesFilterChange).val("");
                $('#advisee-lname-filter').change(adviseesFilterChange).val("");
                $('#advisee-currenthealth-filter').change(adviseesFilterChange).val("");
                $('#advisee-lastvisitstart-filter').change(adviseesFilterChange);
                $('#advisee-lastvisitend-filter').change(adviseesFilterChange);
                
                $('#visits-datestart-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2000 00:00:00"));
                $('#visits-dateend-filter').datepicker({dateFormat: 'yy-mm-dd', changeYear: true}).datepicker('setDate', new Date("January 1, 2100 00:00:00"));
                
                $('#visits-filter').trigger("footable_expand_first_row");
                
                $('#visits-visitNum-filter').change(visitFilterChange).val("");
                $('#visits-datestart-filter').change(visitFilterChange);
                $('#visits-dateend-filter').change(visitFilterChange);
                $('#visits-doctor-filter').change(visitFilterChange).val("");
                
                $('.visits-starttimestart-filter').change(visitFilterChange).val("00:00:00");
                $('.visits-starttimeend-filter').change(visitFilterChange).val("23:59:59");
                $('.visits-endtimestart-filter').change(visitFilterChange).val("00:00:00");
                $('.visits-endtimeend-filter').change(visitFilterChange).val("23:59:59");
                $('.visits-procedures-filter').change(visitFilterChange).val("");
                $('.visits-diagnoses-filter').change(visitFilterChange).val("");
                $('.visits-prescriptions-filter').change(visitFilterChange).val("");
                $('.visits-comments-filter').change(visitFilterChange).val("");
                
                $( '#tabs' ).tabs();
            });

            var pClickHandler = function() {
                var patientId = $(this).find('td:first').text();
                
                $.ajax({
                   type: 'POST',
                   url: '/ece356/DoctorServlet',
                   data: {
                       type: 'PatientRequest', 
                       patientId : patientId
                   }
                }).done(function(msg){
                    $('.patientdetails').html(msg);
                });
                
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'VisitRequest', 
                        patientId: patientId,
                        isPatient: true
                    }
                }).done(function(rows){
                    var visitsList = $('#visitstable').data('footable');
                    
                    $('#visitstable').data("isPatient", true);
                    $('#visitstable').data("patientId", patientId);
                    
                    $('.visitrow').each(function() {
                        visitsList.removeRow(this);
                    });
                    
                    $(rows).each(function(){
                        visitsList.appendRow(this);
                    });
                    
                    $('#visitsList th').each(function() {
                       $(this).show(); 
                    });
                    
                    $('#visitsList td').each(function() {
                        $(this).show();
                    });
                });
                
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'NewVisitLink',
                        patientId: patientId
                    }
                }).done(function(html){
                    $('.new-visit').html(html);
                })
            };

            var aClickHandler = function() {
                var patientId = $(this).find('td:first').text();
                
                $.ajax({
                   type: 'POST',
                   url: '/ece356/DoctorServlet',
                   data: {
                       type: 'PatientRequest', 
                       patientId : patientId
                   }
                }).done(function(msg){
                    $('.patientdetails').html(msg);
                });
                
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'VisitRequest', 
                        patientId: patientId,
                        isPatient: false
                    }
                }).done(function(rows){
                    var visitsList = $('#visitstable').data('footable');
                    
                    $('#visitstable').data("isPatient", false);
                    $('#visitstable').data("patientId", patientId);
                    
                    $('.visitrow').each(function() {
                        visitsList.removeRow(this);
                    });
                    
                    $(rows).each(function(){
                        visitsList.appendRow(this);
                    });
                    
                    $('#visitsList th').each(function() {
                       $(this).show(); 
                    });
                    
                    $('#visitsList td').each(function() {
                        $(this).show();
                    });
                });
                
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'NewVisitLink',
                        patientId: patientId
                    }
                }).done(function(html){
                    $('.new-visit').html(html);
                })
            };
            
            var patientsFilterChange = function() {                
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'PatientFilter',
                        pid: $('#patient-pid-filter').val(),
                        fname: $('#patient-fname-filter').val(),
                        lname: $('#patient-lname-filter').val(),
                        current_health: $('#patient-currenthealth-filter').val(),
                        last_visit_start: $('#patient-lastvisitstart-filter').val(),
                        last_visit_end: $('#patient-lastvisitend-filter').val(),
                        isPatientsList: true
                    }
                }).done(function(rows){
                    var patientsList = $('#patientslist').data('footable');
                    
                    $('.patientrow').each(function() {
                        patientsList.removeRow(this);
                    });
                    
                    $(rows).each(function(){
                        patientsList.appendRow(this);
                    });
                    
                    $('#patientslist th').each(function() {
                       $(this).show(); 
                    });
                    
                    $('#patientslist td').each(function() {
                        $(this).show();
                    });
                    
                    $('.patientrow').click(pClickHandler);
                });
            };
            
            var adviseesFilterChange = function() {
                $.ajax({
                    type: 'POST',
                    url: '/ece356/DoctorServlet',
                    data: {
                        type: 'PatientFilter',
                        pid: $('#advisee-pid-filter').val(),
                        fname: $('#advisee-fname-filter').val(),
                        lname: $('#advisee-lname-filter').val(),
                        current_health: $('#advisee-currenthealth-filter').val(),
                        last_visit_start: $('#advisee-lastvisitstart-filter').val(),
                        last_visit_end: $('#advisee-lastvisitend-filter').val(),
                        isPatientsList: false
                    }
                }).done(function(rows){
                    var patientsList = $('#adviseeslist').data('footable');
                    
                    $('.adviseerow').each(function() {
                        patientsList.removeRow(this);
                    });
                    
                    $(rows).each(function(){
                        patientsList.appendRow(this);
                    });
                    
                    $('#adviseeslist th').each(function() {
                       $(this).show(); 
                    });
                    
                    $('#adviseeslist td').each(function() {
                        $(this).show();
                    });
                    
                    $('.adviseerow').click(aClickHandler); 
                });
            }
            
            var visitFilterChange = function() {                
                $.ajax({
                   type: 'POST',
                   url: '/ece356/DoctorServlet',
                   data: {
                       type: 'VisitsFilter',
                       patientId: $('#visitstable').data("patientId"),
                       visitNum: $('#visits-visitNum-filter').val(),
                       dateStart: $('#visits-datestart-filter').val(),
                       dateEnd: $('#visits-dateend-filter').val(),
                       doctor: $('#visits-doctor-filter').val(),
                       starttimestart: $($('.visits-starttimestart-filter')[1]).val(),
                       starttimeend: $($('.visits-starttimeend-filter')[1]).val(),
                       endtimestart: $($('.visits-endtimestart-filter')[1]).val(),
                       endtimeend: $($('.visits-endtimeend-filter')[1]).val(),
                       procedures: $($('.visits-procedures-filter')[1]).val(),
                       diagnoses: $($('.visits-diagnoses-filter')[1]).val(),
                       prescriptions: $($('.visits-prescriptions-filter')[1]).val(),
                       comments: $($('.visits-comments-filter')[1]).val(),
                       isPatient: $('#visitstable').data("isPatient")
                   }
                }).done(function(rows){
                    var visitsList = $('#visitstable').data('footable');
                    
                    $('.visitrow').each(function() {
                        visitsList.removeRow(this);
                    });
                    
                    $(rows).each(function(){
                        visitsList.appendRow(this);
                    });
                });
            }
            
        });
        </script>
        <title>Doctor Homepage</title>
    </head>
    <body class="backgreen">
        <div class="nav"> 
            <p> Logged in as: <%= user.getStringParam("fname") %> <%= user.getStringParam("lname") %> </p>
            <a href="LogoutServlet"> Log Out </a>
        </div>
        <div class="view-container rounded backteal">            
            <div class="two-thirds patient-container" id="tabs">
                <ul>
                    <li> <a href="#tabs1"> All Patients </a> </li>
                    <li> <a href="#tabs2"> Advisee Patients </a> </li>
                </ul>
                <div class="two-thirds patients-list-container" id="tabs1">
                    <div class="patients-list-filter">
                        <%= doctorVM.formatPatientsListFilters(true) %>
                    </div>
                    <div class="patients-list">
                        <%= doctorVM.formatPatientsList(true) %>
                    </div>
                </div>
                <div class="two-thirds advisees-list-container" id="tabs2">
                    <div class="advisees-list-filter">
                        <%= doctorVM.formatPatientsListFilters(false) %>
                    </div>
                    <div class="advisees-list">
                        <%= doctorVM.formatPatientsList(false) %>
                    </div>
                </div>
            </div>
            <div class="patient-details">
                <%= doctorVM.formatPatientDetails(-1) %>
                <div class="new-visit">
                </div>
            </div>
            <div class="patient-visits-container">
                <div class="patient-visits-filter">
                    <%= doctorVM.formatPatientVisitsFilter() %>
                </div>
                <div class="patient-visits-table">
                    <%= doctorVM.formatPatientVisitsTable(-1, false) %>
                </div>
            </div>
        </div>
    </body>
</html>