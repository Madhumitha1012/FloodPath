package com.floodpath.web;

import com.floodpath.model.User;
import com.floodpath.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        User u = AuthService.getInstance().login(email, req.getParameter("password"));
        if (u == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.setAttribute("email", email == null ? "" : email);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate(); // prevent session fixation
        req.getSession(true).setAttribute("user", u);

        String next = (String) req.getParameter("next");
        if (next != null && next.startsWith(req.getContextPath() + "/") && !next.startsWith("//")
                && !next.contains("login") && !next.contains("register")
                && (u.isAdmin() || !next.contains("admin"))) {
            resp.sendRedirect(next);
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
