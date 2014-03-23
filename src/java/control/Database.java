/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package control;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Set;
/**
 *
 * @author Kyle
 */
public class Database {
    
    public static final String url ="jdbc:mysql://198.50.229.147:3306/";
    public static final String user = "ece356";
    public static final String pass = "database";
    
    private static Connection connection;
    
    //not sure if the connection needs to be closed, or if its ok just to leave it on timeout
    //not really sure if this is a good way to do the application anyways... is one db connection OK? 
    // probably.
    public static boolean openConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            return false;
        }

    }
    
    public static void closeConnection(){
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * 
     * @param id
     * @param pw
     * @return A JSONArray with the columns of the row from the appropriate user type
     */
    public static JSONObject userLogin(String id, String pw, boolean patient){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject user = new JSONObject();
        
        PreparedStatement ps;
        if(status){
            
            Statement s;
            ResultSet rs;
            try{
                 if(patient){
                    ps = connection.prepareStatement("SELECT * FROM ece356.patient WHERE pid=? AND password=? AND is_enabled=1");
                }else{
                    ps = connection.prepareStatement("SELECT * FROM ece356.patient WHERE eid=? AND password=? AND is_enabled=1");
                }
                
                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, pw);
                
                rs = ps.executeQuery();
               
                user = convertRowToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return user;
            }
            
        }
        
        return user;
        
    }
    
    
    //Dynamically converts all results into a JSONArray of rows (JSONObjects)
    //all column names returned by the query becomes keys in the JSONObjects
    //  --------------------
    //  |  id     |  name  |
    //  |   1     |  foo   |
    //  |   2     |  bar   |
    //  --------------------
    //looks like [{id:1,"name":"foo"},{id:2,"name":"bar"}]
    public static JSONArray convertToJson(ResultSet rs)
    {
        JSONArray rows = new JSONArray();
        
        try {
            while (rs.next())
            {
                ResultSetMetaData rsmd = rs.getMetaData();
                int count = rsmd.getColumnCount();
                JSONObject row = new JSONObject();
                
                for (int i = 1; i <= count; i++)
                {
                    row.put(rsmd.getColumnName(i), rs.getString(i));
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return rows;
        }
        return rows;
    }
    
    //Dynamically converts the first row of the results into a JSONObject
    //all column names returned by the query becomes keys
    //  --------------------
    //  |  id     |  name  |
    //  |   1     |  foo   |
    //  |   2     |  bar   |
    //  --------------------
    //looks like {id:1,"name":"foo"}
    public static JSONObject convertRowToJson(ResultSet rs)
    {
        JSONObject obj = new JSONObject();
        
        try {
            if (rs.next())
            {
                ResultSetMetaData rsmd = rs.getMetaData();
                int count = rsmd.getColumnCount();
                
                for (int i = 1; i <= count; i++)
                {
                    obj.put(rsmd.getColumnName(i), rs.getString(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return obj;
        }
        return obj;
    }
    
    public static boolean updatePatient(int patientId, String password, JSONObject params){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
         if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "UPDATE ece356.patient SET ";
                Set keys = params.keySet();
                for(int i = 0; i < params.size(); i++){
                    if((keys.toArray()[i]).toString().equals("sin") || (keys.toArray()[i]).toString().equals("street_number")){
                        preparedStatement += (keys.toArray()[i]).toString() + "=" + params.get((keys.toArray()[i]).toString()).toString();
                    }else{
                        preparedStatement += (keys.toArray()[i]).toString() + "='" + params.get((keys.toArray()[i]).toString()).toString() + "'";
                    }
                    
                    if(i != params.size()-1){
                        preparedStatement += ",";
                    }
                }
                preparedStatement += " WHERE pid=? AND password=? AND is_enabled=1";
                ps = connection.prepareStatement(preparedStatement);
                
                ps.setInt(1, patientId);
                ps.setString(2, password);
                if(ps.executeUpdate() == 1 ){
                    //success
                    return true;
                }else if(ps.executeUpdate() > 1){
                    //something went terribly wrong
                }else{
                    return false;
                }
               
                
                
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        return false;
    }
    
    /**
     * 
     * @param doctorId
     * @return A JSONArray with the following info: 
     * { pid, fname, lname, street_number., street, city, postcode, sin, num_visits, current_health }
     */
    public static JSONArray getPatients(int doctorId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray rows = new JSONArray();
        JSONArray patients = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                ps = connection.prepareStatement("SELECT * FROM ece356.patient-of WHERE doctor_id=?");
                
                ps.setInt(1, doctorId);
                
                rs = ps.executeQuery();
               
                rows = convertToJson(rs);
                
                if (!rows.isEmpty())
                {
                    //found patient to doctor mappings
                    //get patient information
                    for (int i = 0; i < rows.size(); i++)
                    {
                        patients.add(getPatient(Integer.parseInt(((JSONObject)rows.get(i)).get("pid").toString())));
                    }
                    
                }
            }catch(SQLException e){
                e.printStackTrace();
                return patients;
            }
            
        }
        
        return patients;
        
    }
    
    /**
     * 
     * @param patientId
     * @return A JSONObject with the following info: 
     * { pid, fname, lname, street_number, street, city, postcode, sin, num_visits, current_health }
     */
    public static JSONObject getPatient(int patientId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject patient = new JSONObject();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                ps = connection.prepareStatement("SELECT * FROM ece356.patient WHERE pid=? AND is_enabled=1");
                
                ps.setInt(1, patientId);
                
                rs = ps.executeQuery();
               
                patient = convertRowToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return patient;
            }
            
        }
        return patient;
        
    }
    
    //if next == false gets the previous visit, otherwise gets the next sequential appt
    public static JSONObject getSeqPatientVisit(int patientId, boolean next){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject visit = new JSONObject();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                if(next){
                    ps = connection.prepareStatement(
                        "SELECT visit_id, last_updated, min(visit_date), visit_start_time, visit_end_time, pid, eid FROM"
                        + "(SELECT * FROM ece356.visit WHERE pid=? AND is_valid=1 AND visit_date > NOW()) as future_visits");
                }else{
                    ps = connection.prepareStatement(
                        "SELECT visit_id, last_updated, max(visit_date), visit_start_time, visit_end_time, pid, eid FROM"
                        + "(SELECT * FROM ece356.visit WHERE pid=? AND is_valid=1 AND visit_date <= NOW())as past_visits");
                }
                ps.setInt(1, patientId);
                rs = ps.executeQuery();
               
                visit = convertRowToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visit;
            }
            
        }
        return visit;
    }
    
    public static JSONArray getVisits(int patientId){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray visits = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //selects each visit with the biggest "last updated" time
                //and that isn't a cancelled visit
                String preparedStatement = "select * " +
                    "from ece356.visit v " +
                    "inner join( " +
                    "select visit_id, max(last_updated) last_updated " +
                    "from ece356.visit where pid=? and is_valid=1 " +
                    "group by visit_id" +
                    " ) mv on mv.visit_id = v.visit_id and mv.last_updated = v.last_updated;";
               
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, patientId);
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        return visits;
    }

    public static JSONArray getPrescriptionsByPatient(int patientId, boolean onlyValid){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray prescriptions = new JSONArray();
        JSONArray visits = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "";
                if(onlyValid){
                    //selects the prescription with the biggest last_updated time
                    //and that hasn't yet expired
                    preparedStatement = "select * " +
                    "from ece356.prescription p " +
                    "inner join( " +
                    "select visit_id,drug_name, max(last_updated) last_updated " +
                    "from ece356.prescription where visit_id=? and expires > NOW() " +
                    "group by visit_id, drug_name" +
                    " ) mp on mp.visit_id = p.visit_id and mp.last_updated = p.last_updated and mp.drug_name = p.drug_name;";

                }else{
                    //selects the prescription with the biggest last_updated time
                     preparedStatement = "select * " +
                    "from ece356.prescription p " +
                    "inner join( " +
                    "select visit_id,drug_name, max(last_updated) last_updated " +
                    "from ece356.prescription where visit_id=? " +
                    "group by visit_id, drug_name" +
                    " ) mp on mp.visit_id = p.visit_id and mp.last_updated = p.last_updated and mp.drug_name = p.drug_name;";
                }
                ps = connection.prepareStatement(preparedStatement);
                
                visits = getVisits(patientId);
                if(!visits.isEmpty()){
                    for(int i = 0; i < visits.size(); i++){
                        ps.setInt(1, Integer.parseInt(((JSONObject)visits.get(i)).get("visit_id").toString()));
                        rs = ps.executeQuery();
                        prescriptions.add(convertToJson(rs));
                    }
                }
                
              return prescriptions;
            }catch(SQLException e){
                e.printStackTrace();
                return prescriptions;
            }
            
        }
        
        return prescriptions;
        
    }
    
    
    
    public static JSONArray getPrescriptionsByVisit(int visitId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray prescriptions = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "select * " +
                "from ece356.prescription p " +
                "inner join( " +
                "select visit_id, drug_name, max(last_updated) last_updated " +
                "from ece356.prescription where visit_id=? " +
                "group by visit_id, drug_name" +
                " ) mp on mp.visit_id = p.visit_id and mp.last_updated = p.last_updated and mp.drug_name = p.drug_name;";
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                
                rs = ps.executeQuery();
                prescriptions = convertToJson(rs);
                
                
              return prescriptions;
            }catch(SQLException e){
                e.printStackTrace();
                return prescriptions;
            }
            
        }
        
        return prescriptions;
        
    }
    
    public static JSONArray getProcedureByVisit(int visitId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray procedures = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "select * " +
                "from ece356.procedure p " +
                "inner join( " +
                "select visit_id, procedure_name, max(last_updated) last_updated " +
                "from ece356.procedure where visit_id=? " +
                "group by visit_id, procedure_name" +
                " ) mp on mp.visit_id = p.visit_id and mp.last_updated = p.last_updated and mp.procedure_name = p.procedure_name;";
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                
                rs = ps.executeQuery();
                procedures = convertToJson(rs);
                
                
              return procedures;
            }catch(SQLException e){
                e.printStackTrace();
                return procedures;
            }
            
        }
        
        return procedures;
        
    }
    
    public static JSONArray getDiagnosisByVisit(int visitId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray diagnoses = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "select * " +
                "from ece356.diagnosis d " +
                "inner join( " +
                "select visit_id, max(last_updated) last_updated " +
                "from ece356.diagnosis where visit_id=? " +
                "group by visit_id, last_updated" +
                " ) dd on dd.visit_id = d.visit_id and dd.last_updated = d.last_updated;";
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                
                rs = ps.executeQuery();
                diagnoses = convertToJson(rs);
                
                
              return diagnoses;
            }catch(SQLException e){
                e.printStackTrace();
                return diagnoses;
            }
            
        }
        
        return diagnoses;
        
    }
    
    
}
