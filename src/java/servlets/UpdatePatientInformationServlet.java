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
public class UpdatePatientInformationServlet extends HttpServlet {

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
            out.println("<title> Update Patient Information </title>");       
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1> Update Patient Information </h1>");
            
            
            String updateForm ="";
            String tmp="";
            int pid = Integer.parseInt(request.getParameter("patient"));
            JSONObject patient = Database.getPatient(pid);

            updateForm += "<form action='UpdatePatientInformationServlet' method='post'>";

            tmp = "Patient ID : <input type'text' name='pid' value='%s' readonly> </br>";
            updateForm += String.format(tmp, pid);
            
            tmp = "First Name : <input type='text' name='fname' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("fname"));

            tmp = "Last Name :  <input type='text' name='lname' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("lname"));

            tmp = "SIN: <input type='text' name='sin' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("sin"));

            tmp = "Address : </br>";
            tmp += "Street Address: # <input type='text' name='street_number' value='%s' > Street <input type='text' name='street' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("street_number"), patient.get("street"));

            tmp = "City: <input type='text' name='city' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("city"));

            tmp = "Post Code: <input type='text' name='post_code' value='%s'> </br>";
            updateForm += String.format(tmp, patient.get("post_code"));
            updateForm += "<input type='submit' value='Update Patient Information'> ";
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
            out.println("<title> Update Patient Information </title>"); 
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            //use form data to update user
            JSONObject params = new JSONObject();
            //update personal details
            params.put("fname", request.getParameter("fname"));
            params.put("lname", request.getParameter("lname"));
            params.put("sin", request.getParameter("sin"));
            params.put("street_number", request.getParameter("street_number"));
            params.put("street", request.getParameter("street"));
            params.put("city", request.getParameter("city"));
            params.put("post_code", request.getParameter("post_code"));
                
            boolean success = Database.updatePatientInformation(Integer.parseInt(request.getParameter("pid")), params);
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
