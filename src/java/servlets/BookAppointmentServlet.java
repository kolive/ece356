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
public class BookAppointmentServlet extends HttpServlet {

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
             
            //check the user, make sure that the user which is logged in is a staff member
            if(((User)request.getSession().getAttribute("user")).getUserType() != User.UserType.STAFF){
                response.sendRedirect("/ece356/error.jsp");
            }
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("<title> Book Appointment </title>");       
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1> Book Appointment </h1>");
            
            
            String updateForm ="";
            String tmp="";
            int pid = Integer.parseInt(request.getParameter("patient"));
            JSONObject patient = Database.getPatient(pid);

            updateForm += "<form action='BookAppointmentServlet' method='post'>";

            tmp = "Patient ID : <input type'text' name='pid' value='%s' readonly> </br>";
            updateForm += String.format(tmp, pid);
            
            tmp = "Doctor ID : <input type='text' name='eid'> </br>";
            updateForm += tmp;
            
            tmp = "Date: <input type='text' name='date'> </br>";
            updateForm += tmp;

            tmp = "Start Time :  <input type='text' name='starttime'> </br>";
            updateForm += tmp;

            tmp = "End Time : <input type='text' name='endtime'> </br>";
            updateForm += tmp;

            updateForm += "<input type='submit' value='Book Appointment'> ";
            updateForm += "</form>";
            
            out.println(updateForm);
           
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }catch(Exception e){
            response.sendRedirect("/ece356/error.jsp");
        }   finally {
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
            params.put("pid", request.getParameter("pid"));
            params.put("eid", request.getParameter("eid"));
            params.put("date", request.getParameter("date"));
            params.put("starttime", request.getParameter("starttime"));
            params.put("endtime", request.getParameter("endtime"));
                
            boolean success = Database.bookAppointment(params);
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
