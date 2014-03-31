/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import control.Database;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Kyle
 */
public class UpdateVisitationServlet extends HttpServlet {

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
            out.println("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js\" type=\"text/javascript\"></script>");
            out.println("<script src=\"http://code.jquery.com/ui/1.10.3/jquery-ui.js\" type=\"text/javascript\"></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.3/themes/start/jquery-ui.css\">");
            
            out.println("<script type='text/javascript'>");
            out.println("$(function () {");
            out.println("   $(document).ready(function () {");
            out.println("       $('.date').datepicker({dateFormat: 'yy-mm-dd', changeYear: true});");
            out.println("   });");
            out.println("});");
            out.println("</script>");
                    
            out.println("<title>Edit Visitation Record</title>");            
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1>Edit Visitation Record</h1>");
            
            User doctor = (User) request.getSession().getAttribute("user");
            int visitId = Integer.parseInt(request.getParameter("vid").trim());
            int patientId = Integer.parseInt(request.getParameter("pid").trim());
            
            JSONArray visitInfo = Database.getFullVisitRecord(visitId);
            System.out.println(visitInfo);
            
            String form = "<form action='UpdateVisitationServlet' method='POST'>";
            
            form += String.format("<input type='hidden' name='eid' value='%s'>", doctor.getStringParam("eid"));
            form += String.format("<input type='hidden' name='vid', value='%s'>", visitId); 
            form += String.format("<input type='hidden' name='pid', value='%s'>", patientId); 
            
            // Pass as parameter and make it a header
            // need to prepopulate fields
            form += String.format("<h3>Visit ID: %s </h3> </br>", visitId);
            form += String.format("<h3>Appointment Date:</h3> <input type='text' class='date' name='visit_date' value='%s'> </br>", 
                    ((JSONObject)visitInfo.get(0)).get("visit_date"));
            form += String.format("<h3>Start Time:</h3> <input type='text' name='visit_start_time' value='%s'></br>",
                    ((JSONObject)visitInfo.get(0)).get("visit_start_time"));
            form += String.format("<h3>End Time:</h3> <input type='text' name='visit_end_time' value='%s'></br>",
                    ((JSONObject)visitInfo.get(0)).get("visit_end_time"));
            
            form += "</br>";
            
            form += String.format("<h3>Procedure Performed:</h3> <p>Name: <input type='text' name='procedure_name' value='%s'>",
                    ((JSONObject)visitInfo.get(0)).get("procedure_name"));
            form += String.format("Description: <input type='text' name='description' value='%s'></p></br>",
                    ((JSONObject)visitInfo.get(0)).get("description"));
            form += String.format("<h3>Diagnosis:</h3> <input type='text' name='severity' value='%s'></br>",
                    ((JSONObject)visitInfo.get(0)).get("severity"));
            form += "<h3>Prescriptions Prescribed:</h3> <div>";
            for(int i = 0; i < visitInfo.size() && ((JSONObject)visitInfo.get(i)).get("drug_name") != null ; i++){
                form += String.format("<p>Drug Name: <input type='text' name='drug_name%s' value='%s'> Expires: <input type='text' class='date' name='expires%s' value='%s'></p>",
                        Integer.toString(i), ((JSONObject)visitInfo.get(i)).get("drug_name"),
                        Integer.toString(i), ((JSONObject)visitInfo.get(0)).get("expires"));
            
            }
            
            form += "</div></br>"; 
            String comment = "";
            if(((JSONObject)visitInfo.get(0)).containsKey("content") 
                    && ((JSONObject)visitInfo.get(0)).get("content") != null){
                comment = ((JSONObject)visitInfo.get(0)).get("content").toString();
            }
            form += String.format("<h3>Comments:</h3> <input type='text' name='content'value='%s'></br>",
                        comment);
            
            form += "<input type='submit' value='Submit'>";
            form += "</form>";
            
            out.println(form);
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }catch(Exception e){
            e.printStackTrace();            
            response.sendRedirect("/ece356/error.jsp");
            out.close();
        }finally {
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
            
            

            if(request.getParameter("vid") != null && !request.getParameter("vid").trim().equals("") &&
                request.getParameter("visit_date") != null && !request.getParameter("visit_date").trim().equals("") &&
                request.getParameter("visit_start_time") != null && !request.getParameter("visit_start_time").trim().equals("") &&
                request.getParameter("visit_end_time") != null && !request.getParameter("visit_end_time").trim().equals("")){

                visitParams.put("vid", request.getParameter("vid"));
                visitParams.put("visit_date", request.getParameter("visit_date"));
                visitParams.put("visit_start_time", request.getParameter("visit_start_time"));
                visitParams.put("visit_end_time", request.getParameter("visit_end_time"));

                visitParams.put("procedure_name", request.getParameter("procedure_name"));
                visitParams.put("description", request.getParameter("description"));
                visitParams.put("severity", request.getParameter("severity"));
                visitParams.put("content", request.getParameter("content"));

                visitParams.put("pid", request.getParameter("pid"));


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

                System.out.println(visitParams);

                boolean success = Database.UpdateRecord(Integer.parseInt(request.getParameter("eid")), visitParams);

                if(success){
                    out.println("<p class='status'>Update visitation record submission successful!</p>");
                }
                else{
                    out.println("<p class='status'>Update visitation record submission failed. Please try again, or contact your system admin.</p>");
                }
            }else{
                out.println("<p class='status'>Missing required fields. Please try again.</p>");
            } 
            out.println("<button class='close' type=\"button\" onclick=\"window.open('', '_self', ''); window.close();\">Close Window</button>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }catch(Exception e){
            response.sendRedirect("/ece356/error.jsp");
        }finally{
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
