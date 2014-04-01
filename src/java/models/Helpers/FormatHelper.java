/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models.Helpers;

import control.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FormatHelper {
    
    /**
     * Gets the procedure(s) preformed during a visit and outputs a list of them
     * @param visitId
     * @param filter
     * @return formatted, an HTML formatted paragraph
     */
    public static String formatProcedures(int visitId, String filter){
        String formatted = "<div class='procedures'>";
        
        JSONArray procedures = Database.getProcedureByVisit(visitId, filter);
        
        if(procedures.size() == 0){
            return "<p>No procedure performed during this visit.</p>";
        }
        
        for(int i = 0; i < procedures.size(); i++){
            JSONObject procedure = (JSONObject) procedures.get(i);
            
            formatted += String.format(
                                "<p>%s: %s</p>",
                                procedure.get("procedure_name").toString(),
                                procedure.get("description").toString()
                            );
        }
        
        formatted += "</div>";
        
        return formatted;
    }
    
    /**
     * Gets the diagnosis(es) given during a visit and outputs a list of them
     * @param visitId
     * @param filter
     * @return diagnoses, an HTML formatted paragraph
     */
    public static String formatDiagnoses(int visitId, String filter){
        JSONArray diagnoses = Database.getDiagnosisByVisit(visitId, filter);
        
        if(diagnoses.size() == 0){
            return "<p>No diagnosis given during this visit.</p>";
        }
        
        String formatted = "<div class='diagnoses'>";
        
        for(int i = 0; i < diagnoses.size(); i++){
            JSONObject diagnosis = (JSONObject)diagnoses.get(i);
            
            formatted += String.format(
                                "<p>%s</p>",
                                diagnosis.get("severity").toString()
                            );
        }
        
        formatted += "</div>";
        
        return formatted;
    }
    
    /**
     * Gets any prescriptions prescribed in a given visit and outputs a list of them 
     * @param visitId
     * @param filter
     * @return prescriptions, an HTML formatted unordered list
     */
    public static String formatPrescriptions(int visitId, String filter){
        JSONArray prescriptions = Database.getPrescriptionsByVisit(visitId, filter);
        
        if(prescriptions.size() == 0){
            return "<p>No prescriptions prescribed during this visit.</p>";
        }
        
        String formatted = "<ul class='prescriptions'>";
        
        for(int i = 0; i < prescriptions.size(); i++){
            JSONObject prescription = (JSONObject) prescriptions.get(i);
            
            formatted += String.format(
                                "<li> Drug: %s, Expires: %s</li>",
                                prescription.get("drug_name").toString(),
                                prescription.get("expires").toString()
                            );
        }
        
        formatted += "</ul>";
        
        return formatted;
    }
    
    /**
     * Gets any comments for a given visit and outputs a list of them
     * @param visitId
     * @param filter
     * @return comments, an HTML formatted unordered list
     */
    public static String formatComments(int visitId, String filter){
        JSONArray comments = Database.getCommentsByVisit(visitId, filter);
        
        if(comments.size() == 0){
            return "<p>No comments for this visit.</p>";
        }
        
        String formatted = "<ul class='comments'>";
        
        for(int i = 0; i< comments.size(); i++){
            JSONObject comment = (JSONObject) comments.get(i);
            
            formatted += String.format(
                                "<li> %s: %s</li>",
                                comment.get("timestamp").toString().trim(),
                                comment.get("content").toString().trim()
                            );
        }
        
        formatted += "</ul>";
        
        return formatted;
    }
    
    public static String generateVisitAuditTable(int visitId){
        //get the most up-to-date records for all the visits of a particular patient
        JSONArray lastUpdateds = Database.getHistoryTrail(visitId);
        
        //init table, as per FooTable plugin with expandable rows
        //details about a visit are hidden unless the row is expanded
        String formattedList = "<table id='visits' class='footable table-bordered toggle-circle toggle-small'>" 
                +"<thead><tr><th data-toggle='true'> Visit #</th> <th> Appointment Date </th> <th> Assigned Physician </th> <th> Last Updated </th> "
                +"<th data-hide='all' > Start Time </th><th data-hide='all' > End Time </th>"
                +"<th data-hide='all' > Procedure Performed </th><th data-hide='all' > Diagnosis </th><th data-hide='all' > Prescriptions Perscribed </th> <th data-hide='all'> Comments </th></tr></thead>";
        String tmp;
        for(int i = 0; i < lastUpdateds.size(); i++){
            JSONObject v = (JSONObject)lastUpdateds.get(i);
            JSONObject visit = Database.getVisit(visitId, v.get("last_updated").toString());
            
            //formats the non-details data
            tmp = "<tr><td class='visit_id'>%s</td> <td> %s </td> <td> %s </td><td> %s </td>";
            formattedList += String.format(tmp, 
                    visit.get("visit_id"),
                    visit.get("visit_date"), 
                    visit.get("eid"),
                    visit.get("last_updated"));
            
            //formats the details data
            tmp ="<td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td> <td> %s </td><td> %s </td></tr>";
            String prescriptionSummary = formatPrescriptionsAuditTrail(visitId, v.get("last_updated").toString());
            String diagnosisSummary = formatDiagnosesAuditTrail(visitId, v.get("last_updated").toString());
            String procedureSummary = formatProceduresAuditTrail(visitId, v.get("last_updated").toString());
            String commentsSummary = formatCommentsAuditTrail(visitId, v.get("last_updated").toString());
            formattedList += String.format(tmp,
                    visit.get("visit_start_time"),
                    visit.get("visit_end_time"),
                    procedureSummary,
                    diagnosisSummary,
                    prescriptionSummary,
                    commentsSummary);
        }
        formattedList += "</table>";
       
        return formattedList;        
    }
    
    private static String formatPrescriptionsAuditTrail(int visitId, String lastUpdated){
        JSONArray prescriptions = Database.getPrescriptionsAudit(visitId, lastUpdated);
        
        if(prescriptions.size() == 0){
            return "<p>No prescriptions prescribed during this visit.</p>";
        }
        
        String formatted = "<ul class='prescriptions'>";
        
        for(int i = 0; i < prescriptions.size(); i++){
            JSONObject prescription = (JSONObject) prescriptions.get(i);
            
            formatted += String.format(
                                "<li> Drug: %s, Expires: %s</li>",
                                prescription.get("drug_name").toString(),
                                prescription.get("expires").toString()
                            );
        }
        
        formatted += "</ul>";
        
        return formatted;
    }
    
    /**
     * Gets any comments for a given visit and outputs a list of them
     * @param visitId
     * @param lastUpdated
     * @return comments, an HTML formatted unordered list
     */
    public static String formatCommentsAuditTrail(int visitId, String lastUpdated){
        JSONArray comments = Database.getCommentsAudit(visitId, lastUpdated);
        
        if(comments.size() == 0){
            return "<p>No comments for this visit.</p>";
        }
        
        String formatted = "<ul class='comments'>";
        
        for(int i = 0; i< comments.size(); i++){
            JSONObject comment = (JSONObject) comments.get(i);
            
            formatted += String.format(
                                "<li> %s: %s</li>",
                                comment.get("timestamp").toString().trim(),
                                comment.get("content").toString().trim()
                            );
        }
        
        formatted += "</ul>";
        
        return formatted;
    }
    
    /**
     * Gets the procedure(s) preformed during a visit and outputs a list of them
     * @param visitId
     * @param lastUpdated
     * @return formatted, an HTML formatted paragraph
     */
    public static String formatProceduresAuditTrail(int visitId, String lastUpdated){
        String formatted = "<div class='procedures'>";
        
        JSONArray procedures = Database.getProcedureAudit(visitId, lastUpdated);
        
        if(procedures.size() == 0){
            return "<p>No procedure performed during this visit.</p>";
        }
        
        for(int i = 0; i < procedures.size(); i++){
            JSONObject procedure = (JSONObject) procedures.get(i);
            
            formatted += String.format(
                                "<p>%s: %s</p>",
                                procedure.get("procedure_name").toString(),
                                procedure.get("description").toString()
                            );
        }
        
        formatted += "</div>";
        
        return formatted;
    }
    
    /**
     * Gets the diagnosis(es) given during a visit and outputs a list of them
     * @param visitId
     * @param lastUpdated
     * @return diagnoses, an HTML formatted paragraph
     */
    public static String formatDiagnosesAuditTrail(int visitId, String lastUpdated){
        JSONArray diagnoses = Database.getDiagnosisAudit(visitId, lastUpdated);
        
        if(diagnoses.size() == 0){
            return "<p>No diagnosis given during this visit.</p>";
        }
        
        String formatted = "<div class='diagnoses'>";
        
        for(int i = 0; i < diagnoses.size(); i++){
            JSONObject diagnosis = (JSONObject)diagnoses.get(i);
            
            formatted += String.format(
                                "<p>%s</p>",
                                diagnosis.get("severity").toString()
                            );
        }
        
        formatted += "</div>";
        
        return formatted;
    }
    
    
     public static String generateAdvisorsTable(int visitId){
        //get the most up-to-date records for all the visits of a particular patient
        JSONArray advisors = Database.getAdivsorsOf(visitId);
        
        //init table, as per FooTable plugin with expandable rows
        //details about a visit are hidden unless the row is expanded
        String formattedList = "<table id='advisors' class='footable table-bordered toggle-circle toggle-small'>" 
                +"<thead><tr><th data-toggle='true'> Advisor ID </th> <th> Advisor Name </th> <th> Remove Advisor </th>"
                +"</tr></thead>";
        String tmp;
        for(int i = 0; i < advisors.size(); i++){
            JSONObject a = (JSONObject)advisors.get(i);
           
            
            //formats the non-details data
            tmp = "<tr><td>%s</td> <td> %s %s </td>";
            formattedList += String.format(tmp, 
                    a.get("doctor_id"),
                    a.get("fname"),
                    a.get("lname"));
            formattedList += String.format("<td><a href='#' onclick='javascript:removeAdvisor(%s, %s)'> Remove Advisor </a></td></tr>",
                    visitId,
                    a.get("doctor_id"));
            
           
        }
        formattedList += "</table>";
        formattedList +=  String.format("<input type='text' id='advisorid'><a href='#' onclick='javascript:addAdvisor(%s)'> Add Advisor </a>", visitId);
        return formattedList;        
    }
}
