/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Kyle
 */
public class User {
    
    public enum UserType{ PATIENT, DOCTOR, STAFF, FAUDITOR, LAUDITOR };
    
    JSONObject info;
    UserType userType;
    
    
    public User(JSONObject info, UserType userType){
        this.info = info;
        this.userType = userType;
        System.out.println(info.keySet());
    }
    
    public String getStringParam(String name){
        return info.get(name).toString();
    }
    public UserType getUserType(){
        return userType;
    }
    
}
