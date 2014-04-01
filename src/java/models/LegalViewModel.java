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
 * @author Steven
 */
public class LegalViewModel {
    User m_legal;
    
    public LegalViewModel(User legal){  
        m_legal = legal;
    }
    
    private int getLegalId(){
        return Integer.parseInt(m_legal.getStringParam("eid"));
    }
    
    //Test
    /**
     * 
     * Generates a list of all current doctors
     * 
     * @return an HTML table with a list of all doctors
     */
    public String formatDoctorList(){
        JSONArray doctors = Database.getDoctors();
        String formattedList = "Search doctors : <input id='dfilter' type='text' />"+
                                "<table class='footable vhalftable dlist'data-filter-minimum='1' data-filter='#dfilter' data-page-navigation='.pagination'>"+
                                "<thead><tr> <th> Employee ID </th> <th> First Name </th> <th> Last Name </th></tr></thead>";
        String tmp;
        for(int i = 0; i < doctors.size(); i++){
            JSONObject doctor = (JSONObject)doctors.get(i);
            tmp = "<tr class='doctorrow'><td> %s </td> <td> %s </td> <td> %s </td></tr>";
            formattedList += String.format(tmp, 
                    doctor.get("eid"), 
                    doctor.get("fname"),
                    doctor.get("lname"));
            
        }
        formattedList += "<tfoot class='hide-if-no-paging'><tr><td colspan='3'>" +
 		"<div class='pagination pagination-centered'></div></td></tr></tfoot>";
        formattedList += "</table>";
        return formattedList;
    }
    
    /** 
     * Generates a list of patient-of's for dId, and patients which have had a 
     * visit from dId but are not patients
     * 
     * This is called by AJAX when a doctor is selected from doctor list
     * @param dId
     * @return HTML table of patients
     */
    public String formatPatientList(int dId, boolean onlyRows, String date1, String date2){ 
        JSONArray patients;
        if(dId == -1){
            patients = new JSONArray();
        }else{
            patients = Database.getPatientsWithVisitsInRange(
                    dId,
                    java.sql.Date.valueOf(date1),
                    java.sql.Date.valueOf(date2));
        }
        
        String formattedList = "";
        if(!onlyRows){
            formattedList += "Search patients : <input id='pfilter' type='text' />"+
                            "<table class='footable vhalftable patlist' data-filter-minimum='1' data-filter='#pfilter' data-page-navigation='.pagination'>"+
                            "<thead><tr> <th> Patient ID </th> <th> First Name </th> <th> Last Name </th></tr></thead><tbody>";
        }
        String tmp;
        for(int i = 0; i < patients.size(); i++){
            JSONObject patient = (JSONObject)patients.get(i);
            tmp = "<tr class='patientrow'><td> %s </td> <td> %s </td> <td> %s </td></tr>";
            formattedList += String.format(tmp, 
                    patient.get("pid"), 
                    "NOT VISIBLE",
                    "NOT VISIBLE");
            
        }
        if(!onlyRows){
            formattedList += "</tbody><tfoot class='hide-if-no-paging'><tr><td colspan='3'>" +
 		"<div class='pagination pagination-centered'></div></td></tr></tfoot>";
            formattedList += "</table>";
        }
        return formattedList;
    }
    
    /**
     * Generates a HTML for table of visits which involve the doctor and patient passed
     * This is called by AJAX when a patient is selected from patient list
     * @param pId
     * @param dId
     * @return HTML for table of visits
     */
    public String formatVisitList(int pId, int dId, boolean onlyRows, String date1, String date2){
        JSONArray visits;
        if(dId == -1 || pId == -1){
            visits = new JSONArray();
        }else{
            visits = Database.getVisits(pId, dId);
            visits = Database.getVisitsInRange(
                    pId,
                    dId,
                    java.sql.Date.valueOf(date1),
                    java.sql.Date.valueOf(date2));
        }
        
        String formattedList = "";
        if(!onlyRows){
            formattedList += "Search visits : <input id='vfilter' type='text' />" +
                            "<table class='footable vhalftable vlist' data-filter-minimum='1' data-filter='#vfilter' data-page-navigation='.pagination'>"+ 
                            "<thead><tr> <th> Visit ID </th> <th> Visit Date </th> </tr></thead><tbody>";
        }
        String tmp;
        for(int i = 0; i < visits.size(); i++){
            JSONObject visit = (JSONObject)visits.get(i);
            tmp = "<tr class='visitrow'><td class='vid'> %s </td><td> %s </td></tr>";
            formattedList += String.format(tmp, 
                    visit.get("visit_id"),
                    visit.get("visit_date").toString());
            
        }
        if(!onlyRows){
            formattedList += "</tbody><tfoot class='hide-if-no-paging'><tr><td colspan='3'>" +
 		"<div class='pagination pagination-centered'></div></td></tr></tfoot>";
            formattedList += "</table>";
        }
        return formattedList;
    }
}
