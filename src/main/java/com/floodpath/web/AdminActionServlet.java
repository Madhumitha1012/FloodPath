package com.floodpath.web;

import com.floodpath.model.FloodReport;
import com.floodpath.service.FloodService;
import com.floodpath.websocket.FloodSocket;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Set;

@WebServlet("/admin/action")
public class AdminActionServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        String action=req.getParameter("action");
        try{
            if(Set.of("verify","reject","close").contains(action)){
                int id=Integer.parseInt(req.getParameter("id"));
                String status="verify".equals(action)?"VERIFIED":("reject".equals(action)?"REJECTED":"CLOSED");
                FloodReport r=FloodService.getInstance().setReportStatus(id,status);if(r==null){resp.sendError(404);return;}
                FloodSocket.broadcast("{\"type\":\"REPORT_REVIEWED\",\"reportId\":"+id+",\"roadId\":"+r.getRoadId()+",\"status\":\""+r.getStatus()+"\"}");
            }else if("reset".equals(action)){FloodService.getInstance().resetRoads();FloodSocket.broadcast("{\"type\":\"SIMULATION_UPDATED\",\"message\":\"Roads reset to normal\"}");}
            else{resp.sendError(400);return;}
        }catch(NumberFormatException e){resp.sendError(400);return;}
        resp.sendRedirect(req.getContextPath()+"/admin/reports");
    }
}
