/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import control.Database;
import static java.awt.PageAttributes.MediaType.D;
import java.io.IOException;
import java.io.PrintWriter;
import static java.sql.DriverManager.println;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Kouen
 */
public class InsertVisitationServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<script src=\"http://code.jquery.com/ui/1.10.3/jquery-ui.js\" type=\"text/javascript\"></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.3/themes/start/jquery-ui.css\">");
            
            out.println("<script type='text/javascript'>");
            out.println("$(function ) {");
            out.println("   $(document).ready(function () {");
            out.println("       $('#apptdate').datepicker({dateFormat: 'yy-mm-dd', changeYear: true});");
            out.println("   });");
            out.println("});");
            out.println("</script>");
                    
            out.println("<title>New Visitation Record</title>");            
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1>New Visitation Record</h1>");
            
            User doctor = (User) request.getSession().getAttribute("user");
            int patientId = Integer.parseInt(request.getParameter("patientId").toString().trim());
            
            String form = "<form action='InsertVisitationServlet' method='POST'>";
            
            form += String.format("<input type='hidden' name='eid' value='%s'>", doctor.getStringParam("eid"));
            form += String.format("<input type='hidden' name='pid', value='%s'>", patientId); 
            
            // Pass as parameter and make it a header
            form += String.format("<h3>Patient Id: %s </h3> </br>", patientId);
            form += "<h3>Appointment Date:</h3> <input type='text' id='apptdate' name='visit_date'> </br>";
            form += "<h3>Start Time:</h3> <input type='text' name='visit_start_time'></br>";
            form += "<h3>End Time:</h3> <input type='text' name='visit_end_time'></br>";
            
            form += "</br>";
            
            form += "<h3>Procedure Performed:</h3> <p>Name: <input type='text' name='procedure_name'> Description: <input type='text' name='description'></p></br>";
            form += "<h3>Diagnosis:</h3> <input type='text' name='severity'></br>";
            form += "<h3>Prescriptions Prescribed:</h3> <div>";
            form += "<p>Drug Name: <input type='text' name='drug_name1'> Expires: <input type='text' name='expires1'></p>";
            form += "<p>Drug Name: <input type='text' name='drug_name2'> Expires: <input type='text' name='expires2'></p>";
            form += "<p>Drug Name: <input type='text' name='drug_name3'> Expires: <input type='text' name='expires3'></p>";
            form += "<p>Drug Name: <input type='text' name='drug_name4'> Expires: <input type='text' name='expires4'></p>";
            form += "<p>Drug Name: <input type='text' name='drug_name5'> Expires: <input type='text' name='expires5'></p>";
            
            form += "</div></br>";
            form += "<h3>Comments:</h3> <input type='text' name='content'></br>";
            
            form += "<input type='submit' value='Submit'>";
            form += "</form>";
            
            out.println(form);
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try{
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>New Visitation Record</title>"); 
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            
            JSONObject visitParams = new JSONObject();
            
            
            if(request.getParameter("pid") != null && !request.getParameter("pid").trim().equals("") &&
                    request.getParameter("visit_date") != null && !request.getParameter("visit_date").trim().equals("") &&
                    request.getParameter("visit_start_time") != null && !request.getParameter("visit_start_time").trim().equals("") &&
                    request.getParameter("visit_end_time") != null && !request.getParameter("visit_end_time").trim().equals("")){
                
                visitParams.put("pid", request.getParameter("pid"));
                visitParams.put("visit_date", request.getParameter("visit_date"));
                visitParams.put("visit_start_time", request.getParameter("visit_start_time"));
                visitParams.put("visit_end_time", request.getParameter("visit_end_time"));

                visitParams.put("procedure_name", request.getParameter("procedure_name"));
                visitParams.put("description", request.getParameter("description"));
                visitParams.put("severity", request.getParameter("severity"));
                visitParams.put("content", request.getParameter("content"));

            
                JSONArray prescriptions = new JSONArray();

                for(int i = 1; i <= 5; i++){
                    JSONObject prescription = new JSONObject();

                    if(request.getParameter("drug_name" + i) != null && !request.getParameter("drug_name" + i).trim().equals("") &&
                            request.getParameter("expires" + i) != null && !request.getParameter("expires" + i).trim().equals("")){
                        prescription.put("drug_name", request.getParameter("drug_name" + i).trim());
                        prescription.put("expires", request.getParameter("expires" + i).trim());

                        prescriptions.add(prescription);
                    }   
                }

                visitParams.put("prescriptions", prescriptions);

                boolean success = Database.InsertNewRecord(Integer.parseInt(request.getParameter("eid")), visitParams);

                if(success){
                    out.println("<p class='status'>New visitation record submission successful!</p>");
                }
                else{
                    out.println("<p class='status'>New visitation record submission failed. Please try again, or contact your system admin.</p>");
                }
            }
            else{
                out.println("<p class='status'>Missing required fields. Please try again.</p>");
            } 
            
            out.println("<button class='close' type=\"button\" onclick=\"window.open('', '_self', ''); window.close();\">Close Window</button>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
        finally{
            out.close();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
