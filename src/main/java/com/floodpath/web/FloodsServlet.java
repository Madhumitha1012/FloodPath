package com.floodpath.web;

import com.floodpath.model.Road;
import com.floodpath.service.FloodService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/floods")
public class FloodsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().print("{\"roads\":[");
        int i = 0;
        for (Road r : FloodService.getInstance().getRoads()) {
            if (i++ > 0) resp.getWriter().print(",");
            resp.getWriter().printf("{\"id\":%d,\"name\":\"%s\",\"start\":\"%s\",\"end\":\"%s\",\"distance\":%.2f,\"status\":\"%s\",\"severity\":%d}",
                    r.getId(), escape(r.getName()), r.getStart(), r.getEnd(), r.getDistanceKm(), r.getStatus(), r.getSeverity(), r.getWaterLevelCm(), r.getRainIntensity());
        }
        resp.getWriter().print("]}");
    }

    private String escape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }
}
