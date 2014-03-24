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
    }
    
    public static void deleteAll(){
        openConnection();
        try{
            connection.prepareStatement("delete from ece356_test.patient").executeUpdate();
             connection.prepareStatement("ALTER TABLE ece356_test.patient AUTO_INCREMENT = 1").executeUpdate();
        }catch(SQLException e){
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
                System.out.println(ps);
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
