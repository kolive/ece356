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
 * @author Kyle
 */
public class PatientViewModel {
    
    User patient;
    
    public PatientViewModel(User patient){
        this.patient = patient;
    }
    
    public String formatSummary(){
        /**
            
            h2 Welcome {fname}, /h2   
            h2 Your summary: /h2
            div class=patient_summary
                p Last Visit: {lastVisitDate} /p
                p Next Appointment: {nextVisitDate} /p
                p Your Current Health: {currentHealth} /p
                div class=prescriptions
                  p Active Prescriptions: /p 
                     {prescription list}
                
         **/
         String summary = "";
         String tmp;
         
         tmp = "<h2> Welcome %s, </h2>";
         summary += String.format(tmp, patient.getStringParam("fname"));
         summary += " <h2> Your summary: </h2> <div class='patient_summary'>";
         
         tmp = "<p> Next Appointment: %s </p>";
         summary += String.format(tmp, 
                 Database.getSeqPatientVisit(Integer.parseInt(patient.getStringParam("pid")), true)
                         .get("min(visit_date)").toString()
         );
         
         tmp = "<p> Last Visit: %s </p>";
         summary += String.format(tmp, 
                 Database.getSeqPatientVisit(Integer.parseInt(patient.getStringParam("pid")), false)
                         .get("max(visit_date)").toString()
         );
         
         tmp = "<p> Your current health: %s </p>";
         summary += String.format(tmp, patient.getStringParam("current_health"));
         
         summary += "<div class='prescriptionlist'> <p> Active prescriptions: </p> ";
         summary += formatPrescriptionTable(true);
         summary += "</div></div>";
         return summary;
    }
    
    public String formatPrescriptionTable(boolean onlyValid){
        JSONArray pl = Database.getPrescriptions(Integer.parseInt(patient.getStringParam("pid")), onlyValid);
        System.out.println(pl.toString());
        String formattedList = "<table><tr> <th> Prescription </th> <th> Expires </th></td>";
        String tmp;
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
    
    public String formatVisitHistoryTable(){
        return "";        
    }
    
    public String formatVisitDetails(){
         return ""; 
    }
    
    public String formatPersonalDetails(){
         return ""; 
    }
    
    
}
