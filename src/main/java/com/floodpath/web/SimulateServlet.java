package com.floodpath.web;

import com.floodpath.model.Road;
import com.floodpath.service.FloodService;
import com.floodpath.websocket.FloodSocket;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/simulate")
public class SimulateServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        try{
            int roadId=Integer.parseInt(req.getParameter("roadId"));
            int water=Integer.parseInt(req.getParameter("waterLevel"));
            int rain=Integer.parseInt(req.getParameter("rainIntensity"));
            FloodService fs=FloodService.getInstance(); fs.simulate(roadId,water,rain);
            Road r=fs.getRoads().stream().filter(x->x.getId()==roadId).findFirst().orElseThrow();
            FloodSocket.broadcast("{\"type\":\"ROAD_UPDATED\",\"roadId\":"+roadId+",\"waterLevel\":"+water+",\"rainIntensity\":"+rain+",\"severity\":"+r.getSeverity()+",\"status\":\""+r.getStatus()+"\",\"message\":\"Road simulation updated in real time\"}");
            resp.setContentType("application/json");resp.getWriter().printf("{\"ok\":true,\"roadId\":%d,\"waterLevel\":%d,\"rainIntensity\":%d,\"severity\":%d,\"status\":\"%s\"}",roadId,water,rain,r.getSeverity(),r.getStatus());
        }catch(Exception e){resp.setStatus(400);resp.setContentType("application/json");resp.getWriter().print("{\"ok\":false,\"error\":\""+com.floodpath.util.Json.esc(e.getMessage())+"\"}");}
    }
}
