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
import models.FinanceViewModel;
import models.User;

/**
 *
 * @author Kyle
 */
public class FinanceServlet extends HttpServlet {

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
        //TODO: add user check to make sure user is logged in and an FAUDITOR,
        User fauditor = (User)request.getSession().getAttribute("user");
        if(fauditor != null && fauditor.getUserType() == User.UserType.FAUDITOR){
           FinanceViewModel financeVM = new FinanceViewModel();
           request.getSession().setAttribute("financeVM", financeVM);
           response.sendRedirect("/ece356/finance.jsp");
        }else{
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User)request.getSession().getAttribute("user");
            if(user.getUserType() == User.UserType.FAUDITOR){
                FinanceViewModel fvm = (FinanceViewModel) request.getSession().getAttribute("financeVM");
                if(request.getParameter("type").equals("PatientRequest")){
                    out.println(fvm.formatPatientList(
                            Integer.parseInt(request.getParameter("dId").trim()),
                            true,
                            request.getParameter("date1").trim(),
                            request.getParameter("date2").trim()));
                }else if(request.getParameter("type").equals("VisitRequest")){
                    out.println(fvm.formatVisitList(
                            Integer.parseInt(request.getParameter("pId").trim()),
                            Integer.parseInt(request.getParameter("dId").trim()),
                            true,
                            request.getParameter("date1"),
                            request.getParameter("date2")));
                }else if(request.getParameter("type").equals("DoctorDetailRequest")){
                    out.println(fvm.formatDoctorSummary(
                        Integer.parseInt(request.getParameter("dId").trim())
                    ));
                }else if(request.getParameter("type").equals("PatientDetailRequest")){
                    out.println(fvm.formatPatientSummary(
                        Integer.parseInt(request.getParameter("pId").trim())                       
                    ));
                }else if(request.getParameter("type").equals("VisitDetailRequest")){
                    out.println(fvm.formatVisitSummary(
                        Integer.parseInt(request.getParameter("vId").trim())
                    ));
                }
            }else{
               out.println("User authentication error. Please log in.");
            }
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
