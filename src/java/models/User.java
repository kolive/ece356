/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author Kyle
 */
public class User {
    
    public enum UserType{ PATIENT, DOCTOR, STAFF, FAUDITOR, LAUDITOR };
    
    String userId;
    String fname;
    String lname;
    UserType userType;
    
    
    public User(String userId, String fname, String lname, UserType userType){
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.userType = userType;
    }
    
    public UserType getUserType(){
        return userType;
    }
    
}
