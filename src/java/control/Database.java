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

/**
 *
 * @author Kyle
 */
public class Database {
    
    public static final String url ="jdbc:mysql://198.50.229.147:3306/";
    public static final String user = "ece356";
    public static final String pass = "database";
    public static final String db = "ece356.";
    
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
    
    //Tested as working with login 1, password test
    public static boolean verifyUserLogin(String table, String id, String pw){
        boolean status = true;
        String idtype = (table.equals( "patient" )) ? "pid":"eid";
        if(connection == null){
            status = openConnection();
        }
        
        if(status){
            Statement s;
            ResultSet rs;
            try{
                s = connection.createStatement();
                rs = s.executeQuery("SELECT * FROM " + db + table + " WHERE " + 
                    idtype+"=" + id + " AND " + "password='" + pw + "'");
               
                if(!rs.next()){
                    return false; //no such user or password combo
                }else{
                    return true; //user logged in
                }
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            
        }
        
        closeConnection();
        return false; //something wrong with the connection        
    }
    
    /**
     * 
     * @param id
     * @param pw
     * @return A String[] with the following info: 
     * { id, fname, lname, st n., street, city, postcode, sin, num_visits, c_health }
     */
    public static String[] patientLogin(String id, String pw){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        if(status){
            Statement s;
            ResultSet rs;
            try{
                s = connection.createStatement();
                rs = s.executeQuery("SELECT * FROM " + db + "patient" + " WHERE " + 
                    "pid=" + id + " AND " + "password='" + pw + "' and is_enabled=1");
               
                if(!rs.next()){
                    return new String[0]; //no such user or password combo
                }else{
                    return new String[]{
                        id,
                        rs.getString("fname"),
                        rs.getString("lname"),
                        Integer.toString(rs.getInt("street_number")),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("post_code"),
                        Integer.toString(rs.getInt("sin")),
                        Integer.toString(rs.getInt("num_visits")),
                        rs.getString("current_health")
                    };
                }
            }catch(SQLException e){
                e.printStackTrace();
                return new String[0];
            }
            
        }
        
        closeConnection();
        return new String[0]; //something wrong with the connection    
        
    }
    
    /**
     * 
     * @param id
     * @param pw
     * @return A String[] with the following info: 
     * { id, fname, lname, dept }
     */
    public static String[] employeeLogin(String id, String pw){
        
        boolean status = true;
        if(connection == null){
            status = openConnection();
        }
        
        if(status){
            Statement s;
            ResultSet rs;
            try{
                s = connection.createStatement();
                rs = s.executeQuery("SELECT * FROM " + db + "employee" + " WHERE " + 
                    "eid=" + id + " AND " + "password='" + pw + "' and is_enabled=1");
               
                if(!rs.next()){
                    return new String[0]; //no such user or password combo
                }else{
                    return new String[]{
                        id,
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("dept")
                    };
                }
            }catch(SQLException e){
                e.printStackTrace();
                return new String[0];
            }
            
        }
        
        closeConnection();
        return new String[0]; //something wrong with the connection    
        
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
                ps = connection.prepareStatement("SELECT * FROM ?patient-of WHERE doctor_id=?");
                
                ps.setString(1, db);
                ps.setInt(2, doctorId);
                
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
                closeConnection();
                return patients;
            }
            
        }
        
        closeConnection();
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
                ps = connection.prepareStatement("SELECT * FROM ?patient WHERE pid=? AND is_enabled=1");
                
                ps.setString(1, db);
                ps.setInt(2, patientId);
                
                rs = ps.executeQuery();
               
                patient = convertRowToJson(rs);
                
            }catch(SQLException e){
                e.printStackTrace();
                return patient;
            }
            
        }
        
        return patient;
        
    }
}
