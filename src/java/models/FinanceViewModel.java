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
public class FinanceViewModel {
    
    //TODO: once working for all time periods, modify view so there's a field for range of time
    //use field for DB queries in AJAX calls
    
    /**
     * 
     * Generates a list of all current doctors
     * 
     * @return an HTML table with a list of all doctors
     */
    public String formatDoctorList(){
        return "";
    }
    
    /**
     * Generates a list of patient-of's for dId, and patients which have had a 
     * visit from dId but are not patients
     * 
     * This is called by AJAX when a doctor is selected from doctor list
     * @param dId
     * @return HTML table of patients
     */
    public String formatPatientList(int dId){ 
        return "";
    }
    
    /**
     * Generates a HTML for table of visits which involve the doctor and patient passed
     * This is called by AJAX when a patient is selected from patient list
     * @param pId
     * @param dId
     * @return HTML for table of visits
     */
    public String formatVisitList(int pId, int dId){
        return "";
    }
    
    /**
     * Generates a summary of doctor's activities, including # patients seen
     * @param dId
     * @return HTML formatted summary block
     */
    public String formatDoctorSummary(int dId){
        return "";
    }
    
    /**
     * Generates a summary of patient's activities, including #visits for given doc
     * @param pId
     * @return HTML formatted summary block
     */
    public String formatPatientSummary(int pId){
        return "";
    }
    
    /**
     * Generates a summary of a particular visit, including procedure, diagnosis, and prescriptions
     * @param vId
     * @return HTML formatted summary block
     */
    public String formatVisitSummary(int vId){
        return "";
    }
    
}
