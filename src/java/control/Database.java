/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package control;

import java.sql.*;
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
    
    /**
     * Opens a DB connection
     * @return 
     */
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
    
    /**
     * Closes a DB Connection
     * TODO: I removed all of the calls to closeConnection(), since it was causing problems
     *       when one function calls another function that performs a query (since the inner function would close the connection)
     *      Need to figure out a way how to manage the db connection well
     */
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
                    ps = connection.prepareStatement("SELECT * FROM ece356.employee WHERE eid=? AND password=? AND is_enabled=1");
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
    
    /**
     * 
     * Updates the patient record using the attributes in the JSONObject params
     * THIS METHOD, AND updateStaff SHOULD BE THE ONLY ONES WHO HAVE AN UPDATE QUERY
     * all other update methods MUST be done with a copy and modification of the last-updated field
     * 
     * This method will fail if the params dont have valid columns or values
     * 
     * TODO: this isn't safe from the user injecting wildcards for password or username
     * 
     * @param patientId
     * @param password
     * @param params
     * @return true if update was successful, false if it updated more than one row, or no rows
     */
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
     * Gets all the patient records for patients of a given doctor
     * @param doctorId
     * @return A JSONArray with all the patient records
     */
    public static JSONArray getPatients(int doctorId, JSONObject filters){
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
                ps = connection.prepareStatement(
                        "SELECT v.pid, p.fname, p.lname, p.current_health, max(v.visit_date) AS last_visit " +
                        "FROM ece356.visit AS v " +
                        "INNER JOIN ( " +
                            "SELECT pid, fname, lname, current_health " +
                            "FROM ece356.patient " +
                            "WHERE is_enabled='1'" +
                        ") AS p ON p.pid=v.pid " +
                        "WHERE eid=? AND is_valid='1' " +
                        buildPatientsFilters() +
                        "GROUP BY v.pid " +
                        "HAVING last_visit BETWEEN ? AND ?"
                );
                
                ps.setInt(1, doctorId);
                
                ps.setString(
                    2,
                    filters.get("pid") != null 
                        ? "%%" + filters.get("pid").toString().trim() + "%%"
                        : "%%"
                    );
                
                ps.setString(
                    3,
                    filters.get("fname") != null
                        ? "%%" + filters.get("fname").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setString(
                    4,
                    filters.get("lname") != null
                        ? "%%" + filters.get("lname").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setString(
                    5,
                    filters.get("current_health") != null
                        ? "%%" + filters.get("current_health").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setDate(
                    6,
                    filters.get("last_visit_start") != null && !filters.get("last_visit_start").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("last_visit_start").toString().trim())
                        : java.sql.Date.valueOf("2000-01-01")
                );
                
                ps.setDate(
                    7,
                    filters.get("last_visit_end") != null && !filters.get("last_visit_end").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("last_visit_end").toString().trim())
                        : java.sql.Date.valueOf("2100-01-01")    
                );
                
                rs = ps.executeQuery();                
                patients = convertToJson(rs);
            }catch(SQLException e){
                e.printStackTrace();
                return patients;
            } 
        }
        
        return patients;
    }
    
    /**
     * Gets all the patient records for advisees of a given doctor
     * @param doctorId
     * @return A JSONArray with all the advisee records
     */
    public static JSONArray getAdvisees(int doctorId, JSONObject filters){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray rows = new JSONArray();
        JSONArray advisees = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                ps = connection.prepareStatement(
                        "SELECT v.pid, p.fname, p.lname, p.current_health, MAX(v.visit_date) as last_visit " +
                        "FROM ece356.visit as v " +
                        "INNER JOIN ( " +
                            "SELECT visit_id " +
                            "FROM ece356.advises " +
                            "WHERE doctor_id=? " +
                        ") AS a ON a.visit_id=v.visit_id " +
                        "INNER JOIN ( " +
                            "SELECT pid, fname, lname, current_health " +
                            "FROM ece356.patient " +
                            "WHERE is_enabled='1' " +
                        ") AS p on p.pid=v.pid " +
                        "WHERE v.is_valid='1' " +
                        buildPatientsFilters() +
                        "GROUP BY v.pid " +
                        "HAVING last_visit BETWEEN ? AND ?"
                );
                
                ps.setInt(1, doctorId);
                
                ps.setString(
                    2,
                    filters.get("pid") != null 
                        ? "%%" + filters.get("pid").toString().trim() + "%%"
                        : "%%"
                    );
                
                ps.setString(
                    3,
                    filters.get("fname") != null
                        ? "%%" + filters.get("fname").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setString(
                    4,
                    filters.get("lname") != null
                        ? "%%" + filters.get("lname").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setString(
                    5,
                    filters.get("current_health") != null
                        ? "%%" + filters.get("current_health").toString().trim() + "%%"
                        : "%%"
                );
                
                ps.setDate(
                    6,
                    filters.get("last_visit_start") != null && !filters.get("last_visit_start").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("last_visit_start").toString().trim())
                        : java.sql.Date.valueOf("2000-01-01")
                );
                
                ps.setDate(
                    7,
                    filters.get("last_visit_end") != null && !filters.get("last_visit_end").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("last_visit_end").toString().trim())
                        : java.sql.Date.valueOf("2100-01-01")    
                );

                rs = ps.executeQuery();
                advisees = convertToJson(rs);
                
                if (!rows.isEmpty())
                {
                    //found patient to doctor mappings
                    //get patient information
                    for (int i = 0; i < rows.size(); i++)
                    {
                        JSONObject advisee = getPatient(Integer.parseInt(((JSONObject)rows.get(i)).get("pid").toString()));  
                        advisee.put("last_visit", ((JSONObject)rows.get(i)).get("last_visit"));
                        advisees.add(advisee);
                    }
                    
                }
            }catch(SQLException e){
                e.printStackTrace();
                return advisees;
            }
        }
        
        return advisees;
    }
    
    private static String buildPatientsFilters(){             
        String filter = " AND v.pid LIKE ?";
        filter += " AND p.fname LIKE ?";
        filter += " AND p.lname LIKE ?";
        filter += " AND p.current_health LIKE ?";
        filter += " ";

        /*if(filters.get("pid") != null && !filters.get("pid").toString().trim().equals("")){
            String filterValue = filters.get("pid").toString().trim();

            filter += String.format(" AND v.pid LIKE '%%%s%%'", filterValue);
        }

        if(filters.get("fname") != null && !filters.get("fname").toString().trim().equals("")){
            String filterValue = filters.get("fname").toString().trim();
            filter += String.format(" AND p.fname LIKE '%%%s%%'", filterValue);
        }

        if(filters.get("lname") != null && !filters.get("lname").toString().trim().equals("")){
            String filterValue = filters.get("lname").toString().trim();
            filter += String.format(" AND p.lname LIKE '%%%s%%'", filterValue);
        }

        if(filters.get("current_health") != null && !filters.get("current_health").toString().trim().equals("")){
            String filterValue = filters.get("current_health").toString().trim();
            filter += String.format(" AND p.current_health LIKE '%%%s%%'", filterValue);
        }
        
        filter += " ";*/
        
        return filter;
    }

    
    /**
     * Gets all the patient records for patients of a given doctor
     * @param doctorId
     * @return A JSONArray with all the patient records
     */
    public static JSONArray getPatientsWithVisitsInRange(int doctorId, Date date1, Date date2){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray patients = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //this should also have a join with visits where eid=dId and get all distinct pids
                //doctors can have patients that aren't their primary patients (so wouldn't be in the patient-of relation)
                ps = connection.prepareStatement(
                        "SELECT * FROM ece356.patient inner join"
                                 + "(SELECT DISTINCT(pid) FROM ece356.`visit`"
                                 + " WHERE eid=? AND visit_date >= ? AND visit_date <= ?) as p"
                                 + " ON p.pid = ece356.patient.pid;"
                );
                
                ps.setInt(1, doctorId);
                ps.setDate(2, date1);
                ps.setDate(3, date2);
                System.out.println(ps);
                rs = ps.executeQuery();
               
                patients = convertToJson(rs);
                
                
            }catch(SQLException e){
                e.printStackTrace();
                return patients;
            }
            
        }
        
        return patients;
        
    }
    
    /**
     * Queries the database to get information about a patient
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
    
    /**
     * Returns a visit row of either the next or previous visit for a particular patient.
     * if next is true, gets the next visit, otherwise gets the previous one
     * TODO: change query to only look at most up-to-date records
     * @param patientId
     * @param next
     * @return a JSONObject describing a visit row
     */
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
                    //gets the closest visit which comes after now
                    ps = connection.prepareStatement(
                        "SELECT visit_id, last_updated, min(visit_date), visit_start_time, visit_end_time, pid, eid FROM"
                        + "(SELECT * FROM ece356.visit WHERE pid=? AND is_valid=1 AND visit_date > NOW()) as future_visits");
                }else{
                    //gets the oldest visit which comes before now
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
    
    /**
     * Queries database for information about a particular visit
     * @param visitId
     * @return A JSONObject describing a visit row
     */
    public static JSONObject getVisit(int visitId){
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
                ps = connection.prepareStatement(
                       "SELECT visit_id, max(last_updated) last_updated, visit_date, visit_start_time, visit_end_time, pid, eid, is_valid"+
                       " FROM ece356.visit WHERE visit_id=? ");
                ps.setInt(1, visitId);
                rs = ps.executeQuery();
               
                visit = convertRowToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visit;
            }
            
        }
        return visit;
    }
    
    /**
     * Same as getVisits but doesn't filter by doctor
     * @param patientId
     * @return 
     */
    public static JSONArray getVisits(int patientId){
        return getVisits(patientId, -1);
    }
    
    /**
     * Queries for a list of all visitation records for a particular patient
     * Only gets most up-to-date records
     * @param patientId
     * @return a JSONArray describing all patient visits
     */
    public static JSONArray getVisits(int patientId, int doctorId){
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
                String preparedStatement;
                if(doctorId == -1){
                    preparedStatement = "select * " +
                    "from ece356.visit v " +
                    "inner join( " +
                    "select visit_id, max(last_updated) last_updated " +
                    "from ece356.visit where pid=? and is_valid=1 " +
                    "group by visit_id" +
                    " ) mv on mv.visit_id = v.visit_id and mv.last_updated = v.last_updated;";
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, patientId);
                }else{
                    preparedStatement = "select * " +
                    "from ece356.visit v " +
                    "inner join( " +
                    "select visit_id, max(last_updated) last_updated " +
                    "from ece356.visit where pid=? and eid=? and is_valid=1 " +
                    "group by visit_id" +
                    " ) mv on mv.visit_id = v.visit_id and mv.last_updated = v.last_updated;";
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                }
                
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        return visits;
    }
    
    /**
     * 
     * @param patientId
     * @param doctorId
     * @return 
     */
    public static JSONArray getPatientVisitsForDoctor(int patientId, int doctorId, JSONObject filters){
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
 
               
                ps = connection.prepareStatement(
                        "SELECT visit_id, MAX(last_updated) AS last_updated, visit_date, visit_start_time, visit_end_time, pid, eid " +
                        "FROM ece356.visit " +
                        "WHERE pid=? AND eid=? is_valid='1' " +
                        "GROUP BY visit_id"
                );
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        
        return visits;
    }

    /**
     * 
     * @param adviseeId
     * @param doctorId
     * @return 
     */
    public static JSONArray getAdviseeVisitsForDoctor(int adviseeId, int doctorId, JSONObject filters){
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
 
               
                ps = connection.prepareStatement(
                        "SELECT visit_id, MAX(last_updated) AS last_updated, visit_date, visit_start_time, visit_end_time, pid, eid " +
                            "FROM ece356.visit as v " +
                            "INNER JOIN ( " +
                                "SELECT visit_id " +
                                "FROM ece356.advises AS a " +
                                "WHERE doctor_id=? " +
                            ") ON a.visit_id=v.visit_id " +
                            "WHERE pid=? AND is_valid='1' " +
                            "GROUP BY visit_id"
                );
                ps.setInt(1, doctorId);
                ps.setInt(2, adviseeId);
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        
        return visits;
    }
    
    /**
     * Gets all prescriptions for a given patient (only looking at most up-to-date records)
     * if onlyValid is true, only gets prescriptions that haven't expired
     * otherwise, gets all prescriptions
     * @param patientId
     * @param onlyValid
     * @return a JSONArray describing prescription rows
     */
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
    
    
     /**
     * Queries the database to return a JSONArray of prescriptions perscribed for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @return a JSONArray describing prescription row(s)
     */
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
    
    /**
     * Queries the database to return a JSONArray of procedures for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @return a JSONArray describing procedure row(s)
     */
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
    
    /**
     * Queries the database to return a JSONArray of diagnoses for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @return a JSONArray describing diagnosis row(s)
     */
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
    
    /**
     * Queries the database to return a JSONArray of all doctors 
     * @param 
     * @return a JSONArray describing doctor row(s)
     */
    public static JSONArray getDoctors(){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray doctors = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "select * from ece356.employee WHERE is_enabled=1 AND dept=\"DOCTOR\"";
                ps = connection.prepareStatement(preparedStatement);
                
                rs = ps.executeQuery();
                doctors = convertToJson(rs);
                
                
              return doctors;
            }catch(SQLException e){
                e.printStackTrace();
                return doctors;
            }
            
        }
        
        return doctors;
        
    }
    
    /**
     * Queries the database to get a summary on a doctor's activity
     * @param dId, doctor id
     * @return a JSONArray describing doctor summary row
     */
    public static JSONObject getDoctorActivity(int dId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject doctoractivity = new JSONObject();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = " SELECT" +
                    " (SELECT COUNT(*) FROM ece356.`patient-of` WHERE doctor_id=?) as primary_patient_count," +
                    " (SELECT COUNT(DISTINCT pid) from ece356.`visit` WHERE eid=?) as total_patient_count," +
                    " (SELECT COUNT(DISTINCT visit_id) from ece356.`visit` WHERE eid=?) as total_visit_count;";
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, dId);
                ps.setInt(2, dId);
                ps.setInt(3, dId);
                
                rs = ps.executeQuery();
                doctoractivity = convertRowToJson(rs);
                
                
              return doctoractivity;
            }catch(SQLException e){
                e.printStackTrace();
                return doctoractivity;
            }
            
        }
        
        return doctoractivity;
        
    }
    
    /**
     * Queries the database to return a JSONArray of an employee 
     * @param eId, employee id
     * @return a JSONArray describing employee row(s)
     */
    public static JSONObject getEmployee(int eId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject employee = new JSONObject();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "SELECT * from ece356.employee WHERE eid=?;";
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, eId);
                
                rs = ps.executeQuery();
                employee = convertRowToJson(rs);
                
                
              return employee;
            }catch(SQLException e){
                e.printStackTrace();
                return employee;
            }
            
        }
        
        return employee;
        
    }
    
     /**
     * Queries the database to return a JSONArray of a patient's activity summary 
     * @param pId, patient's id
     * @return a JSONArray with patient summary counts
     */
    public static JSONObject getPatientActivity(int pId){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONObject patientactivity = new JSONObject();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = " SELECT" +
                "(SELECT COUNT(DISTINCT visit_id) " +
                "    FROM ece356.`visit` INNER JOIN ece356.`patient-of` " +
                "    ON ece356.`visit`.eid = ece356.`patient-of`.doctor_id" +
                "    WHERE pid=?) as primary_visit_count," +
                "(SELECT COUNT(*) from ece356.`visit` WHERE pid=? ) as total_visit_count," +
                "(SELECT COUNT(*) " +
                "    FROM ece356.prescription INNER JOIN ece356.visit" +
                "    ON ece356.prescription.visit_id = ece356.visit.visit_id" +
                "    WHERE ece356.visit.pid = ?) as total_prescription_count," +
                "(SELECT COUNT(*) " +
                "    FROM ece356.prescription INNER JOIN ece356.visit" +
                "    ON ece356.prescription.visit_id = ece356.visit.visit_id" +
                "    WHERE ece356.visit.pid = ? and ece356.prescription.expires >= NOW()) as active_prescription_count";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, pId);
                ps.setInt(2, pId);
                ps.setInt(3, pId);
                ps.setInt(4, pId);
                
                rs = ps.executeQuery();
                patientactivity = convertRowToJson(rs);
                
                
              return patientactivity;
            }catch(SQLException e){
                e.printStackTrace();
                return patientactivity;
            }
            
        }
        
        return patientactivity;
        
    }
    
    /**
     * Queries for a list of all visitation records for a particular patient within a particular range of dates
     * Only gets most up-to-date records
     * @param patientId patient's id
     * @param doctorId doctor's id (-1 will fetch visits for all doctors)
     * @param date1 start date of range
     * @param date2 end date of range
     * @return a JSONArray describing all patient visits
     */
    public static JSONArray getVisitsInRange(int patientId, int doctorId, Date date1, Date date2){
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
                String preparedStatement;
                if(doctorId == -1){
                    preparedStatement = "select * " +
                    "from ece356.visit v " +
                    "inner join( " +
                    "select visit_id, max(last_updated) last_updated " +
                    "from ece356.visit where pid=? and is_valid=1 and visit_date >= ? and visit_date <= ? " +
                    "group by visit_id" +
                    " ) mv on mv.visit_id = v.visit_id and mv.last_updated = v.last_updated;";
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, patientId);
                    ps.setDate(2, date1);
                    ps.setDate(3, date2);
                }else{
                    preparedStatement = "select * " +
                    "from ece356.visit v " +
                    "inner join( " +
                    "select visit_id, max(last_updated) last_updated " +
                    "from ece356.visit where pid=? and eid=? and is_valid=1 and visit_date >= ? and visit_date <= ?" +
                    "group by visit_id" +
                    " ) mv on mv.visit_id = v.visit_id and mv.last_updated = v.last_updated;";
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setDate(3, date1);
                    ps.setDate(4, date2);
                }
                
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        return visits;
    }
    
}
