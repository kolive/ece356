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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        
       try{
            if(request.getSession().getAttribute("user") != null){
                //redirect to logout confirmation before allowing to login
                //for now i'll just delete the current user attribute
                request.getSession().setAttribute("user", null);
            }

            //Two cases, want to login as a patient or employee

            if(request.getParameter("type") != null 
                    && request.getParameter("type").equals("patient")){

                JSONObject userInfo = Database.userLogin(request.getParameter("username"), request.getParameter("password"), true);
                if(!userInfo.isEmpty()){
                    User p = new User(userInfo, User.UserType.PATIENT);
                    request.getSession().setAttribute("user", p);
                    response.sendRedirect("PatientServlet");
                }else{
                     //redirect to failed login page
                    response.sendRedirect("/ece356/error.jsp");
                }


            }else if(request.getParameter("type") != null){
                //employee login
                JSONObject userInfo = Database.userLogin(request.getParameter("username"), request.getParameter("password"), false);
                if(!userInfo.isEmpty()){
                    if(userInfo.get("dept").equals("DOCTOR")){
                        User p = new User(userInfo, User.UserType.DOCTOR);
                        request.getSession().setAttribute("user", p);
                        response.sendRedirect("DoctorServlet");
                    }
                    else if(userInfo.get("dept").equals("FINANCE")){
                        User p = new User(userInfo, User.UserType.FAUDITOR);
                        request.getSession().setAttribute("user", p);
                        response.sendRedirect("/ece356/FinanceServlet");
                    }
                    else if (userInfo.get("dept").equals("STAFF")) {
	                    User p = new User(userInfo, User.UserType.STAFF);
	                    request.getSession().setAttribute("user", p);
	                    response.sendRedirect("/ece356/StaffServlet");
                	}
                    else if(userInfo.get("dept").equals("LEGAL")) {
                        User p = new User(userInfo, User.UserType.LAUDITOR);
                        request.getSession().setAttribute("user", p);
                        response.sendRedirect("LegalServlet");
                    }
                }else{
                    //redirect to failed login page
                    response.sendRedirect("/ece356/error.jsp");
                }
            }

            System.out.println(request.getParameter("type"));
       }catch(Exception e){
           //redirect to failed login page
            response.sendRedirect("/ece356/error.jsp");
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
