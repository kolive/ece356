/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package control;

import java.sql.*;

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
        
        return new String[0]; //something wrong with the connection    
        
    }
    
    
}
