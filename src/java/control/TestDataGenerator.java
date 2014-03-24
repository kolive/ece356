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
public class TestDataGenerator {

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
    
    public static void run(){
        deleteAll();
        generatePatients();
        generateEmployees();        
        generateManagedBy();
        generatePatientOf();
        generateDrugs();
        
    }
    
    public static void deleteAll(){
        openConnection();
        try{
            Statement s = connection.createStatement();
            
            System.out.println("deleting managed-by");
            s.addBatch("delete from ece356_test.`managed-by`");
            
            System.out.println("deleting patient-of");
            s.addBatch("delete from ece356_test.`patient-of`");
            
            System.out.println("deleting patients");
            s.addBatch("delete from ece356_test.patient"); 
            s.addBatch("ALTER TABLE ece356_test.patient AUTO_INCREMENT = 1");
            
            System.out.println("deleting staff");
            s.addBatch("delete from ece356_test.employee");
            s.addBatch("ALTER TABLE ece356_test.employee AUTO_INCREMENT = 1");
               
            System.out.println("deleting drugs");
            s.addBatch("delete from ece356_test.drug");
            
            s.executeBatch();
        }catch(SQLException e){
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }
    
    public static void generateVisits(){
        //each patient has had 1 to 5 visits
        for(int i = 0; i < 150; i++){
            //required information for each visit
        }
    }
    
    public static void generatePatientOf(){
        /*
         `doctor_id` INT NOT NULL,
         `patient_id` INT NOT NULL,

        */
        openConnection();
        String preparedStatement = "INSERT INTO ece356_test.`patient-of` VALUES (?, ?)";
        try{
            PreparedStatement ps = connection.prepareStatement(preparedStatement);
            //each doctor gets 2 patients
            int patientId = 1;
            for(int i = 0; i < 160; i++){
                //for each doctor
                if( i%10 != 8 && (i%10)%2 == 0){
                    //assign two patients
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, i+1); //set doctor id
                    ps.setInt(2, patientId); //patient id
                    ps.executeUpdate();
                    patientId++;
                    ps = connection.prepareStatement(preparedStatement);
                    ps.setInt(1, i+1); //set doctor id
                    ps.setInt(2, patientId); //patient id
                    ps.executeUpdate();
                    patientId++;
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }
    
    public static void generateDrugs(){
        /*
            `drug_name` VARCHAR(100) NOT NULL,
            `manufacturer` VARCHAR(100) NULL,


        */
        
        openConnection();
        
        String[] drug = new String[]{
            // <editor-fold desc="drug list">
            "Advil",
            "Sickbegone",
            "GoodFeelz",
            "Hypophorestus",
            "Male Enlargement Pills",
            "Female Enlargement Pills",
            "The One Pill To Rule Them All",
            "Cialis",
            "Insulin",
            "Morphine",
            "Oxytocin",
            "Vicodin",
            "Nitrite",
            "Anti-cancer Pills",
            "DoNotDie",
            "Sideeffectz",
            "Mooduplift",
            "Diagopropinate",
            "Digestion Cookies",
            "Exlax",
            "Penicillin",
            "Wart-b-gone",
            "NoMoCrabs",
            "Cure For The Common Cold"
            //</editor-fold>
        };
        
        String[] mfg = new String[]{
            //<editor-fold desc="lname list">
            "DrugCo",
            "BigPharma",
            "LittlePharma",
            "Heisenberg",
            "Los Pollos Hermanos"
            //</editor-fold>
        };
        
        String preparedStatement = "INSERT INTO ece356_test.drug" +   
                " VALUES (?, ?);";
        //every doctor has one staff member, so there should be one staff member per doctor
        //for every 4 doctors, have one legal auditor and one finance auditor
        // i%10 = 0,2,4,6 doctor, i%10 = 1,3,5,7 staff, i%10 = 8,9 auditors
        try{
            PreparedStatement ps = connection.prepareStatement(preparedStatement);
            String type ="";
            for(int i = 0; i < drug.length; i++){
                ps = connection.prepareStatement(preparedStatement);
                ps.setString(1, drug[i]);
                ps.setString(2, mfg[(int)(Math.random()*mfg.length)]);
                
                ps.execute();
            }
            //ps.executeBatch();
        }catch(SQLException e){
            //do something
            e.printStackTrace();
            closeConnection();
        }
        
        closeConnection();
    }
    
    public static void generateManagedBy(){
        openConnection();
        String preparedStatementManaged = "INSERT INTO ece356_test.`managed-by` VALUES (?, ?)";
        
        try{
            PreparedStatement managed = connection.prepareStatement(preparedStatementManaged);

            for(int i = 0; i < 150; i++){
                if(i%10 != 8 && (i%10)%2 == 0){     
                    managed = connection.prepareStatement(preparedStatementManaged);
                    //set managed-by relation
                    managed.setInt(1, i+2);
                    managed.setInt(2, i+1);
                    managed.execute();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }
    
    public static void generateEmployees(){
        /*
           `eid` INT NOT NULL AUTO_INCREMENT,
            `password` VARCHAR(32) NULL,
            `fname` VARCHAR(45) NULL,
            `lname` VARCHAR(45) NULL,
            `is_enabled` TINYINT(1) NULL,
            `dept` VARCHAR(45) NULL,

        */
        
        openConnection();
        
        String password = "default";
        String[] fnames = new String[]{
            // <editor-fold desc="fname list">

            "Shaun",
            "Laurel",
            "Giuseppe",
            "Kaitlin",
            "Dannette",
            "Emery",
            "Twanda",
            "Jenell",
            "Marge",
            "Kathrine",
            "Buford",
            "Cathryn",
            "Elisha",
            "Jin",
            "Genoveva",
            "Martine",
            "Kala",
            "Barbra",
            "Solange",
            "Bruce",
            "Terresa",
            "Eladia",
            "Lucretia",
            "Isabella",
            "Hedwig",
            "Katharina",
            "Lakita",
            "Trena",
            "Josef",
            "Adelina",
            "Amos",
            "Dorethea",
            "Giovanni",
            "Eva",
            "Bart",
            "Veta",
            "Arie",
            "Selina",
            "Veronika",
            "Willard",
            "Lakeesha",
            "Alicia",
            "Rochelle",
            "Deborah",
            "Filiberto",
            "Tomika",
            "Parker",
            "Isobel",
            "Shelton",
            "Candida"
                
            //</editor-fold>
        };
        
        String[] lnames = new String[]{
            //<editor-fold desc="lname list">
            "Tome",
            "Buber",
            "Morduch",
            "Buerle",
            "Demirjian",
            "Nanna",
            "Bussini",
            "Koh",
            "Biagioli",
            "Boyajian",
            "Ball",
            "Siesto",
            "Valli",
            "Bartlett",
            "Rennolls",
            "Ives",
            "Lettvin",
            "Train",
            "Vannelli",
            "Gall",
            "Schachter",
            "Kang",
            "D'aristotle",
            "Dial",
            "Hawkes",
            "Krol",
            "Bommarito",
            "Reza",
            "Disalvo",
            "Jerome",
            "Marple",
            "Jacobs",
            "Burg",
            "Esty",
            "Huang",
            "Glanzman",
            "Dart",
            "Serino",
            "Meister",
            "Yatsko",
            "Warner",
            "Fuhring",
            "Feinerman",
            "Mayne",
            "Woerne",
            "Piediscalzi",
            "Duesenberry",
            "Wadzinski",
            "Clow",
            "Ledford",
            "Pater",
            "Donaghey",
            "Honner-white"
            //</editor-fold>
        };
        
        String preparedStatement = "INSERT INTO ece356_test.employee (password, fname, lname, dept, is_enabled) " +   
                " VALUES (?, ?, ?, ?, 1);";
       
        //every doctor has one staff member, so there should be one staff member per doctor
        //for every 4 doctors, have one legal auditor and one finance auditor
        // i%10 = 0,2,4,6 doctor, i%10 = 1,3,5,7 staff, i%10 = 8,9 auditors
        try{
            PreparedStatement ps = connection.prepareStatement(preparedStatement);
            String type ="";
            for(int i = 0; i < 160; i++){
                ps = connection.prepareStatement(preparedStatement);
                ps.setString(1, password);
                ps.setString(2, fnames[(int)(Math.random()*fnames.length)]);
                ps.setString(3, lnames[(int)(Math.random()*lnames.length)]);
                if( i%10 != 8 && (i%10)%2 == 0){
                    //doctor
                    type = "DOCTOR";
                }else if(i%10 != 9 && (i%10)%2 == 1){
                    //staff
                    type = "STAFF";

                }else if(i%10 == 9){
                    //legal
                    type = "LEGAL";
                }else{
                    //finance
                    type = "FINANCE";
                }
               
                                
                ps.setString(4, type);
                ps.execute();

            }
            //ps.executeBatch();
        }catch(SQLException e){
            //do something
            e.printStackTrace();
            closeConnection();
        }
        
        closeConnection();
    }
    
    
    public static void generatePatients(){
        /*
         `pid` INT NOT NULL AUTO_INCREMENT,
        `password` VARCHAR(32) NOT NULL,
        `fname` VARCHAR(45) NOT NULL,
        `lname` VARCHAR(45) NOT NULL,
        `is_enabled` TINYINT(1) NULL,
        `street_number` INT NULL,
        `street` VARCHAR(45) NULL,
        `city` VARCHAR(45) NULL,
        `post_code` VARCHAR(6) NULL,
        `sin` INT UNSIGNED NULL,
        `num_visits` INT UNSIGNED NULL,
        `current_health` VARCHAR(100) NULL,
        */
        
        openConnection();
        
        String password = "default";
        String[] fnames = new String[]{
            // <editor-fold desc="fname list">

            "Shaun",
            "Laurel",
            "Giuseppe",
            "Kaitlin",
            "Dannette",
            "Emery",
            "Twanda",
            "Jenell",
            "Marge",
            "Kathrine",
            "Buford",
            "Cathryn",
            "Elisha",
            "Jin",
            "Genoveva",
            "Martine",
            "Kala",
            "Barbra",
            "Solange",
            "Bruce",
            "Terresa",
            "Eladia",
            "Lucretia",
            "Isabella",
            "Hedwig",
            "Katharina",
            "Lakita",
            "Trena",
            "Josef",
            "Adelina",
            "Amos",
            "Dorethea",
            "Giovanni",
            "Eva",
            "Bart",
            "Veta",
            "Arie",
            "Selina",
            "Veronika",
            "Willard",
            "Lakeesha",
            "Alicia",
            "Rochelle",
            "Deborah",
            "Filiberto",
            "Tomika",
            "Parker",
            "Isobel",
            "Shelton",
            "Candida"
                
            //</editor-fold>
        };
        
        String[] lnames = new String[]{
            //<editor-fold desc="lname list">
            "Tome",
            "Buber",
            "Morduch",
            "Buerle",
            "Demirjian",
            "Nanna",
            "Bussini",
            "Koh",
            "Biagioli",
            "Boyajian",
            "Ball",
            "Siesto",
            "Valli",
            "Bartlett",
            "Rennolls",
            "Ives",
            "Lettvin",
            "Train",
            "Vannelli",
            "Gall",
            "Schachter",
            "Kang",
            "D'aristotle",
            "Dial",
            "Hawkes",
            "Krol",
            "Bommarito",
            "Reza",
            "Disalvo",
            "Jerome",
            "Marple",
            "Jacobs",
            "Burg",
            "Esty",
            "Huang",
            "Glanzman",
            "Dart",
            "Serino",
            "Meister",
            "Yatsko",
            "Warner",
            "Fuhring",
            "Feinerman",
            "Mayne",
            "Woerne",
            "Piediscalzi",
            "Duesenberry",
            "Wadzinski",
            "Clow",
            "Ledford",
            "Pater",
            "Donaghey",
            "Honner-white"
            //</editor-fold>
        };
        
        String[] streets = new String[]{
            //<editor-fold desc="street names">
            "King Street",
            "Circle Crescent",
            "Queen Road",
            "Main Street",
            "Second Street",
            "Fifth Street",
            "Shack Lane",
            "Moneybags Drive",
            "Honeypot Lane",
            "Big Boulevard",
            "Small Street",
            "Large Lane",
            "Roundabout Way",
            "Shortcut Street"
            //</editor-fold>
        };
        
        String[] cities = new String[]{
            //<editor-fold desc="city names">
            "Toronto",
            "Brampton",
            "Waterloo",
            "Kitchener",
            "Barrie",
            "Missisauga",
            "Markham"
            //</editor-fold>
        };
        
        String[] postcode = new String[]{
          //<editor-fold desc="postcodes">
            "L5F3D3",
            "F4S2D6",
            "G6GH7J",
            "L3L9F9",
            "U8IS92",
            "L9X1R0",
            "G7K3K0",
            "F0L2D6"
          //</editor-fold>
        };
        
        String[] chealth = new String[]{
            //<editor-fold desc="curhealths">
            "In Good Health",
            "In Poor Health",
            "Suffering from terminal illness",
            "Could be doing better",
            "Probably won't make it",
            "Alive, unfortuantely.",
            "Tonsils fell out and appendix exploded. Three times.",
            "Ribs found in lung.",
            "Heart is three times too small. Is a grinch.",
            "Heart is there times too big.",
            "Recovering from an accident with a blender.",
            "Spleen caused an accident with a blender.",
            "Missing spleen. What does a spleen even do?",
            "One lung is filled with jell-o.",
            "The other lung is filled with jell-o",
            "Responding well to treatment.",
            "Responding poorly to treatment.",
            "Responding to response of treatment."
            //</editor-fold>
        };
        
        String preparedStatement = 
                "INSERT INTO ece356_test.patient" +
                " (password, fname, lname, is_enabled," +
                " street_number, street, city, post_code," +
                " sin, num_visits, current_health) VALUES "+
                " (?, ?, ?, 1, ?, ?, ?, ?, ?, 0, ?);";
        
        try{
            PreparedStatement ps = connection.prepareStatement(preparedStatement);
            int sin = 199289999;
            for(int i = 0; i < 150; i++){
                ps = connection.prepareStatement(preparedStatement);
                ps.setString(1, password);
                ps.setString(2, fnames[(int)(Math.random()*fnames.length)]);
                ps.setString(3, lnames[(int)(Math.random()*lnames.length)]);
                ps.setInt(4, (int)(Math.random()*100));
                ps.setString(5, streets[(int)(Math.random()*streets.length)]);
                ps.setString(6, cities[(int)(Math.random()*cities.length)]);
                ps.setString(7, postcode[(int)(Math.random()*postcode.length)]);
                ps.setInt(8, sin);
                ps.setString(9, chealth[(int)(Math.random()*chealth.length)]);
                sin++;
                ps.execute();
            }
            //ps.executeBatch();
        }catch(SQLException e){
            //do something
            e.printStackTrace();
            closeConnection();
        }
        
        closeConnection();
    }
     
}
