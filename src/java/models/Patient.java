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
public class Patient extends User{
    
    String streetNumber;
    String street;
    String city;
    String postCode;
    String sin;
    String numVisits;
    String currentHealth;
    
    public Patient(String[] details){
        super(details[0], details[1], details[2], User.UserType.PATIENT);
        
        streetNumber = details[3];
        street = details[4];
        city = details[5];
        postCode = details[6];
        sin = details[7];
        numVisits = details[8];
        currentHealth = details[9];
        
    }
    
    public String stringify(){
        return "Patient #: " + this.userId + "'s name is: " +
                this.fname + " " + this.lname +
                " They live at : " +
                this.streetNumber + " " + this.street + ", " + this.city + ", " + this.postCode +
                " they have had " + this.numVisits + " visits. " +
                " Current diagnosis: " + this.currentHealth;
    }
    
}
