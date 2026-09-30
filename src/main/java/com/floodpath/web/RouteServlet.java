package com.floodpath.web;

import com.floodpath.model.Road;
import com.floodpath.service.FloodService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/route")
public class RouteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        if (from == null || to == null) {
            resp.sendError(400, "from and to are required");
            return;
        }
        List<Road> path = FloodService.getInstance().calculateRoute(from.toUpperCase(), to.toUpperCase());
        double distance = path.stream().mapToDouble(Road::getDistanceKm).sum();

        resp.setContentType("application/json");
        resp.getWriter().printf("{\"found\":%s,\"distance\":%.2f,\"roads\":[", !path.isEmpty(), distance);
        for (int i = 0; i < path.size(); i++) {
            Road r = path.get(i);
            if (i > 0) resp.getWriter().print(",");
            resp.getWriter().printf(
                    "{\"id\":%d,\"name\":\"%s\",\"start\":\"%s\",\"end\":\"%s\",\"status\":\"%s\",\"severity\":%d}",
                    r.getId(), escape(r.getName()), r.getStart(), r.getEnd(), r.getStatus(), r.getSeverity());
        }
        resp.getWriter().print("]}");
    }

    private String escape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }
}
