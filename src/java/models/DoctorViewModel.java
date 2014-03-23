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
    
    public DoctorViewModel(User doctor){  
        m_doctor = doctor;
    }
    
    public String formatPatientsListFilter(){
        return "";
    }
    
    public String formatPatientsList(){
        JSONArray patients = Database.getPatients(Integer.parseInt(m_doctor.getStringParam("eid")));
        
        String formattedList = "<table><thead><tr><th>First Name</th><th>Last Name</th><th>Current Health</th><thead>";
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i);
            
            
            formattedList += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><tr>",
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"));
        }
        
        formattedList += "</table>";
        
        return formattedList;
    }
    
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
    
    public String formatPatientVisitsTable(int patientId, int visitId){       
       return "";
    }
}
