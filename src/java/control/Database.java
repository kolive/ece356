/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package control;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Set;
import java.util.Date;
import java.util.Locale;
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
                    if((keys.toArray()[i]).toString().equals("sin") || (keys.toArray()[i]).toString().equals("street_number") || (keys.toArray()[i]).toString().equals("healthcard_number")){
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
                //this should also have a join with visits where eid=dId and get all distinct pids
                //doctors can have patients that aren't their primary patients (so wouldn't be in the patient-of relation)
                ps = connection.prepareStatement("SELECT * FROM ece356.`patient-of` WHERE doctor_id=?");
                
                ps.setInt(1, doctorId);
                rs = ps.executeQuery();
               
                rows = convertToJson(rs);
                
                if (!rows.isEmpty())
                {
                    //found patient to doctor mappings
                    //get patient information
                    for (int i = 0; i < rows.size(); i++)
                    {
                        patients.add(getPatient(Integer.parseInt(((JSONObject)rows.get(i)).get("patient_id").toString())));
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
     * Gets all the patient records for patients of a given doctor
     * @param doctorId
     * @return A JSONArray with all the patient records
     */
    public static JSONArray getDoctorPatients(int doctorId, JSONObject filters){
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
                    filters.get("pid") != null && !filters.get("pid").toString().trim().equals("")
                        ? filters.get("pid").toString().trim()
                        : "%"
                    );
                
                ps.setString(
                    3,
                    filters.get("fname") != null
                        ? "%" + filters.get("fname").toString().trim() + "%"
                        : "%"
                );
                
                ps.setString(
                    4,
                    filters.get("lname") != null
                        ? "%" + filters.get("lname").toString().trim() + "%"
                        : "%"
                );
                
                ps.setString(
                    5,
                    filters.get("current_health") != null
                        ? "%" + filters.get("current_health").toString().trim() + "%"
                        : "%"
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
                    filters.get("pid") != null && !filters.get("pid").toString().trim().equals("")
                        ? filters.get("pid").toString().trim()
                        : "%"
                    );
                
                ps.setString(
                    3,
                    filters.get("fname") != null
                        ? "%" + filters.get("fname").toString().trim() + "%"
                        : "%"
                );
                
                ps.setString(
                    4,
                    filters.get("lname") != null
                        ? "%" + filters.get("lname").toString().trim() + "%"
                        : "%"
                );
                
                ps.setString(
                    5,
                    filters.get("current_health") != null
                        ? "%" + filters.get("current_health").toString().trim() + "%"
                        : "%"
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
        
        return filter;
    }
    
    /**
     * Gets all the patient records for patients of a given doctor
     * @param doctorId
     * @return A JSONArray with all the patient records
     */
    public static JSONArray getPatientsWithVisitsInRange(int doctorId, java.sql.Date date1, java.sql.Date date2){
        
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
                 ;
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
     * Queries database for information about a particular visit and all attached records
     * @param visitId
     * @return A JSONObject describing a visit row
     */
    public static JSONArray getFullVisitRecord(int visitId){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray visit = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                ps = connection.prepareStatement(
                        "SELECT ece356.visit.visit_id, ece356.visit.visit_date, ece356.visit.visit_start_time, ece356.visit.visit_end_time, ece356.visit.pid, ece356.visit.eid,\n" +
                        "ece356.prescription.drug_name, ece356.prescription.expires, ece356.`procedure`.procedure_name as `procedure`, ece356.`procedure`.description as procedure_description,\n" +
                        "ece356.diagnosis.severity as diagnosis, ece356.comment.content as comment\n" +
                        "FROM ece356.visit \n" +
                        "NATURAL JOIN ( SELECT visit_id, MAX(last_updated) last_updated FROM ece356.visit WHERE visit_id = ? GROUP BY visit_id ) last_record\n" +
                        "LEFT OUTER JOIN ece356.`procedure` ON ece356.`procedure`.visit_id = ece356.visit.visit_id AND ece356.`procedure`.last_updated = ece356.visit.last_updated\n" +
                        "LEFT OUTER JOIN ece356.diagnosis ON ece356.diagnosis.visit_id = ece356.visit.visit_id AND ece356.diagnosis.last_updated = ece356.visit.last_updated\n" +
                        "LEFT OUTER JOIN ece356.prescription ON ece356.prescription.visit_id = ece356.visit.visit_id AND ece356.prescription.last_updated = ece356.visit.last_updated\n" +
                        "LEFT OUTER JOIN ece356.comment ON ece356.comment.eid = ece356.visit.eid AND ece356.comment.visit_id = ece356.visit.visit_id AND ece356.comment.last_updated = ece356.visit.last_updated\n" +
                        "WHERE ece356.visit.visit_id = last_record.visit_id AND ece356.visit.last_updated = last_record.last_updated;");
                ps.setInt(1, visitId);
                rs = ps.executeQuery();
                
                visit = convertToJson(rs);
                
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
     * Queries database for information about a particular visit
     * @param visitId
     * @return A JSONObject describing a visit row
     */
    public static JSONObject getVisit(int visitId, String lastUpdated){
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
                       "SELECT *"+
                       " FROM ece356.visit WHERE visit_id=? and last_updated=?");
                ps.setInt(1, visitId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(lastUpdated));
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
                        "SELECT v.visit_id, MAX(v.last_updated) AS last_updated, v.visit_date, v.visit_start_time, v.visit_end_time, v.pid, v.eid " +
                        "FROM ece356.visit AS v " +
                        "WHERE v.pid=? AND v.eid=? AND v.is_valid='1' " +
                        buildVisitsFilters() +
                        "GROUP BY v.visit_id"
                );
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                
                ps.setString(
                    3,
                    filters.get("visit_id") != null && !filters.get("visit_id").toString().trim().equals("")
                        ? filters.get("visit_id").toString().trim()
                        : "%"
                );
                
                ps.setDate(
                    4,
                    filters.get("visit_date_start") != null && !filters.get("visit_date_start").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("visit_date_start").toString().trim())
                        : java.sql.Date.valueOf("2000-01-01")
                );
                
                ps.setDate(
                    5,
                    filters.get("visit_date_end") != null && !filters.get("visit_date_end").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("visit_date_end").toString().trim())
                        : java.sql.Date.valueOf("2100-01-01") 
                );
                  
                ps.setString(
                    6,
                    filters.get("visit_start_time_start") != null && !filters.get("visit_start_time_start").toString().trim().equals("")
                        ? filters.get("visit_start_time_start").toString().trim()
                        : "00:00:00"
                );
                
                ps.setString(
                    7,
                    filters.get("visit_start_time_end") != null && !filters.get("visit_start_time_end").toString().trim().equals("")
                        ? filters.get("visit_start_time_end").toString().trim()
                        : "23:59:59"
                );
                
                ps.setString(
                    8,
                    filters.get("visit_end_time_start") != null && !filters.get("visit_end_time_start").toString().trim().equals("")
                        ? filters.get("visit_end_time_start").toString().trim()
                        : "00:00:00"
                );
                
                ps.setString(
                    9,
                    filters.get("visit_end_time_end") != null && !filters.get("visit_end_time_end").toString().trim().equals("")
                        ? filters.get("visit_end_time_end").toString().trim()
                        : "23:59:59"
                );
                
                if(filters.get("eid") != null && !filters.get("eid").toString().trim().equals("")){
                    ps.setString(10, filters.get("eid").toString().trim());
                }
                else{
                    ps.setInt(10, doctorId);
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
                        "SELECT v.visit_id, MAX(last_updated) AS last_updated, visit_date, visit_start_time, visit_end_time, pid, eid " +
                            "FROM ece356.visit AS v " +
                            "INNER JOIN ( " +
                                "SELECT visit_id " +
                                "FROM ece356.advises " +
                                "WHERE doctor_id=? " +
                            ") a ON a.visit_id=v.visit_id " +
                            "WHERE pid=? AND is_valid='1' " +
                            buildVisitsFilters() +
                            "GROUP BY v.visit_id"
                );
                ps.setInt(1, doctorId);
                ps.setInt(2, adviseeId);
                
                ps.setString(
                    3,
                    filters.get("visit_id") != null && !filters.get("visit_id").toString().trim().equals("")
                        ? filters.get("visit_id").toString().trim()
                        : "%"
                );
                
                ps.setDate(
                    4,
                    filters.get("visit_date_start") != null && !filters.get("visit_date_start").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("visit_date_start").toString().trim())
                        : java.sql.Date.valueOf("2000-01-01")
                );
                
                ps.setDate(
                    5,
                    filters.get("visit_date_end") != null && !filters.get("visit_date_end").toString().trim().equals("")
                        ? java.sql.Date.valueOf(filters.get("visit_date_end").toString().trim())
                        : java.sql.Date.valueOf("2100-01-01") 
                );
                  
                ps.setString(
                    6,
                    filters.get("visit_start_time_start") != null && !filters.get("visit_start_time_start").toString().trim().equals("")
                        ? filters.get("visit_start_time_start").toString().trim()
                        : "00:00:00"
                );
                
                ps.setString(
                    7,
                    filters.get("visit_start_time_end") != null && !filters.get("visit_start_time_end").toString().trim().equals("")
                        ? filters.get("visit_start_time_end").toString().trim()
                        : "23:59:59"
                );
                
                ps.setString(
                    8,
                    filters.get("visit_end_time_start") != null && !filters.get("visit_end_time_start").toString().trim().equals("")
                        ? filters.get("visit_end_time_start").toString().trim()
                        : "00:00:00"
                );
                
                ps.setString(
                    9,
                    filters.get("visit_end_time_end") != null && !filters.get("visit_end_time_end").toString().trim().equals("")
                        ? filters.get("visit_end_time_end").toString().trim()
                        : "23:59:59"
                );
                
                ps.setString(
                    10,
                    filters.get("eid") != null && !filters.get("eid").toString().trim().equals("")
                        ? filters.get("eid").toString().trim()
                        : "%"
                );
                
                rs = ps.executeQuery();
               
                visits = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return visits;
            }
            
        }
        
        return visits;
    }
    
    private static String buildVisitsFilters(){
        String filter = " AND v.visit_id LIKE ?";
        filter += " AND visit_date BETWEEN ? AND ?";
        filter += " AND visit_start_time BETWEEN ? AND ?";
        filter += " AND visit_end_time BETWEEN ? AND ?";
        filter += " AND eid LIKE ? ";
        
        return filter;
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
                    preparedStatement = "select p.visit_id, p.last_updated, p.drug_name, p.expires " +
                    "from ece356.prescription p " +
                    "inner join( " +
                    "select visit_id,drug_name, max(last_updated) last_updated " +
                    "from ece356.prescription where visit_id=? and expires > NOW() " +
                    "group by visit_id" +
                    " ) mp on p.last_updated = mp.last_updated and p.visit_id = mp.visit_id;";

                }else{
                    //selects the prescription with the biggest last_updated time
                     preparedStatement = "select  p.visit_id, p.last_updated, p.drug_name, p.expires " +
                    "from ece356.prescription p " +
                    "inner join( " +
                    "select visit_id,drug_name, max(last_updated) last_updated " +
                    "from ece356.prescription where visit_id=? " +
                    "group by visit_id" +
                    " ) mp on p.last_updated = mp.last_updated and p.visit_id = mp.visit_id;";
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
     * @param filter
     * @return a JSONArray describing prescription row(s)
     */
    public static JSONArray getPrescriptionsByVisit(int visitId, String filter){
        
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
                String preparedStatement = "select  p.visit_id, p.last_updated, p.drug_name, p.expires " +
                "from ece356.prescription p " +
                "inner join( " +
                "select visit_id, drug_name, max(last_updated) last_updated " +
                "from ece356.prescription where visit_id=? " +
                " ) mp on p.last_updated = mp.last_updated and p.visit_id = mp.visit_id " +
                "WHERE p.drug_name LIKE ?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                ps.setString(
                    2,
                    !filter.equals("")
                    ? "%" + filter + "%"
                    : "%"
                );
                
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
     * @param filter
     * @return a JSONArray describing procedure row(s)
     */
    public static JSONArray getProcedureByVisit(int visitId, String filter){
        
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
                "group by visit_id" +
                " ) mp on mp.visit_id = p.visit_id and mp.last_updated = p.last_updated and mp.procedure_name = p.procedure_name " +
                "WHERE p.procedure_name LIKE ? OR p.description LIKE ?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                
                ps.setString(
                    2,
                    !filter.equals("")
                    ? "%" + filter + "%"
                    : "%"
                );
                
                ps.setString(
                    3,
                    !filter.equals("")
                    ? "%" + filter + "%"
                    : "%"
                );
                
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
     * @param filter
     * @return a JSONArray describing diagnosis row(s)
     */
    public static JSONArray getDiagnosisByVisit(int visitId, String filter){
        
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
                "group by visit_id" +
                " ) dd on dd.visit_id = d.visit_id and dd.last_updated = d.last_updated " +
                "WHERE d.severity LIKE ?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                
                ps.setString(
                    2,
                    !filter.equals("")
                    ? "%" + filter + "%"
                    : "%"
                );
                
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
     * Queries the database to return a JSONArray of comments for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @param filter
     * @return a JSONArray describing comment row(s)
     */
    public static JSONArray getCommentsByVisit(int visitId, String filter){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray comments = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            
            try{
                ps = connection.prepareStatement(
                            "SELECT * " +
                            "FROM ece356.comment AS c " +
                            "INNER JOIN ( " +
                            "SELECT visit_id, MAX(last_updated) AS last_updated " +
                                "FROM ece356.comment WHERE visit_id=? "+
                            ") mc on mc.visit_id=c.visit_id AND mc.last_updated=c.last_updated " +
                            "WHERE c.content LIKE ?"
                        );
                
                ps.setInt(1, visitId);
                
                ps.setString(
                    2,
                    !filter.equals("")
                    ? "%" + filter + "%"
                    : "%"
                );
                rs = ps.executeQuery();
                comments = convertToJson(rs);
            }
            catch(SQLException e){
                e.printStackTrace();
                return comments;
            }
        }
        
        return comments;
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
    public static JSONArray getVisitsInRange(int patientId, int doctorId, java.sql.Date date1, java.sql.Date date2){
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
    
      public static boolean UpdateRecord(int doctorId, JSONObject params){
        boolean status = true;
        
        if(connection == null){
            status = openConnection();
        }
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            
            try{                
                ps = connection.prepareStatement(
                            "INSERT INTO ece356.visit " +
                            "(visit_id, last_updated, visit_date, visit_start_time, visit_end_time, pid, eid, is_valid) " +
                            "VALUES (?, NOW(), ?, ?, ?, ?, ?, '1')",
                            Statement.RETURN_GENERATED_KEYS
                        );
                
                // Required parameters
                if(params.get("visit_date") == null || params.get("visit_date").toString().trim().equals("") ||
                        params.get("visit_start_time") == null || params.get("visit_start_time").toString().trim().equals("") ||
                        params.get("visit_end_time") == null || params.get("visit_end_time").toString().trim().equals("") ||
                        params.get("vid") == null || params.get("vid").toString().trim().equals("")){
                    return false;
                }
                
                int visitId = Integer.parseInt(params.get("vid").toString().trim());
                
                ps.setInt(1, visitId);
                ps.setDate(2, java.sql.Date.valueOf(params.get("visit_date").toString().trim()));
                ps.setString(3, params.get("visit_start_time").toString().trim());
                ps.setString(4, params.get("visit_end_time").toString().trim());
                ps.setInt(5, Integer.parseInt(params.get("pid").toString().trim()));
                ps.setInt(6, doctorId);
                
                 ;
                
                ps.executeUpdate();
                
                ps = connection.prepareStatement(
                            "SELECT max(last_updated) as last_updated " +
                            "FROM ece356.visit WHERE visit_id=?"
                        );
                
                ps.setInt(1, visitId);
                rs = ps.executeQuery();
                
                JSONObject insertedVisit = convertRowToJson(rs);
                
                
                java.sql.Timestamp lastUpdated = java.sql.Timestamp.valueOf(insertedVisit.get("last_updated").toString().trim());
                
                // Insert procedure for new visit
                if(params.get("procedure_name") != null && !params.get("procedure_name").toString().trim().equals("") &&
                        params.get("description") != null && !params.get("description").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                                "INSERT INTO ece356.procedure " +
                                "(visit_id, last_updated, procedure_name, description) " +
                                "VALUES (?, ?, ?, ?)"
                            );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setString(3, params.get("procedure_name").toString().trim());
                    ps.setString(4, params.get("description").toString().trim());
                }
                 ;
                ps.executeUpdate();
                
                // Insert diagnosis for new visit
                if(params.get("severity") != null && !params.get("severity").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                            "INSERT INTO ece356.diagnosis " +
                            "(visit_id, last_updated, severity) " +
                            "VALUES (?, ?, ?)"
                        );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setString(3, params.get("severity").toString().trim());
                }
                 ;
                ps.executeUpdate();
                
                // Insert comment for new visit
                if(params.get("content") != null && !params.get("content").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                                "INSERT INTO ece356.comment " +
                                "(visit_id, last_updated, eid, timestamp, content) " +
                                "VALUES (?, ?, ?, ?, ?)"
                            );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setInt(3, doctorId);
                    ps.setTimestamp(4, lastUpdated);
                    ps.setString(5, params.get("content").toString().trim());
                     ;
                    ps.executeUpdate();
                }
                
                // Insert prescriptions
                
                if(params.get("prescriptions") != null){
                    JSONArray prescriptions = (JSONArray) params.get("prescriptions");
                    
                    for(int i = 0; i < prescriptions.size(); i++){
                        JSONObject prescription = (JSONObject) prescriptions.get(i);
                        
                        ps = connection.prepareStatement(
                                    "INSERT INTo ece356.prescription " +
                                    "(visit_id, last_updated, drug_name, expires) " +
                                    "VALUES (?, ?, ?, ?)"        
                                );
                        
                        ps.setInt(1, visitId);
                        ps.setTimestamp(2, lastUpdated);
                        ps.setString(3, prescription.get("drug_name").toString());
                        ps.setDate(4, java.sql.Date.valueOf(prescription.get("expires").toString().trim()));
                         ;
                        ps.executeUpdate();
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean InsertNewRecord(int doctorId, JSONObject params){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }

        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            
            try{                
                ps = connection.prepareStatement(
                            "INSERT INTO ece356.visit " +
                            "(last_updated, visit_date, visit_start_time, visit_end_time, pid, eid, is_valid) " +
                            "VALUES (NOW(), ?, ?, ?, ?, ?, '1')",
                            Statement.RETURN_GENERATED_KEYS
                        );
                
                // Required parameters
                if(params.get("visit_date") == null || params.get("visit_date").toString().trim().equals("") ||
                        params.get("visit_start_time") == null || params.get("visit_start_time").toString().trim().equals("") ||
                        params.get("visit_end_time") == null || params.get("visit_end_time").toString().trim().equals("") ||
                        params.get("pid") == null || params.get("pid").toString().trim().equals("")){
                    return false;
                }
                
                ps.setDate(1, java.sql.Date.valueOf(params.get("visit_date").toString().trim()));
                ps.setString(2, params.get("visit_start_time").toString().trim());
                ps.setString(3, params.get("visit_end_time").toString().trim());
                ps.setInt(4, Integer.parseInt(params.get("pid").toString().trim()));
                ps.setInt(5, doctorId);
                
                ps.executeUpdate();
                
                ps = connection.prepareStatement(
                            "SELECT v.visit_id, v.last_updated " +
                            "FROM ece356.visit AS v " +
                            "INNER JOIN ( " +
                                "SELECT MAX(visit_id) AS visit_id " +
                                "FROM ece356.visit " +
                            ") AS mv on v.visit_id=mv.visit_id"
                        );
                
                rs = ps.executeQuery();
                
                JSONObject insertedVisit = convertRowToJson(rs);
                
                int visitId = Integer.parseInt(insertedVisit.get("visit_id").toString().trim());
                java.sql.Timestamp lastUpdated = java.sql.Timestamp.valueOf(insertedVisit.get("last_updated").toString().trim());
                
                // Insert procedure for new visit
                if(params.get("procedure_name") != null && !params.get("procedure_name").toString().trim().equals("") &&
                        params.get("description") != null && !params.get("description").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                                "INSERT INTO ece356.procedure " +
                                "(visit_id, last_updated, procedure_name, description) " +
                                "VALUES (?, ?, ?, ?)"
                            );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setString(3, params.get("procedure_name").toString().trim());
                    ps.setString(4, params.get("description").toString().trim());
                }
                
                ps.executeUpdate();
                
                // Insert diagnosis for new visit
                if(params.get("severity") != null && !params.get("severity").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                            "INSERT INTO ece356.diagnosis " +
                            "(visit_id, last_updated, severity) " +
                            "VALUES (?, ?, ?)"
                        );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setString(3, params.get("severity").toString().trim());
                }
                
                ps.executeUpdate();
                
                // Insert comment for new visit
                if(params.get("content") != null && !params.get("content").toString().trim().equals("")){
                    ps = connection.prepareStatement(
                                "INSERT INTO ece356.comment " +
                                "(visit_id, last_updated, eid, timestamp, content) " +
                                "VALUES (?, ?, ?, ?, ?)"
                            );

                    ps.setInt(1, visitId);
                    ps.setTimestamp(2, lastUpdated);
                    ps.setInt(3, doctorId);
                    ps.setTimestamp(4, lastUpdated);
                    ps.setString(5, params.get("content").toString().trim());

                    ps.executeUpdate();
                }
                
                // Insert prescriptions
                
                if(params.get("prescriptions") != null){
                    JSONArray prescriptions = (JSONArray) params.get("prescriptions");
                    
                    for(int i = 0; i < prescriptions.size(); i++){
                        JSONObject prescription = (JSONObject) prescriptions.get(i);
                        
                        ps = connection.prepareStatement(
                                    "INSERT INTo ece356.prescription " +
                                    "(visit_id, last_updated, drug_name, expires) " +
                                    "VALUES (?, ?, ?, ?)"        
                                );
                        
                        ps.setInt(1, visitId);
                        ps.setTimestamp(2, lastUpdated);
                        ps.setString(3, prescription.get("drug_name").toString());
                        ps.setDate(4, java.sql.Date.valueOf(prescription.get("expires").toString().trim()));
                        
                        ps.executeUpdate();
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        
        return true;
    }

    public static JSONArray getPatients()
    {
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray users = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //get doctors that manages this staff
                String preparedStatement = "select pid, fname, lname from ece356.patient where is_enabled = 1;";
                ps = connection.prepareStatement(preparedStatement);
                
                rs = ps.executeQuery();
                users = convertToJson(rs);
                
                
              return users;
            }catch(SQLException e){
                e.printStackTrace();
                return users;
            }
            
        }
        
        return users;
    }
    
    public static JSONObject getPatientByStaff(int eid, int pid)
    {
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
                //get doctors that manages this staff
                String preparedStatement = "SELECT * FROM (SELECT patient_id FROM (SELECT * FROM (SELECT doctor_id as ID from ece356.`managed-by` where staff_id = ?) a join ece356.`patient-of` b on a.ID = b.doctor_id) as patients where patient_id = ?) c join ece356.patient d on c.patient_id = d.pid;";
                ps = connection.prepareStatement(preparedStatement);
                
                ps.setInt(1, eid);
                ps.setInt(2, pid);
                
                rs = ps.executeQuery();
                patient = convertRowToJson(rs);
                
                
              return patient;
            }catch(SQLException e){
                e.printStackTrace();
                return patient;
            }
            
        }

        return patient;
    }
    
    public static JSONArray getAppointmentByPatient(int pid)
    {
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray appointments = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //get current date and time
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                //get doctors that manages this staff
                String preparedStatement = "SELECT visit_id, visit_date, visit_start_time, visit_end_time, eid from ece356.visit where pid = ? and is_valid = 1 and (visit_date between \'%s\' and \'9999-12-31\' or (visit_date = \'%s\' and visit_start_time between \'%s\' and \'23:59:59\'));";
                ps = connection.prepareStatement(String.format(preparedStatement, date, date, time));
                ps.setInt(1, pid);
                
                rs = ps.executeQuery();
                appointments = convertToJson(rs);
                
                
              return appointments;
            }catch(SQLException e){
                e.printStackTrace();
                return appointments;
            }
            
        }
        
        return appointments;
    }
    
    public static JSONArray getAppointmentByDoctor(int eid)
    {
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray appointments = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //get current date and time
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                
                //get doctors that manages this staff
                String preparedStatement = "SELECT visit_id, visit_date, visit_start_time, visit_end_time, pid from ece356.visit where eid = ? and is_valid = 1 and (visit_date between \'%s\' and \'9999-12-31\' or (visit_date = \'%s\' and visit_start_time between \'%s\' and \'23:59:59\'));";
                ps = connection.prepareStatement(String.format(preparedStatement, date, date, time));
                ps.setInt(1, eid);
                
                rs = ps.executeQuery();
                appointments = convertToJson(rs);
                
                
              return appointments;
            }catch(SQLException e){
                e.printStackTrace();
                return appointments;
            }
            
        }
        
        return appointments;
    }
    
    public static boolean assignPatientToDoctor(int doctorId, int patientId){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
         if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{

                String preparedStatement = "INSERT ece356.`patient-of` (`doctor_id`, `patient_id`) VALUES (?, ?);";
                ps = connection.prepareStatement(preparedStatement);
                
                ps.setInt(1, doctorId);
                ps.setInt(2, patientId);
                if(ps.executeUpdate()== 1 ){
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
    
    public static boolean updatePatientInformation(int patientId, JSONObject params){
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
                preparedStatement += " WHERE pid=? AND is_enabled=1";
                ps = connection.prepareStatement(preparedStatement);
                
                ps.setInt(1, patientId);
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
    
    public static boolean bookAppointment(JSONObject params){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
         if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                //check conflict
                if (checkValidAppointmentTime(params.get("date").toString() + " " + params.get("starttime").toString(), 
                        params.get("date").toString() + " " + params.get("endtime").toString())){
                
                    //check conflict
                    String preparedStatement = getConflictCheckingStatement(params);
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, Integer.parseInt(params.get("eid").toString()));
                    rs = ps.executeQuery();
                    if (!convertToJson(rs).isEmpty())
                    {
                        //conflict
                        return false;
                    }
                    preparedStatement = "INSERT INTO `ece356`.`visit` (`visit_date`, `visit_start_time`, `visit_end_time`, `pid`, `eid`, `is_valid`) VALUES "
                            +"('"+ params.get("date") +"', '" +
                            params.get("starttime") + "', '" +
                            params.get("endtime") +"', '" +
                            params.get("pid") +"', '"+
                            params.get("eid") +"', '1');";
                    ps = connection.prepareStatement(preparedStatement);

                    if(ps.executeUpdate() == 1 ){
                        //success
                        return true;
                    }else if(ps.executeUpdate() > 1){
                        //something went terribly wrong
                    }else{
                        return false;
                    }
                }
               
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        return false;
    }
    
    public static boolean newPatient(int eid, JSONObject params){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
         if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{

                String preparedStatement = "INSERT INTO `ece356`.`patient` (`password`, `fname`, `lname`, `is_enabled`, `street_number`, `street`, `city`, `post_code`, `sin`, `healthcard_number`, `num_visits`, `current_health`) VALUES "
                        +"('"+ params.get("password") +"', '" +
                        params.get("fname") + "', '" +
                        params.get("lname") +"', '1', '" +
                        params.get("street_number") +"', '"+
                        params.get("street") +"', '"+
                        params.get("city") +"', '"+
                        params.get("post_code") +"', '"+
                        params.get("sin") +"', '"+
                        params.get("healthcard_number") + "', '0', 'In Good Health');";
                ps = connection.prepareStatement(preparedStatement);
                
                if(ps.executeUpdate() == 1 ){
                    //success
                    preparedStatement = "SELECT pid FROM `ece356`.`patient` where `password`=\'"+params.get("password")+"\' AND `fname`=\'"+params.get("fname")+"\' AND `lname`=\'"+params.get("lname")
                            +"\' AND `street_number`=\'"+params.get("street_number")+"\' AND `street`=\'"+params.get("street")+"\' AND `city`=\'"+params.get("city")+"\' AND `post_code`=\'"+params.get("post_code")
                                    +"\' AND `sin`=\'"+params.get("sin")+"\' AND `healthcard_number`=\'"+params.get("healthcard_number")+"\';";
                    ps = connection.prepareStatement(preparedStatement);
                    rs = ps.executeQuery();
                    JSONObject value = convertRowToJson(rs);
                    
                    preparedStatement = "INSERT INTO `ece356`.`patient-of` (`doctor_id`, `patient_id`) VALUES (?, ?);";
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, eid);
                    ps.setInt(2, Integer.parseInt(value.get("pid").toString()));
                    ps.executeUpdate();
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
    
    public static boolean editAppointment(JSONObject params){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
         if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                
                //check validity of data
                if (checkValidAppointmentTime(params.get("date").toString() + " " + params.get("starttime").toString(), 
                        params.get("date").toString() + " " + params.get("endtime").toString())){
                
                    //check conflict
                    String preparedStatement = getConflictCheckingStatement(params);
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, Integer.parseInt(params.get("eid").toString()));
                    rs = ps.executeQuery();
                    JSONArray apps = convertToJson(rs);
                    if (!apps.isEmpty())
                    {
                        if (apps.size() == 1 && ((JSONObject)apps.get(0)).get("visit_id").equals(params.get("vid")))
                        {
                            //conflict with itself, ignore
                        }
                        else
                        {
                            //conflict
                            return false;
                        }
                    }

                    preparedStatement = "UPDATE `ece356`.`visit` SET " +
                            "`is_valid`=\'0\' WHERE `visit_id`=\'" + params.get("vid") + "\';";
                    ps = connection.prepareStatement(preparedStatement);

                    if(ps.executeUpdate() == 1 ){
                        //success

                        return bookAppointment(params);
                    }else if(ps.executeUpdate() > 1){
                        //something went terribly wrong
                    }else{
                        return false;
                    }
                }
                else
                {
                    return false;
                }
               
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        return false;
    }
    
    private static boolean checkValidAppointmentTime(String starttime, String endtime)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            return df.parse(starttime).after(new Date()) && df.parse(starttime).before(df.parse(endtime));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private static String getConflictCheckingStatement(JSONObject params)
    {
        return "SELECT * FROM ece356.visit where `visit_date`=\'"
                            + params.get("date") + "\' and eid=? and (visit_start_time between \'"
                            + params.get("starttime") + "\' and \'"
                            + params.get("endtime") + "\' or visit_end_time between \'"
                            + params.get("starttime") + "\' and \'"
                            + params.get("endtime") + "\' or (visit_start_time between \'00:00:00\' and \'"
                            + params.get("starttime") + "\' and visit_end_time between \'"
                            + params.get("endtime") + "\' and \'23:59:59\')) and is_valid = 1;";
    }

    /**
     * Queries database for historyTrail about a particular visit
     * @param visitId
     * @return A JSONObject describing a visit row
     */
    public static JSONArray getHistoryTrail(int visitId){
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray history = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                ps = connection.prepareStatement(
                       "SELECT visit_id, last_updated"+
                       " FROM ece356.visit WHERE visit_id=? ");
                ps.setInt(1, visitId);
                rs = ps.executeQuery();
               
                history = convertToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return history;
            }
            
        }
        return history;
    }
    
    /**
     * Queries the database to return a JSONArray of prescriptions perscribed for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @param lastUpdated
     * @return a JSONArray describing prescription row(s)
     */
    public static JSONArray getPrescriptionsAudit(int visitId, String lastUpdated){
        
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
                String preparedStatement = "SELECT * " +
                "FROM ece356.prescription WHERE visit_id=? AND last_updated=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(lastUpdated));
                
                
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
     * Queries the database to return a JSONArray of comments for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @param lastUpdated
     * @return a JSONArray describing prescription row(s)
     */
    public static JSONArray getCommentsAudit(int visitId, String lastUpdated){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray comments = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "SELECT * " +
                "FROM ece356.comment WHERE visit_id=? AND last_updated=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(lastUpdated));
                
                
                rs = ps.executeQuery();
                comments = convertToJson(rs);
                
                
              return comments;
            }catch(SQLException e){
                e.printStackTrace();
                return comments;
            }
            
        }
        
        return comments;
        
    }
    
        /**
     * Queries the database to return a JSONArray of procedures for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @param lastUpdated
     * @return a JSONArray describing prescription row(s)
     */
    public static JSONArray getProcedureAudit(int visitId, String lastUpdated){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray procedure = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "SELECT * " +
                "FROM ece356.procedure WHERE visit_id=? AND last_updated=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(lastUpdated));
                
                
                rs = ps.executeQuery();
                procedure = convertToJson(rs);
                
                
              return procedure;
            }catch(SQLException e){
                e.printStackTrace();
                return procedure;
            }
            
        }
        
        return procedure;
        
    }
    
    /**
     * Queries the database to return a JSONArray of diagnostics for a given visit
     * The query only considers the most up-to-date record of the visit.
     * @param visitId
     * @param lastUpdated
     * @return a JSONArray describing prescription row(s)
     */
    public static JSONArray getDiagnosisAudit(int visitId, String lastUpdated){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray diagnostic = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "SELECT * " +
                "FROM ece356.diagnosis WHERE visit_id=? AND last_updated=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(lastUpdated));
                
                
                rs = ps.executeQuery();
                diagnostic = convertToJson(rs);
                
                
              return diagnostic;
            }catch(SQLException e){
                e.printStackTrace();
                return diagnostic;
            }
            
        }
        
        return diagnostic;
        
    }
    
    
    public static JSONArray getAdivsorsOf(int visitId){
         boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray advisors = new JSONArray();
        
        if(status){
            PreparedStatement ps;
            Statement s;
            ResultSet rs;
            try{
                String preparedStatement = "SELECT DISTINCT ece356.advises.doctor_id, ece356.advises.visit_id, ece356.employee.fname, ece356.employee.lname " +
                "FROM ece356.advises INNER JOIN ece356.employee ON ece356.advises.doctor_id = ece356.employee.eid WHERE visit_id=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);  
                
                
                rs = ps.executeQuery();
                advisors = convertToJson(rs);
                
                
              return advisors;
            }catch(SQLException e){
                e.printStackTrace();
                return advisors;
            }
            
        }
        
        return advisors;
    }
    
    public static boolean removeAdvisor(int doctorId, int visitId){
         boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        
        if(status){
            PreparedStatement ps;
            Statement s;
            try{
                String preparedStatement = "DELETE " +
                "FROM ece356.advises WHERE doctor_id=? AND visit_id=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, doctorId);
                ps.setInt(2, visitId);  
                
                
                
                if(ps.executeUpdate() >= 0){
                    return true;
                }
                
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        
        return false;
    }
    
        public static boolean addAdvisorFor(int doctorId, int visitId){
         boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        JSONArray revisions;
        
        if(status){
            PreparedStatement ps;
            ResultSet rs;
            Statement s;
            try{
                String preparedStatement = "SELECT last_updated " +
                "FROM ece356.visit WHERE visit_id=?";
                
                ps = connection.prepareStatement(preparedStatement);
                ps.setInt(1, visitId);  
                
                rs = ps.executeQuery();
                
                revisions = convertToJson(rs);
                
                for(int i = 0; i < revisions.size(); i++){
                    preparedStatement = "INSERT INTO ece356.advises" +
                        " VALUES (?, ?, ?)";
                
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, doctorId);
                    ps.setInt(2, visitId);
                    String lastUpdated = ((JSONObject)revisions.get(i)).get("last_updated").toString();
                    ps.setTimestamp(3, java.sql.Timestamp.valueOf(lastUpdated));
                    ps.executeUpdate();
                }
                
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        
        return false;
    }
}
