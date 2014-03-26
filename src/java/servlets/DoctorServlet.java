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
import models.DoctorViewModel;
import models.User;

/**
 *
 * @author Herbert
 */
public class DoctorServlet extends HttpServlet {

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
    
        // TODO: Type check before casting?
        User user = (User) request.getSession().getAttribute("user");
        
        if(user != null && user.getUserType() == User.UserType.DOCTOR){
            DoctorViewModel doctorVM = new DoctorViewModel(user);

            request.getSession().setAttribute("doctorVM", doctorVM);
            response.sendRedirect("/ece356/doctor.jsp");
        }
        else{
            // Error page
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
            User user = (User) request.getSession().getAttribute("user");
            
            if(user.getUserType() == User.UserType.DOCTOR){
                DoctorViewModel doctorVM = (DoctorViewModel) request.getSession().getAttribute("doctorVM");
                
                if(request.getParameter("type").equals("PatientRequest")){
                    int patientId = Integer.parseInt(request.getParameter("patientId").trim());
                    
                    out.println(doctorVM.formatPatientDetails(patientId));
                }
                else if(request.getParameter("type").equals("VisitRequest")){
                    int patientId = Integer.parseInt(request.getParameter("patientId").trim());
                    
                    boolean isPatient = Boolean.parseBoolean(request.getParameter("isPatient").trim());
                        
                    out.println(doctorVM.formatPatientVisitsTable(patientId, isPatient));
                }
            }
            else{
                out.println("User authentication error. Please log in.");
            }
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
