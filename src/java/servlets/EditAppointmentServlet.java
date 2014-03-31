/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import control.Database;
import org.json.simple.JSONObject;
/**
 *
 * @author Ling
 */
public class EditAppointmentServlet extends HttpServlet {

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
            /**
             * Depending on the GET parameter, will generate a password change form or user update form
             * Form will contain defaults from the user model stored in the HTTP session
             */
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("<title> Edit Appointment </title>");       
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1> Edit Appointment </h1>");
            
            
            String updateForm ="";
            String tmp="";
            int pid = Integer.parseInt(request.getParameter("patient"));

            updateForm += "<form action='EditAppointmentServlet' method='post'>";

            tmp = "Appointment ID : <input type'text' name='vid' value='%s'> </br>";
            updateForm += String.format(tmp, Integer.parseInt(request.getParameter("visit_id")));
            
            tmp = "Patient ID : <input type'text' name='pid' value='%s'> </br>";
            updateForm += String.format(tmp, pid);
            
            tmp = "Doctor ID : <input type='text' name='eid' value='%s'> </br>";
            updateForm += String.format(tmp, Integer.parseInt(request.getParameter("eid")));
            
            tmp = "Date: <input type='text' name='date' value='%s'> </br>";
            updateForm += String.format(tmp, request.getParameter("visit_date"));

            tmp = "Start Time :  <input type='text' name='starttime' value='%s'> </br>";
            updateForm += String.format(tmp, request.getParameter("visit_start_time"));

            tmp = "End Time : <input type='text' name='endtime' value='%s'> </br>";
            updateForm += String.format(tmp, request.getParameter("visit_end_time"));

            updateForm += "<input type='submit' value='Edit Appointment'> ";
            updateForm += "</form>";
            
            out.println(updateForm);
           
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
        /**
         * POST request is the submission of one of the above generated forms.
         * Calls the correct Database function with the correct parameters
         * Generates a success or failure page. 
         */
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title> Book Appointment </title>"); 
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            //use form data to update user
            JSONObject params = new JSONObject();
            //update personal details
            params.put("vid", request.getParameter("vid"));
            params.put("pid", request.getParameter("pid"));
            params.put("eid", request.getParameter("eid"));
            params.put("date", request.getParameter("date"));
            params.put("starttime", request.getParameter("starttime"));
            params.put("endtime", request.getParameter("endtime"));
                
            boolean success = Database.editAppointment(params);
            if(success){
                out.println("<p class='status'> Information update successful! </p>");
            }else{
                out.println("<p class='status'> Information update failed. Please try again, or contact your system admin. </p>");
            }
            out.println("<button class='close' type=\"button\" onclick=\"window.open('', '_self', ''); window.close();\">Close Window</button>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } finally {
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
