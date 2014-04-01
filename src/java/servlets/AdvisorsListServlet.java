/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import control.Database;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;

/**
 *
 * @author Kyle
 */
public class AdvisorsListServlet extends HttpServlet {

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
            
            //check the user, make sure that the user which is logged in is the right doctor
            if(!((User)request.getSession().getAttribute("user")).getStringParam("eid").equals(request.getParameter("eid"))){
                response.sendRedirect("/ece356/error.jsp");
            }
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Visit Advisors</title>"); 
            out.println("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js\" type=\"text/javascript\"></script>\n");
            out.println("<script src=\"js/footable.js\" type=\"text/javascript\"></script>\n");
            out.println("<link href=\"css/footable.core.css\" rel=\"stylesheet\" type=\"text/css\"/>");
            out.println("<link href=\"css/footable.metro.css\" rel=\"stylesheet\" type=\"text/css\" />");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
            out.println("<script type=\"text/javascript\">\n" +
            "        $(function () {\n" +
            "\n" +
            "            $( document ).ready(function() { \n" +
            "                $('table').footable() \n" +
            "            });\n" +
            "\n" +
            "        });\n" +
            "        var removeAdvisor = function(vid, eid ) { $.ajax({\n" +
            "  url: \"/ece356/AdvisorsListServlet\",\n" +
            "  type: 'POST',\n" +
            "  data: { type: 'delete', vid: vid, eid: eid, owner: " + ((User)request.getSession().getAttribute("user")).getStringParam("eid") + " }"+
            "}).done(function() {\n" +
            "  location.reload();\n" +
            "}); };"+
            "        var addAdvisor = function(vid) { $.ajax({\n" +
            "  url: \"/ece356/AdvisorsListServlet\",\n" +
            "  type: 'POST',\n" +
            "  data: { type: 'add', vid: vid, eid: $('#advisorid').val(), owner: " + ((User)request.getSession().getAttribute("user")).getStringParam("eid") + " }"+
            "}).done(function() {\n" +
            "  location.reload();\n" +
            "}); }"+
            "        </script>");
            out.println("</head>");
            out.println("<body class=\"backgreen\"><div class='container backteal'>");
            out.println("<h1> Visit Advisors for Visit ID: " + request.getParameter("vid") + "</h1>");
            out.println(models.Helpers.FormatHelper.generateAdvisorsTable(Integer.parseInt(request.getParameter("vid"))));
            out.println("</div></body>");
            out.println("</html>");
            
        }catch (Exception e){
            response.sendRedirect("error.jsp");
        }finally {
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
       //check the user, make sure that the user which is logged in is the right doctor
        if(!((User)request.getSession().getAttribute("user")).getStringParam("eid").equals(request.getParameter("owner"))){
            response.sendRedirect("/ece356/error.jsp");
        }else if(request.getParameter("type").equals("delete")){
            Database.removeAdvisor(Integer.parseInt(request.getParameter("eid")), Integer.parseInt(request.getParameter("vid")));
        }else if(request.getParameter("type").equals("add")){
            Database.addAdvisorFor(Integer.parseInt(request.getParameter("eid")), Integer.parseInt(request.getParameter("vid")));
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
