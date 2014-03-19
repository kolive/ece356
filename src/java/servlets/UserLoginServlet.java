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
import models.*;
import control.Database;
/**
 *
 * @author Kyle
 */
public class UserLoginServlet extends HttpServlet {

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
            
            if(request.getSession().getAttribute("user") != null){
                //redirect to logout confirmation before allowing to login
                //for now i'll just delete the current user attribute
                request.getSession().setAttribute("user", null);
            }
            
            //Two cases, want to login as a patient or employee
            if(request.getParameter("type") != null 
                    && request.getParameter("type").equals("patient")){
                
                String[] userInfo = Database.patientLogin(request.getParameter("username"), request.getParameter("password"));
                if(userInfo.length > 0){
                    Patient p = new Patient(userInfo);
                    request.getSession().setAttribute("user", p);
                }else{
                    //redirect to failed login page
                }
                
                
            }else if(request.getParameter("type") != null){
                //employee login
                String[] userInfo = Database.employeeLogin(request.getParameter("username"), request.getParameter("password"));
                if(userInfo.length > 0){
                    //currently I'm assuming all employees are staff members
                    //realistically, there should be a model for each employee type
                    // and a check should be made on userInfo[3] to determine type
                    User p = new User(userInfo[0], userInfo[1], userInfo[2], User.UserType.STAFF);
                    request.getSession().setAttribute("user", p);
                }else{
                    //redirect to failed login page
                }
            }
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserLoginServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserLoginServlet at " + request.getContextPath() + "</h1>");
            
            /** 
             * Recovers user session value and prints that the user is logged in
             * Realistically, this should forward the request to a "logged in" page, 
             * based on whether the UserType
             * 
             * But those haven't been made yet
             */
            User user = (User)request.getSession().getAttribute("user");
            User.UserType ut = null;
            if( user != null ){
                ut = user.getUserType();
                if( ut == User.UserType.PATIENT ){
                    out.println("<p> Patient Logged in! </p> <p> USER DUMP: " + ((Patient)request.getSession().getAttribute("user")).stringify() + "</p>" );
                }else{
                    out.println("<p> Employee Logged in! Employee type: " + user.getUserType().toString() + " </p>"  );
                }
            }else{
                out.println(" <p> NOT LOGGED IN! </p> " );
            }
            
            
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
        processRequest(request, response);
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
