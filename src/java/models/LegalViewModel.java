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
    
    public JSONArray buildPatientsListRows(JSONObject filters, boolean isPatientsList){
        JSONArray patients = new JSONArray();
        
        if(isPatientsList){
            patients = Database.getDoctorPatients(getLegalId(), filters);
        }
        else{
            patients = Database.getAdvisees(getLegalId(), filters);
        }
        
        JSONArray formattedRows = new JSONArray();
        
        for(int i = 0; i < patients.size(); i++){
            JSONObject p = (JSONObject) patients.get(i); 
            
            String formattedRow = "";
            
            if(isPatientsList){
                formattedRow += "<tr class='patientrow' style='display: tablerow;'>";
            }
            else{
                formattedRow += "<tr class='adviseerow' style='display: table-row;'>";
            }
            
            formattedRow += String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                                p.get("pid"),
                                p.get("fname"),
                                p.get("lname"),
                                p.get("current_health"),
                                p.get("last_visit"));
                    
            formattedRows.add(formattedRow);
        }
        
        return formattedRows;
    }
}
