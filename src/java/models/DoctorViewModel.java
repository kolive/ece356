/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import control.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Herbert
 */
public class DoctorViewModel {    
    User m_doctor;
    
    JSONArray m_patients;
    JSONArray m_advisees;
    
    public DoctorViewModel(User doctor){  
        m_doctor = doctor;
        
        m_patients = Database.getPatients(getDoctorId());
        m_advisees = Database.getAdvisees(getDoctorId());
    }
    
    private int getDoctorId(){
        return Integer.parseInt(m_doctor.getStringParam("eid"));
    }
    
    public String formatPatientsListFilter(){
        return "";
    }
    
    // TODO: bool param to switch on patients/advisees?
    public String formatPatientsList(){
        JSONArray patients = m_patients;
        
        String formattedList = "<table><thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th><th>Last Visit</th><thead>";
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i);
            
            
            formattedList += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"),
                                p.get("last_visit"));
        }
        
        formattedList += "</table>";
        
        return formattedList;
    }
    
    public String formatAdviseesList(){
        JSONArray advisees = m_advisees;
        
        String formattedList = "<table><thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Current Health</th><th>Last Visit</th><thead>";
        
        for(int i = 0; i < advisees.size(); i++){
            JSONObject p = (JSONObject) advisees.get(i);
            
            formattedList += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><tr>",
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
    public String formatPatientDetails(int patientId){
        String details = "";
        
        JSONObject patient = Database.getPatient(patientId);
        
        details += String.format("<h2>Patient Details:</h2><p>Name: %s %s</p>",
                        patient.get("fname"),
                        patient.get("lname"));
        
        details += String.format("<p>SIN: %s</p><p>ID Number: %s</p>",
                        patient.get("sin"),
                        patient.get("pid"));
        
        // TODO: Number of visits?
     
        
        return details;
    }
    
    // TODO: bool param to switch on patients/advisees?
    public String formatPatientVisitsTable(int patientId){
        JSONArray patientVisits = Database.getPatientVisitsForDoctor(patientId, getDoctorId());
        
        String formattedTable = "";
        
        for(int i = 0; i < patientVisits.size(); i++){
            
        }
        
        return formattedTable;
    }
}
