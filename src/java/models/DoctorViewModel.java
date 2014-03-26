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
    
    JSONObject m_currentPatient;
    
    JSONArray m_patients;
    JSONArray m_advisees;
    
    public DoctorViewModel(User doctor){  
        m_doctor = doctor;
        
        m_patients = Database.getPatients(getDoctorId());
        m_advisees = Database.getAdvisees(getDoctorId());
        
        m_currentPatient = new JSONObject();
    }
    
    private int getDoctorId(){
        return Integer.parseInt(m_doctor.getStringParam("eid"));
    }
    
    public JSONObject getCurrentPatient(){
        return m_currentPatient;
    }
    
    public void setCurrentPatient(int patientId, boolean isAdvisee){
        JSONObject currentPatient = Database.getPatient(patientId);
        currentPatient.put("isAdvisee", isAdvisee);
        
        m_currentPatient = currentPatient;
    }
    
    public boolean currentPatientIsAdvisee(){
        return Boolean.parseBoolean(m_currentPatient.get("isAdvisee").toString());
    }
    
    private String formatPatientsListFilters(boolean isAdviseesList){        
        if(!isAdviseesList){
            return 
                "<tr id='patient-filter-row'>" +        
                "<td><input type='text' id='patient-pid-filter'></td>" +
                "<td><input type='text' id='patient-fname-filter'></td>" + 
                "<td><input type='text' id='patient-lname-filter'></td>" +
                "<td><input type='text' id='patient-curhlth-filter'></td>" +
                "<td><input type='text' id='patient-lstvst-filter'></td>" +
                "</tr>";
        }    
        else{
            return 
                "<tr id='patient-filter-row'>" +        
                "<td><input type='text' id='advisee-pid-filter'></td>" +
                "<td><input type='text' id='advisee-fname-filter'></td>" + 
                "<td><input type='text' id='advisee-lname-filter'></td>" +
                "<td><input type='text' id='advisee-curhlth-filter'></td>" +
                "<td><input type='text' id='advisee-lstvst-filter'></td>" +
                "</tr>";
        }                    
    }
    
    public String formatPatientsList(boolean isAdviseesList){
        JSONArray patients = new JSONArray();
        
        if(isAdviseesList){
            patients = Database.getAdvisees(getDoctorId());
        }
        else{
            patients = Database.getPatients(getDoctorId());
        }
        
        String formattedList = "<table><thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th><th>Last Visit</th></thead>";
        
        formattedList += this.formatPatientsListFilters(isAdviseesList);
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i); 
            
            if(isAdviseesList){
                formattedList += "<tr class='adviseerow'>";
            }
            else{
                formattedList += "<tr class='patientrow'>";
            }
            
            formattedList += String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"),
                                p.get("last_visit"));
        }
        
        formattedList += "</table>";
        
        return formattedList;
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
        
        // TODO: Number of visits?
     
        details += "</div>";
        
        return details;
    }
    
    private String formatPatientVisitsFilter(){
        return 
                "<tr id='visits-filter-row'>" +
                "<td><input type='text' id='visits-visitNum-filter'></td>" +
                "<td><input type='text' id='visits-date-filter'></td>" +
                "<td><input type='text' id='visits-doctor-filter'></td>" +
                "</tr>";
    }
    
    // TODO: bool param to switch on patients/advisees?
    public String formatPatientVisitsTable(int patientId, boolean isPatient){                
        JSONArray visits = new JSONArray();
        
        if(patientId != -1){
            if(isPatient){
                visits = Database.getPatientVisitsForDoctor(patientId, getDoctorId());
            }
            else{
                visits = Database.getAdviseeVisitsForDoctor(patientId, getDoctorId());
            }
        }
        
        //TODO: Comments section
        
        String formatted = 
                "<table class='vtable footable table-bordered toggle-circle toggle-small'>" +
                "<thead><tr>" +
                "<th data-toggle='true'>Visit #</th>" +
                "<th>Appointment Date</th>" +
                "<th>Assigned Physician<th>" +
                "<th data-hide='all'>Start Time</th>" +
                "<th data-hide='all'>End Time</th>" +
                "<th data-hide='all'>Procedure Performed</th>" +
                "<th data-hide='all'>Diagnosis</th>" +
                "<th data-hide='all'>Prescriptions Perscribed</th>" +
                "</tr></thead>"; 
        
        //formatted += formatPatientVisitsFilter();
        
        for(int i = 0; i < visits.size(); i++){
            JSONObject visit = (JSONObject) visits.get(i);
            
            int visitId = Integer.parseInt(visit.get("visit_id").toString());
            
            formatted += String.format(
                                "<tr><td class='visit_id'>%s</td><td>%s</td><td>%s</td>",
                                visit.get("visit_id"),
                                visit.get("visit_date"),
                                visit.get("eid")
                            );
            
            formatted += String.format(
                                "<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td></td></tr>",
                                visit.get("visit_start_time"),
                                visit.get("visit_end_time"),
                                FormatHelper.formatProcedures(visitId),
                                FormatHelper.formatDiagnoses(visitId),
                                FormatHelper.formatPrescriptions(visitId)
                            );
        }
        
        formatted += "</table>";
        
        return formatted;
    }
}
