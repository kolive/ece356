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
         JSONObject appt =  Database.getSeqPatientVisit(Integer.parseInt(patient.getStringParam("pid")), true);
         if(appt.get("min(visit_date)") != null)
            summary += String.format(tmp, appt.get("min(visit_date)").toString());
         else
            summary += String.format(tmp, "No scheduled appointments");
         
         tmp = "<p> Last Visit: %s </p>";
         appt =  Database.getSeqPatientVisit(Integer.parseInt(patient.getStringParam("pid")), false);
         if(appt.get("max(visit_date)") != null)
            summary += String.format(tmp, appt.get("max(visit_date)").toString());
         else
            summary += String.format(tmp, "No past appointments");
         
         tmp = "<p> Your current health: %s </p>";
         summary += String.format(tmp, patient.getStringParam("current_health"));
         
         tmp = "<div class='prescriptionlist'> <p> Active prescriptions: </p> %s </div>";
         summary += String.format(tmp, formatPrescriptionTable(true));
         summary += "</div>";
         return summary;
    }
    
    public String formatPrescriptionTable(boolean onlyValid){
        JSONArray pl = Database.getPrescriptions(Integer.parseInt(patient.getStringParam("pid")), onlyValid);
        String formattedList = "<table><thead><tr> <th> Prescription </th> <th> Expires </th></td></tr></thead>";
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
        //TODO: Using the eid, get the actual doctor's name
        
        JSONArray vl = Database.getVisits(Integer.parseInt(patient.getStringParam("pid")));
        String formattedList = "<table id='visits' class='footable table-bordered toggle-circle toggle-small'>" 
                +"<thead><tr><th data-toggle='true'> Visit #</th> <th> Appointment Date </th> <th> Assigned Physician </th> "
                +"<th data-hide='all' > Start Time </th><th data-hide='all' > End Time </th>"
                +"<th data-hide='all' > Procedure Performed </th><th data-hide='all' > Diagnosis </th><th data-hide='all' > Prescription Perscribed </th></tr></thead>";
        String tmp;
        for(int i = 0; i < vl.size();i++){
            JSONObject p = (JSONObject)vl.get(i);
            
            //formats the non-details data
            tmp = "<tr><td class='visit_id'>%s</td> <td> %s </td> <td> %s </td>";
            formattedList += String.format(tmp, 
                    p.get("visit_id"),
                    p.get("visit_date"), 
                    p.get("eid"));
            
            //formats the details data
            tmp ="<td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td></tr>";
            String prescriptionSummary = "TODO";
            String diagnosisSummary = "TODO";
            String procedureSummary = "TODO";
            formattedList += String.format(tmp,
                    p.get("visit_start_time"),
                    p.get("visit_end_time"),
                    procedureSummary,
                    diagnosisSummary,
                    prescriptionSummary);

        }
        formattedList += "</table>";
        return formattedList;        
    }
    
    
    public String formatPersonalDetails(){
         String tmp;
         String personal = "";
         
         tmp = "<h2> Personal Details: </h2> <p> Your name: %s %s, </p>";
         personal += String.format(tmp, patient.getStringParam("fname"), patient.getStringParam("lname"));
         
         tmp = "<p> SIN: %s </p>";
         personal += String.format(tmp, patient.getStringParam("sin"));
         
         tmp = "<p> ID Number: %s </p>";
         personal += String.format(tmp, patient.getStringParam("pid"));
         
         tmp = "<p>  </p>";
         tmp += "<table class='address'><thead><tr> <th> Address: </th></tr> </thead><tr><td> %s  %s </td></tr> ";
         tmp += "<tr><td> %s </td></tr> ";
         tmp += "<tr><td> %s </td></tr></table>";
         
         personal += String.format(tmp,
                 patient.getStringParam("street_number"),
                 patient.getStringParam("street"),
                 patient.getStringParam("city"),
                 patient.getStringParam("post_code"));
         return personal;
    }
    
    public String updatePatientInfo(){
        
            //refresh user
            JSONObject userInfo = Database.userLogin(patient.getStringParam("pid"), patient.getStringParam("password"), true);
            patient.info = userInfo;
            return "";
    }
    
    
}
