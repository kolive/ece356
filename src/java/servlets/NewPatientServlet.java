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
public class NewPatientServlet extends HttpServlet {

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
            out.println("<title> New Patient </title>");       
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            out.println("<h1> New Patient </h1>");
            
            String updateForm ="";
            String tmp="";
            
            updateForm += "<form action='NewPatientServlet' method='post'>";
            
            updateForm += "Doctor ID : <input type='text' name='eid'> </br>";

            tmp = "First Name : <input type='text' name='fname'> </br>";
            updateForm += tmp;

            tmp = "Last Name :  <input type='text' name='lname'> </br>";
            updateForm += tmp;

            tmp = "SIN : <input type='text' name='sin'> </br>";
            updateForm += tmp;
            
            tmp = "Password : <input type='text' name='password'> </br>";
            updateForm += tmp;

            tmp = "Address : </br>";
            tmp += "Street Address: # <input type='text' name='street_number'> Street <input type='text' name='street'> </br>";
            updateForm += tmp;

            tmp = "City: <input type='text' name='city'> </br>";
            updateForm += tmp;

            tmp = "Post Code: <input type='text' name='post_code'> </br>";
            updateForm += tmp;
            updateForm += "<input type='submit' value='Submit Patient'> ";
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
            out.println("<title> Doctor Assignment </title>"); 
            out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("</head>");
            out.println("<body class='backgreen'>");
            out.println("<div class='update rounded backteal'>");
            
            JSONObject params = new JSONObject();
            //update personal details
            params.put("fname", request.getParameter("fname"));
            params.put("lname", request.getParameter("lname"));
            params.put("sin", request.getParameter("sin"));
            params.put("password", request.getParameter("password"));
            params.put("street_number", request.getParameter("street_number"));
            params.put("street", request.getParameter("street"));
            params.put("city", request.getParameter("city"));
            params.put("post_code", request.getParameter("post_code"));
            
            boolean success = Database.newPatient(
                    Integer.parseInt(request.getParameter("eid").toString()),
                    params);
            if(success){
                out.println("<p class='status'> New Doctor Assigned </p>");
            }else{
                out.println("<p class='status'> Submit failed. Please try again, or contact your system admin. </p>");
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
