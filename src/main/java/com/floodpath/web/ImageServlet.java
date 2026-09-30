package com.floodpath.web;

import com.floodpath.config.DbConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

/** Serves uploaded report photos (login required, see AuthFilter). */
@WebServlet("/api/image")
public class ImageServlet extends HttpServlet {
    private static final Map<String, String> TYPES = Map.of(
            "jpg", "image/jpeg", "png", "image/png", "gif", "image/gif", "webp", "image/webp");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String f = req.getParameter("f");
        if (f == null || !f.matches("[a-f0-9]{32}\\.(jpg|png|gif|webp)")) { resp.sendError(404); return; }
        Path p = Paths.get(DbConfig.UPLOAD_DIR, f);
        if (!Files.isRegularFile(p)) { resp.sendError(404); return; }
        String ext = f.substring(f.lastIndexOf('.') + 1);
        resp.setContentType(TYPES.get(ext));
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "private, max-age=86400");
        resp.setContentLengthLong(Files.size(p));
        Files.copy(p, resp.getOutputStream());
    }
}
