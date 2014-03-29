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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
                else if(request.getParameter("type").equals("PatientFilter")){
                    JSONObject filters = new JSONObject();
                    
                    filters.put("pid", request.getParameter("pid").trim());
                    filters.put("fname", request.getParameter("fname").trim());
                    filters.put("lname", request.getParameter("lname").trim());
                    filters.put("current_health", request.getParameter("current_health").trim());
                    filters.put("last_visit_start", request.getParameter("last_visit_start").trim());
                    filters.put("last_visit_end", request.getParameter("last_visit_end").trim());
                    
                    boolean isPatientsList = Boolean.parseBoolean(request.getParameter("isPatientsList").trim());
                    
                    JSONArray rows = doctorVM.buildPatientsListRows(filters, isPatientsList);
                    
                    for (Object row : rows) {
                        out.println(row);
                    }
                }
                else if(request.getParameter("type").equals("VisitRequest")){                   
                    int patientId = Integer.parseInt(request.getParameter("patientId").trim());
                    boolean isPatient = Boolean.parseBoolean((request.getParameter("isPatient").trim()));
                    
                    JSONArray rows = doctorVM.buildPatientVisitsRows(patientId, isPatient, new JSONObject());
                    
                    for (Object row : rows) {
                        out.println(row);
                    }       
                }
                else if(request.getParameter("type").equals("VisitsFilter")){
                    int patientId = Integer.parseInt(request.getParameter("patientId").trim());
                    boolean isPatient = Boolean.parseBoolean(request.getParameter("isPatient"));
                    
                    JSONObject filters = new JSONObject();
                    
                    filters.put("visit_id", request.getParameter("visitNum").trim());
                    filters.put("visit_date_start", request.getParameter("dateStart").trim());
                    filters.put("visit_date_end", request.getParameter("dateEnd").trim());
                    filters.put("eid", request.getParameter("doctor").trim());
                    filters.put("visit_start_time_start", request.getParameter("starttimestart").trim());
                    filters.put("visit_start_time_end", request.getParameter("starttimeend").trim());
                    filters.put("visit_end_time_start", request.getParameter("endtimestart").trim());
                    filters.put("visit_end_time_end", request.getParameter("endtimeend").trim());
                    
                    filters.put("procedure", request.getParameter("procedures").trim());
                    filters.put("diagnoses", request.getParameter("diagnoses").trim());
                    filters.put("prescriptions", request.getParameter("prescriptions").trim());
                    filters.put("comments", request.getParameter("comments").trim());
                    
                    JSONArray rows = doctorVM.buildPatientVisitsRows(patientId, isPatient, filters);
                    
                    for (Object row : rows) {
                        out.println(row);
                    }
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
