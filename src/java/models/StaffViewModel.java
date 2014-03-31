/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import control.Database;
import models.User.UserType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Ling
 */
public class StaffViewModel {
    
    User staff;
    JSONArray patients;
    JSONArray doctors;
    
    public StaffViewModel(User staff){
        this.staff = staff;
    }
    
    public String formatUserList(){
        patients = Database.getPatients();
        doctors = Database.getDoctors();
        
        //init table, as per FooTable plugin with expandable rows
        //details about a visit are hidden unless the row is expanded
        String formattedList = "Search users : <input id='dfilter' type='text' />"+
                                "<table class='footable vhalftable dlist'data-filter-minimum='1' data-filter='#dfilter' data-page-navigation='.pagination'>" 
                +"<thead><tr><th data-toggle='true'> ID </th> <th> First Name </th> <th> Last Name </th> <th> Type </th> "
                +"</tr></thead>";
        String tmp;
        
        //iterate over all doctors and output rows
        for(int i = 0; i < doctors.size();i++){
            JSONObject p = (JSONObject)doctors.get(i);
            
            //formats the non-details data
            tmp = "<tr class='userrow'><td class='doctor_id'>%s</td> <td> %s </td> <td> %s </td> <td>DOCTOR</td></tr>";
            formattedList += String.format(tmp, 
                    p.get("eid"),
                    p.get("fname"), 
                    p.get("lname"));

        }
        
        //iterate over all patients and output rows
        for(int i = 0; i < patients.size();i++){
            JSONObject p = (JSONObject)patients.get(i);
            
            //formats the non-details data
            tmp = "<tr class='userrow'><td class='patient_id'>%s</td> <td> %s </td> <td> %s </td> <td>PATIENT</td></tr>";
            formattedList += String.format(tmp, 
                    p.get("pid"),
                    p.get("fname"), 
                    p.get("lname"));

        }
        
        formattedList += "<tfoot class='hide-if-no-paging'><tr><td colspan='4'>" +
 		"<div class='pagination pagination-centered'></div></td></tr></tfoot>";
        
        formattedList += "</table>";
        return formattedList;
    }
    
    public String formatInfo(boolean dynamic, int eid, int id, User.UserType type) {
        if (!dynamic)
        {
            return "";
        }
        else
        {
            String summary = "";
            if (type == User.UserType.DOCTOR)
            {
                //doctor summary
                String tmp;
                tmp = "<div class='patientList'> <p> Active patients: </p> %s </div>";
                    summary += String.format(tmp, formatPatientsList(id));
                    summary += "</div>";
            }
            else if (type == User.UserType.PATIENT)
            {
                JSONObject patient = Database.getPatientByStaff(eid, id);
                
                if (patient.isEmpty())
                {
                    //not patient related to superior
                    summary += "<p><a href=\"#\" onclick=\"javascript:window.open('AssignDoctorServlet?patient=" + id + "', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title='Doctor Assignment')\" title=\"Doctor Assignment\"> Assign Doctor </a></p>";
                    summary += "<p><a href=\"#\" onclick=\"javascript:window.open('BookAppointmentServlet?patient="+ id +"', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title=' Book Appointment ')\" title=\" Book Appointment \">Book Appointment</a></p>";
                }
                else
                {
                    //patient summary
                    String tmp;

                    summary += " <h2> Patient summary: </h2> <div class='patient_summary'>";
                    tmp = "<h2> %s %s </h2>";

                    summary += String.format(tmp, patient.get("fname"), patient.get("lname"));

                    //Gets the next scheduled appointment, if it exists
                    tmp = "<p> Next Appointment: %s </p>";
                    JSONObject appt =  Database.getSeqPatientVisit(id, true);
                    if(appt.get("min(visit_date)") != null)
                       summary += String.format(tmp, appt.get("min(visit_date)").toString());
                    else
                       summary += String.format(tmp, "No scheduled appointments");

                    //Gets the last scheduled appointment, if it exists
                    tmp = "<p> Last Visit: %s </p>";
                    appt =  Database.getSeqPatientVisit(id, false);
                    if(appt.get("max(visit_date)") != null)
                       summary += String.format(tmp, appt.get("max(visit_date)").toString());
                    else
                       summary += String.format(tmp, "No past appointments");

                    tmp = "<p> Current health: %s </p>";
                    summary += String.format(tmp, patient.get("current_health"));

                    tmp = "<div class='prescriptionlist'> <p> Active prescriptions: </p> %s </div>";
                    summary += String.format(tmp, formatPrescriptionTable(id));
                    summary += "</div>";
                    
                    summary += "<p><a href=\"#\" onclick=\"javascript:window.open('AssignDoctorServlet?patient=" + id + "', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title='Doctor Assignment')\" title=\"Doctor Assignment\"> Assign Doctor </a></p>";
                    summary += "<p><a href=\"#\" onclick=\"javascript:window.open('UpdatePatientInformationServlet?patient="+ id +"', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title=' Update Patient Information ')\" title=\" Update Patient Information \"> Update Patient Information </a></p>";
                    summary += "<p><a href=\"#\" onclick=\"javascript:window.open('BookAppointmentServlet?patient="+ id +"', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title=' Book Appointment ')\" title=\" Book Appointment \">Book Appointment</a></p>";
                }
            }
             
             return summary;
        }
    }
    
    /**
     * Generates a table of prescriptions for this patient
     * @param onlyValid , if true, will only include non-expired prescriptions
     * @return formattedList, a table of prescriptions and dates that they expire
     */
    public String formatPrescriptionTable(int pid){
        //gets an array of all prescriptions for a given patient
        //if onlyValid == true, this list only contains non-expired prescriptions
        JSONArray pl = Database.getPrescriptionsByPatient(pid, true);
        String formattedList = "<table class='footable dynamicTable'><thead><tr> <th> Prescription </th> <th> Expires </th></td></tr></thead>";
        String tmp;
        
        //iterates over all prescriptions and adds rows for each one
        for(int i = 0; i < pl.size();i++){
            JSONArray p = (JSONArray)pl.get(i);
            for(int n = 0; n < p.size(); n++){
                tmp = "<tr><td> %s </td> <td> %s </td></tr>";
                formattedList += String.format(tmp, 
                        ((JSONObject)p.get(n)).get("drug_name"), 
                        ((JSONObject)p.get(n)).get("expires"));
            }
        }
        formattedList += "</table>";
        return formattedList;
    }
    
    public String formatPatientsList(int eid){        
        String formattedList = "";
        
        formattedList += "<table id='patientslist' class='footable dynamicTable vhalftable dlist'data-filter-minimum='1' data-filter='#dfilter' data-page-navigation='.pagination'>";
        
        formattedList += "<thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th></thead>";
        
        /*formattedList += formatPatientsListFilters(isPatientsList);*/
        
        JSONArray rows = buildPatientsListRows(new JSONObject(),  eid);
        
        for(int i = 0; i < rows.size(); i++){
            formattedList += rows.get(i);
        }
        
        formattedList += "</table>";
        return formattedList;
    }
    
    public JSONArray buildPatientsListRows(JSONObject filters, int eid){
        JSONArray patients = new JSONArray();
        
        patients = Database.getPatients(eid);
        
        JSONArray formattedRows = new JSONArray();
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i); 
            
            String formattedRow = "";
            
            formattedRow += "<tr>";
            
            formattedRow += String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"));
                    
            formattedRows.add(formattedRow);
        }
        
        return formattedRows;
    }
    
    public String formatAppointments(boolean dynamic, int eid, int id, User.UserType type) {
        if (!dynamic)
        {
            return "";
        }
        else
        {
            String summary = "";
            if (type == User.UserType.DOCTOR)
            {
                //doctor summary
                String tmp;

                tmp = "<div class='appointmentlist'> <p> Appointments: </p> %s </div>";
                summary += String.format(tmp, formatAppointmentTable(id, type));
                summary += "</div>";
            }
            else if (type == User.UserType.PATIENT)
            {
                JSONObject patient = Database.getPatientByStaff(eid, id);
                
                if (patient.isEmpty())
                {
                    //not patient related to superior
                }
                else
                {
                    //patient summary
                    String tmp;

                    tmp = "<div class='appointmentlist'> <p> Appointments: </p> %s </div>";
                    summary += String.format(tmp, formatAppointmentTable(id, type));
                    summary += "</div>";
                }
            }
             
             return summary;
        }
    }
    
    public String formatAppointmentTable(int id, UserType type){
        
        JSONArray appointments;
        
        if (type == UserType.DOCTOR){
            appointments = Database.getAppointmentByDoctor(id);
            String formattedList = "<table class='footable dynamicTable'><thead><tr> <th> Appointment ID </th> <th> Appointment Date </th> <th> Appointment Start Time </th> <th> Appointment End Time </th><th> Patient </th> <th> Action </th></td></tr></thead>";
            String tmp;

            //iterates over all prescriptions and adds rows for each one
            for(int i = 0; i < appointments.size();i++){
                tmp = "<tr><td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> <a href=\"#\" onclick=\"javascript:window.open('EditAppointmentServlet?patient=%s&visit_id=%s&visit_date=%s&visit_start_time=%s&visit_end_time=%s&eid=%s', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title=' Edit Appointment ')\" title=\" Edit Appointment \">Edit Appointment</a> </td></tr>";
                formattedList += String.format(tmp,
                        ((JSONObject)appointments.get(i)).get("visit_id"), 
                        ((JSONObject)appointments.get(i)).get("visit_date"), 
                        ((JSONObject)appointments.get(i)).get("visit_start_time"),
                        ((JSONObject)appointments.get(i)).get("visit_end_time"),
                        ((JSONObject)appointments.get(i)).get("pid"),
                        ((JSONObject)appointments.get(i)).get("pid"),
                        ((JSONObject)appointments.get(i)).get("visit_id"), 
                        ((JSONObject)appointments.get(i)).get("visit_date"), 
                        ((JSONObject)appointments.get(i)).get("visit_start_time"),
                        ((JSONObject)appointments.get(i)).get("visit_end_time"),
                        id
                        );
            }
            formattedList += "</table>";
            return formattedList;
        }
        else
        {
            appointments = Database.getAppointmentByPatient(id);
            String formattedList = "<table class='footable dynamicTable'><thead><tr> <th> Appointment ID </th> <th> Appointment Date </th> <th> Appointment Start Time </th> <th> Appointment End Time </th><th> Assigned Physician </th> <th> Action </th></td></tr></thead>";
            String tmp;

            //iterates over all prescriptions and adds rows for each one
            for(int i = 0; i < appointments.size();i++){
                tmp = "<tr><td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> <a href=\"#\" onclick=\"javascript:window.open('EditAppointmentServlet?patient="+ id +"&visit_id=%s&visit_date=%s&visit_start_time=%s&visit_end_time=%s&eid=%s', '_blank', 'scrollbars=0, resizeable=0, height=550, width=700', title=' Edit Appointment ')\" title=\" Edit Appointment \">Edit Appointment</a> </td></tr>";
                formattedList += String.format(tmp, 
                        ((JSONObject)appointments.get(i)).get("visit_id"), 
                        ((JSONObject)appointments.get(i)).get("visit_date"), 
                        ((JSONObject)appointments.get(i)).get("visit_start_time"),
                        ((JSONObject)appointments.get(i)).get("visit_end_time"),
                        ((JSONObject)appointments.get(i)).get("eid"),
                        ((JSONObject)appointments.get(i)).get("visit_id"), 
                        ((JSONObject)appointments.get(i)).get("visit_date"), 
                        ((JSONObject)appointments.get(i)).get("visit_start_time"),
                        ((JSONObject)appointments.get(i)).get("visit_end_time"),
                        ((JSONObject)appointments.get(i)).get("eid")
                        );
            }
            formattedList += "</table>";
            return formattedList;
        }
    }
    
    
}
