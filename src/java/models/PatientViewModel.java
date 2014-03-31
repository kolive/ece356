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
 * @author Kyle
 */
public class PatientViewModel {
    
    User patient;
    
    public PatientViewModel(User patient){
        this.patient = patient;
    }
    
    /**
     * Formats information about the patient info a helpful summary
     * @return summary, an HTML formatted string
     */
    public String formatSummary(){
        /**
            
            h2 Welcome {fname}, /h2   
            h2 Your summary: /h2
            div class=patient_summary
                p Last Visit: {lastVisitDate} /p
                p Next Appointment: {nextVisitDate} /p
                p Your Current Health: {currentHealth} /p
                div class=prescriptionlist
                  p Active Prescriptions: /p 
                     {prescription list}
                
         **/
         String summary = "";
         String tmp;
         
         tmp = "<h2> Welcome %s, </h2>";
         summary += String.format(tmp, patient.getStringParam("fname"));
         summary += " <h2> Your summary: </h2> <div class='patient_summary'>";
         
         //Gets the next scheduled appointment, if it exists
         tmp = "<p> Next Appointment: %s </p>";
         JSONObject appt =  Database.getSeqPatientVisit(Integer.parseInt(patient.getStringParam("pid")), true);
         if(appt.get("min(visit_date)") != null)
            summary += String.format(tmp, appt.get("min(visit_date)").toString());
         else
            summary += String.format(tmp, "No scheduled appointments");
         
         //Gets the last scheduled appointment, if it exists
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
    
    /**
     * Generates a table of prescriptions for this patient
     * @param onlyValid , if true, will only include non-expired prescriptions
     * @return formattedList, a table of prescriptions and dates that they expire
     */
    public String formatPrescriptionTable(boolean onlyValid){
        //gets an array of all prescriptions for a given patient
        //if onlyValid == true, this list only contains non-expired prescriptions
        JSONArray pl = Database.getPrescriptionsByPatient(Integer.parseInt(patient.getStringParam("pid")), onlyValid);
        String formattedList = "<table><thead><tr> <th> Prescription </th> <th> Expires </th></td></tr></thead>";
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
    
    /**
     * Generates a table of past and future visits to be listed on the patient homepage
     * Tables contain all information, javascript on client side should handle hiding details
     * @return formattedList, a table of all visits and details
     */
    public String formatVisitHistoryTable(){
        //TODO: Using the eid, get the actual doctor's name
        
        //get the most up-to-date records for all the visits of a particular patient
        JSONArray vl = Database.getVisits(Integer.parseInt(patient.getStringParam("pid")));
        
        //init table, as per FooTable plugin with expandable rows
        //details about a visit are hidden unless the row is expanded
        String formattedList = "<table id='visits' class='footable table-bordered toggle-circle toggle-small'>" 
                +"<thead><tr><th data-toggle='true'> Visit #</th> <th> Appointment Date </th> <th> Assigned Physician </th> "
                +"<th data-hide='all' > Start Time </th><th data-hide='all' > End Time </th>"
                +"<th data-hide='all' > Procedure Performed </th><th data-hide='all' > Diagnosis </th><th data-hide='all' > Prescriptions Perscribed </th></tr></thead>";
        String tmp;
        
        //iterate over all visits and output rows
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
            String prescriptionSummary = formatPrescriptions(p.get("visit_id").toString());
            String diagnosisSummary = formatDiagnoses(p.get("visit_id").toString());
            String procedureSummary = formatProcedures(p.get("visit_id").toString());
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
    
    /**
     * Gets any prescriptions prescribed in a given visit and outputs a list of them 
     * @param visitId
     * @return prescriptions, an HTML formatted unordered list
     */
    public String formatPrescriptions(String visitId){        
        return FormatHelper.formatPrescriptions(Integer.parseInt(visitId), "");
    }
    
    /**
     * Gets the procedure(s) preformed during a visit and outputs a list of them
     * @param visitId
     * @return procedures, an HTML formatted paragraph
     */
    public String formatProcedures(String visitId){
        return FormatHelper.formatProcedures(Integer.parseInt(visitId), "");
    }
    
    /**
     * Gets the diagnosis(es) given during a visit and outputs a list of them
     * @param visitId
     * @return diagnoses, an HTML formatted paragraph
     */
    public String formatDiagnoses(String visitId){
        return FormatHelper.formatDiagnoses(Integer.parseInt(visitId), "");
    }
    
    /**
     * Formats the personal details of a patient and outputs them
     * @return personal, an HTML formatted summary of personal information
     */
    public String formatPersonalDetails(){
         String tmp;
         String personal = "";
         
         tmp = "<h2> Personal Details: </h2> <p> Your name: %s %s, </p>";
         personal += String.format(tmp, patient.getStringParam("fname"), patient.getStringParam("lname"));
         
         tmp = "<p> SIN: %s </p>";
         personal += String.format(tmp, patient.getStringParam("sin"));
         
         tmp = "<p> Healthcard Number: %s </p>";
         personal += String.format(tmp, patient.getStringParam("healthcard_number"));
         
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
    
    /**
     * Updates the patient info, in case the info has been updated after the session started
     * (for example, if the patient changes their account details)
     * @return null string, to satisfy JSP's complaining
     */
    public String updatePatientInfo(){     
        //refresh user
        JSONObject userInfo = Database.userLogin(patient.getStringParam("pid"), patient.getStringParam("password"), true);
        patient.info = userInfo;
        return "";
    }
    
    
}
