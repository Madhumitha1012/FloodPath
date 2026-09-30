package com.floodpath.web;

import com.floodpath.config.DbConfig;
import com.floodpath.model.FloodReport;
import com.floodpath.model.User;
import com.floodpath.service.FloodService;
import com.floodpath.websocket.FloodSocket;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@WebServlet("/api/report")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024, fileSizeThreshold = 1024 * 1024)
public class ReportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        String savedImage = null;
        try {
            int roadId = Integer.parseInt(req.getParameter("roadId"));
            int water = Integer.parseInt(req.getParameter("waterLevel"));
            int severity = Integer.parseInt(req.getParameter("severity"));
            String condition = req.getParameter("condition");
            String description = req.getParameter("description");
            if (severity < 0 || severity > 4) throw new IllegalArgumentException("Bad severity");
            if (!"PASSABLE".equals(condition) && !"DIFFICULT".equals(condition) && !"BLOCKED".equals(condition))
                throw new IllegalArgumentException("Bad road condition");
            if (description != null && description.length() > 500) description = description.substring(0, 500);

            Part image = req.getPart("image");
            if (image != null && image.getSize() > 0) savedImage = saveImage(image);

            FloodReport r = FloodService.getInstance().addReport(user, roadId, water, severity, condition, description, savedImage);
            FloodSocket.broadcast("{\"type\":\"ROAD_UPDATED\",\"roadId\":" + roadId +
                    ",\"severity\":" + severity + ",\"status\":\"" +
                    r.getStatus() + "\",\"message\":\"New flood report received\"}");

            resp.setContentType("application/json");
            resp.getWriter().printf("{\"ok\":true,\"reportId\":%d,\"hasImage\":%s}", r.getId(), r.isHasImage());
        } catch (Exception e) {
            if (savedImage != null) { try { Files.deleteIfExists(Paths.get(DbConfig.UPLOAD_DIR, savedImage)); } catch (Exception ignored) {} }
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().print("{\"ok\":false,\"error\":\"" + com.floodpath.util.Json.esc(
                    e instanceof NumberFormatException ? "Please fill in all fields." : e.getMessage()) + "\"}");
        }
    }

    
    private String saveImage(Part part) throws IOException {
        byte[] head = new byte[12];
        int n;
        try (InputStream in = part.getInputStream()) { n = in.readNBytes(head, 0, 12); }
        String ext = detectExtension(head, n);
        if (ext == null) throw new IllegalArgumentException("Only JPG, PNG, GIF or WEBP images are allowed.");

        Path dir = Paths.get(DbConfig.UPLOAD_DIR);
        Files.createDirectories(dir);
        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return name;
    }

    static String detectExtension(byte[] b, int n) {
        if (n >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return "jpg";
        if (n >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') return "png";
        if (n >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') return "gif";
        if (n >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') return "webp";
        return null;
    }
}
