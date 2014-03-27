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
public class FinanceViewModel {
    
      
    //TODO: might want to just make a helper class with stuff like building a table
    
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
            tmp = "<tr class='visitrow'><td> %s </td><td> %s </td></tr>";
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
    
    /**
     * Generates a summary of doctor's activities, including # patients seen
     * @param dId
     * @return HTML formatted summary block
     */
    public String formatDoctorSummary(int dId){
        
        JSONObject activity = Database.getDoctorActivity(dId);
        JSONObject doctor = Database.getEmployee(dId);
        System.out.println(activity);
        
        String summary = "<h2> Doctor Summary </h2>";
        
        String tmp = "<p> Dr. %s %s, employee id: %s </p>";
        summary += String.format(
                        tmp,
                        doctor.get("fname"),
                        doctor.get("lname"),
                        Integer.toString(dId)
                    );
        
        tmp = "<p> Total Visits Presided Over: %s </p>";
        summary += String.format(tmp, activity.get("total_visit_count"));
        
        tmp = "<p> for %s total patients, where %s are primary. </p>";
        summary += String.format(
                    tmp, 
                    activity.get("total_patient_count"), 
                    activity.get("primary_patient_count"));
        
        return summary;
    }
    
    /**
     * Generates a summary of patient's activities, including #visits for given doc
     * @param pId
     * @return HTML formatted summary block
     */
    public String formatPatientSummary(int pId){
        
        JSONObject patientActivity = Database.getPatientActivity(pId);
        System.out.println(patientActivity);
        String summary = "<h2> Patient Summary </h2> ";
        summary += "<p> Patient visitation and prescription summary for patient id " + pId + " : </p>";
        
        String tmp = "<ul> <li>Visits with primary doctor: %s </li>" +
                     "<li>Total visits: %s </li>" +
                     "<li>Active prescriptions: %s </li>"+
                     "<li>Total prescriptions: %s </li></ul>";
        
        summary += String.format(
                    tmp,
                    patientActivity.get("primary_visit_count"),
                    patientActivity.get("total_visit_count"),
                    patientActivity.get("active_prescription_count"),
                    patientActivity.get("total_prescription_count")
                   );
        
        return summary;
    }
    
    /**
     * Generates a summary of a particular visit, including procedure, diagnosis, and prescriptions
     * @param vId
     * @return HTML formatted summary block
     */
    public String formatVisitSummary(int vId){
        JSONObject visit = Database.getVisit(vId);
        System.out.println(visit);
        
        String summary = "<h2> Visit Details </h2>";
        
        String tmp = "<p> Visit Date: %s, Start time: %s, End time: %s </p>";
        
        summary += String.format(
                    tmp,
                    visit.get("visit_date"),
                    visit.get("visit_start_time"),
                    visit.get("visit_end_time")
                   );
        summary += "<p> Procedure performed: </p>";
        summary += models.Helpers.FormatHelper.formatProcedures(vId);
        summary += "<p> Diagnosis: </p>";
        summary += models.Helpers.FormatHelper.formatDiagnoses(vId);
        summary += "<p> Prescriptions perscribed: </p>";
        summary += models.Helpers.FormatHelper.formatPrescriptions(vId);
        
        return summary;
    }
    
}
