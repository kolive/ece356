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
     * @return formatted, an HTML formatted paragraph
     */
    public static String formatProcedures(int visitId){
        String formatted = "<div class='procedures'>";
        
        JSONArray procedures = Database.getProcedureByVisit(visitId);
        
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
     * @return diagnoses, an HTML formatted paragraph
     */
    public static String formatDiagnoses(int visitId){
        JSONArray diagnoses = Database.getDiagnosisByVisit(visitId);
        
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
     * @return prescriptions, an HTML formatted unordered list
     */
    public static String formatPrescriptions(int visitId){
        JSONArray prescriptions = Database.getPrescriptionsByVisit(visitId);
        
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
    
    public static String formatComments(int visitId){
        JSONArray comments = Database.getCommentsByVisit(visitId);
        
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
}
