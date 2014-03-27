/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import control.Database;
import models.Helpers.FormatHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Herbert
 */
public class DoctorViewModel {    
    User m_doctor;
    
    public DoctorViewModel(User doctor){  
        m_doctor = doctor;
    }
    
    private int getDoctorId(){
        return Integer.parseInt(m_doctor.getStringParam("eid"));
    }
    
    private String formatPatientsListFilters(boolean isPatientsList){        
        if(isPatientsList){
            return 
                "<tr id='patient-filter-row'>" +        
                "<td><input type='text' id='patient-pid-filter'></td>" +
                "<td><input type='text' id='patient-fname-filter'></td>" + 
                "<td><input type='text' id='patient-lname-filter'></td>" +
                "<td><input type='text' id='patient-currenthealth-filter'></td>" +
                "<td>Between:</br><input type='text' id='patient-lastvisitstart-filter'></br>and:</br><input type='text' id='patient-lastvisitend-filter'></td>" +
                "</tr>";
        }    
        else{
            return 
                "<tr id='patient-filter-row'>" +        
                "<td><input type='text' id='advisee-pid-filter'></td>" +
                "<td><input type='text' id='advisee-fname-filter'></td>" + 
                "<td><input type='text' id='advisee-lname-filter'></td>" +
                "<td><input type='text' id='advisee-currenthealth-filter'></td>" +
                "<td>Between:</br><input type='text' id='advisee-lastvisitstart-filter'></br>and:</br><input type='text' id='advisee-lastvisitend-filter'></td>" +
                "</tr>";
        }                    
    }
    
    public String formatPatientsList(boolean isPatientsList){        
        String formattedList = "";
        
        if(isPatientsList){
         formattedList += "<table id='patientslist'>";   
        }
        else{
            formattedList += "<table id='adviseeslist'>";
        }
        
        formattedList += "<thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th><th>Last Visit</th></thead>";
        
        formattedList += formatPatientsListFilters(isPatientsList);
        
        JSONArray rows = buildPatientsListRows(new JSONObject(), isPatientsList);
        
        for(int i = 0; i < rows.size(); i++){
            formattedList += rows.get(i);
        }
        
        formattedList += "</table>";
        return formattedList;
    }
    
    public JSONArray buildPatientsListRows(JSONObject filters, boolean isPatientsList){
        JSONArray patients = new JSONArray();
        
        if(isPatientsList){
            patients = Database.getPatients(getDoctorId(), filters);
        }
        else{
            patients = Database.getAdvisees(getDoctorId(), filters);
        }
        
        JSONArray formattedRows = new JSONArray();
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i); 
            
            String formattedRow = "";
            
            if(isPatientsList){
                formattedRow += "<tr class='patientrow' style='display: tablerow;'>";
            }
            else{
                formattedRow += "<tr class='adviseerow' style='display: table-row;'>";
            }
            
            formattedRow += String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"),
                                p.get("last_visit"));
                    
            formattedRows.add(formattedRow);
        }
        
        return formattedRows;
    }
    
    /**
     * 
     * @param patientId
     * @return Patient's details formatted 
     */
    public String formatPatientDetails(int patientId){
        
        JSONObject patient = new JSONObject();
        
        if(patientId != -1){
            patient = Database.getPatient(patientId);
        }
        else{
            // Fill details with empty strings if no patient selected
            patient.put("fname", "");
            patient.put("lname", "");
            patient.put("sin", "");
            patient.put("pid", "");
        }
        
        String details = "<div class='patientdetails'><h2>Patient Details:</h2>";
        
        details += String.format(
                        "<p>Name: %s %s</p>",
                        patient.get("fname"),
                        patient.get("lname")
                    );
        
        details += String.format(
                        "<p>SIN: %s</p><p>ID Number: %s</p>",
                        patient.get("sin"),
                        patient.get("pid")
                    );
     
        details += "</div>";
        
        return details;
    }
    
    private String formatPatientVisitsFilter(){
        return 
                "<tr id='visits-filter-row'>" +
                "<td><input type='text' id='visits-visitNum-filter'></td>" +
                "<td><input type='text' id='visits-date-filter'></td>" +
                "<td><input type='text' id='visits-doctor-filter'></td>" +
                "<td>Between <input type='text' id='visits-starttimestart-filter'> and <input type='text' id='visits-starttimeend-filter'></td>" +
                "<td>Between <input type='text' id='visits-endtimestart-filter'> and <input type='text' id='visits-endtimeend-filter'></td>" +
                "<td><input type='text' id='visits-procedures-filter'></td>" +
                "<td><input type='text' id='visits-diagnoses-filter'></td>" +
                "<td><input type='text' id='visits-prescriptions-filter'></td>" +
                "</tr>";
    }
    
    public String formatPatientVisitsTable(int patientId, boolean isPatient){                        
        //TODO: Comments section
        
        String formatted = 
                "<table class='vtable footable table-bordered toggle-circle toggle-small'>" +
                "<thead><tr>" +
                "<th data-toggle='true'>Visit #</th>" +
                "<th>Appointment Date</th>" +
                "<th>Assigned Physician</th>" +
                "<th data-hide='all'>Start Time</th>" +
                "<th data-hide='all'>End Time</th>" +
                "<th data-hide='all'>Procedure Performed</th>" +
                "<th data-hide='all'>Diagnosis</th>" +
                "<th data-hide='all'>Prescriptions Prescribed</th>" +
                "</tr></thead>"; 
        
        formatted += formatPatientVisitsFilter();
        
        JSONArray rows = buildPatientVisitsRows(patientId, isPatient, new JSONObject());
        
        for(int i = 0; i < rows.size(); i++){
            formatted += rows.get(i);
        }
        
        formatted += "</table>";
        
        return formatted;
    }
    
    public JSONArray buildPatientVisitsRows(int patientId, boolean isPatient, JSONObject filters){
        JSONArray formattedRows = new JSONArray();
        
        if(patientId != -1){
            JSONArray visits = new JSONArray();
            
            if(isPatient){
                visits = Database.getPatientVisitsForDoctor(patientId, getDoctorId(), filters);
            }
            else{
                visits = Database.getAdviseeVisitsForDoctor(patientId, getDoctorId(), filters);
            }
            
            for(int i = 0; i < visits.size(); i++){
                JSONObject visit = (JSONObject) visits.get(i);

                int visitId = Integer.parseInt(visit.get("visit_id").toString());

                String formattedRow = "<tr>";

                formattedRow += String.format(
                                    "<td class='visit_id'>%s</td><td>%s</td><td>%s</td>",
                                    visit.get("visit_id"),
                                    visit.get("visit_date"),
                                    visit.get("eid")
                                );

                formattedRow += String.format(
                                    "<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td>",
                                    visit.get("visit_start_time"),
                                    visit.get("visit_end_time"),
                                    FormatHelper.formatProcedures(visitId),
                                    FormatHelper.formatDiagnoses(visitId),
                                    FormatHelper.formatPrescriptions(visitId)
                                );
                formattedRow += "</tr>";
                
                formattedRows.add(formattedRow);
            }
        }
        
        return formattedRows;
    }
}
