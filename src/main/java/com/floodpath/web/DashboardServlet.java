package com.floodpath.web;

import com.floodpath.model.FloodReport;
import com.floodpath.model.Road;
import com.floodpath.model.User;
import com.floodpath.service.AuthService;
import com.floodpath.service.FloodService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet({"/dashboard","/user/dashboard","/user/history","/admin/dashboard","/admin/reports","/admin/roads","/admin/simulation"})
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u=(User)req.getSession().getAttribute("user");
        String path=req.getRequestURI().substring(req.getContextPath().length());
        FloodService fs=FloodService.getInstance();
        List<Road> roads=new ArrayList<>(fs.getRoads());
        long affected=roads.stream().filter(r->r.getSeverity()>0).count();
        long closed=roads.stream().filter(r->"CLOSED".equals(r.getStatus())).count();
        req.setAttribute("roads",roads); req.setAttribute("affectedCount",affected); req.setAttribute("closedCount",closed);
        if ("/dashboard".equals(path)) { resp.sendRedirect(req.getContextPath()+(u.isAdmin()?"/admin/dashboard":"/user/dashboard")); return; }
        if (u.isAdmin()) {
            List<FloodReport> all=fs.getReports(); Collections.reverse(all);
            req.setAttribute("reports",all);
            req.setAttribute("pendingCount",all.stream().filter(r->"PENDING".equals(r.getStatus())).count());
            req.setAttribute("verifiedCount",all.stream().filter(r->"VERIFIED".equals(r.getStatus())).count());
            req.setAttribute("liveCount",all.stream().filter(FloodReport::isLive).count());
            req.setAttribute("users",AuthService.getInstance().listUsers());
            if (path.equals("/admin/reports")) req.getRequestDispatcher("/admin/reports.jsp").forward(req,resp);
            else if (path.equals("/admin/roads")) req.getRequestDispatcher("/admin/roads.jsp").forward(req,resp);
            else if (path.equals("/admin/simulation")) req.getRequestDispatcher("/admin/simulation.jsp").forward(req,resp);
            else req.getRequestDispatcher("/admin/dashboard.jsp").forward(req,resp);
        } else {
            List<FloodReport> mine=fs.getReportsByUser(u.getId());
            req.setAttribute("reports",mine);
            req.setAttribute("verifiedCount",mine.stream().filter(r->"VERIFIED".equals(r.getStatus())).count());
            req.setAttribute("liveCount",mine.stream().filter(FloodReport::isLive).count());
            if (path.equals("/user/history")) req.getRequestDispatcher("/user/history.jsp").forward(req,resp);
            else req.getRequestDispatcher("/user/dashboard.jsp").forward(req,resp);
        }
    }
}
