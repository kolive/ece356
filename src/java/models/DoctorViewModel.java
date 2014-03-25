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
                "<td><input type='text' id='patient-lstvst-filter'></td>";
        }    
        else{
            return 
                "<tr id='patient-filter-row'>" +        
                "<td><input type='text' id='advisee-pid-filter'></td>" +
                "<td><input type='text' id='advisee-fname-filter'></td>" + 
                "<td><input type='text' id='advisee-lname-filter'></td>" +
                "<td><input type='text' id='advisee-curhlth-filter'></td>" +
                "<td><input type='text' id='advisee-lstvst-filter'></td>";
        }                    
    }
    
    public String formatPatientsList(boolean isAdviseesList){
        JSONArray patients = m_patients;
        
        if(isAdviseesList){
            patients = m_advisees;
        }
        
        String formattedList = "<table><thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th><th>Last Visit</th><thead>";
        
        formattedList += this.formatPatientsListFilters(isAdviseesList);
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i); 
            
            formattedList += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"),
                                p.get("last_visit"));
        }
        
        formattedList += "</table>";
        
        return formattedList;
    }
    
    // TODO: bool param to switch on patients/advisees?
    public String formatPatientDetails(){
        if(m_currentPatient.isEmpty()){
            return "";
        }
        
        String details = "<h2>Patient Details:</h2>)";
        
        if(this.currentPatientIsAdvisee()){
            details = "<h2>Advisee Details:</h2>";
        }
        
        details = String.format(
                        "<p>Name: %s %s</p>",
                        m_currentPatient.get("fname"),
                        m_currentPatient.get("lname")
                    );
        
        details += String.format(
                        "<p>SIN: %s</p><p>ID Number: %s</p>",
                        m_currentPatient.get("sin"),
                        m_currentPatient.get("pid")
                    );
        
        // TODO: Number of visits?
     
        
        return details;
    }
    
    // TODO: bool param to switch on patients/advisees?
    public String formatPatientVisitsTable(){
        if(m_currentPatient.isEmpty()){
            return "";
        }
        
        int patientId = Integer.parseInt(m_currentPatient.get("pid").toString());
                
        JSONArray visits = new JSONArray();
        
        if(!currentPatientIsAdvisee()){
            visits = Database.getPatientVisitsForDoctor(patientId, getDoctorId());
        }
        else{
            visits = Database.getAdviseeVisitsForDoctor(patientId, getDoctorId());
        }
        
        //TODO: Comments section
        
        String formatted = 
                "<table id='visits' class='footable table-bordered toggle-circle toggle-small'>" +
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
