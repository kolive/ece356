/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.PatientViewModel;
import models.StaffViewModel;
import models.User;
import models.User.UserType;

/**
 *
 * @author Ling
 */
@WebServlet(name = "StaffServlet", urlPatterns = {"/StaffServlet"})
public class StaffServlet extends HttpServlet {

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
        User staff = (User)request.getSession().getAttribute("user");
        if(staff != null && staff.getUserType() == User.UserType.STAFF){
           StaffViewModel staffVM = new StaffViewModel(staff);
           request.getSession().setAttribute("staffVM", staffVM);
           response.sendRedirect("/ece356/staff.jsp");
        }else{
            //redirect to error page
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User)request.getSession().getAttribute("user");
            if(user.getUserType() == User.UserType.STAFF){
                StaffViewModel vm = (StaffViewModel) request.getSession().getAttribute("staffVM");
                if(request.getParameter("type").equals("UserRequest")){
                    //
                    out.println(vm.formatInfo(
                            true,
                            Integer.parseInt(user.getStringParam("eid")),
                            Integer.parseInt(request.getParameter("dId")),
                            request.getParameter("userType").equals("PATIENT") ? UserType.PATIENT : UserType.DOCTOR
                            ));
                }else if(request.getParameter("type").equals("AppointmentsRequest")){
                    out.println(vm.formatAppointments(
                            true,
                            Integer.parseInt(user.getStringParam("eid")),
                            Integer.parseInt(request.getParameter("dId")),
                            request.getParameter("userType").equals("PATIENT") ? UserType.PATIENT : UserType.DOCTOR
                            ));
                }/*else if(request.getParameter("type").equals("DoctorDetailRequest")){
                    out.println(vm.formatDoctorSummary(
                        Integer.parseInt(request.getParameter("dId").trim())
                    ));
                }else if(request.getParameter("type").equals("PatientDetailRequest")){
                    out.println(vm.formatPatientSummary(
                        Integer.parseInt(request.getParameter("pId").trim())                       
                    ));
                }else if(request.getParameter("type").equals("VisitDetailRequest")){
                    out.println(vm.formatVisitSummary(
                        Integer.parseInt(request.getParameter("vId").trim())
                    ));
                }*/
            }else{
               out.println("User authentication error. Please log in.");
            }
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
