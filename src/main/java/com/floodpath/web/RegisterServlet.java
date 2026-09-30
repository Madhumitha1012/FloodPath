package com.floodpath.web;

import com.floodpath.model.User;
import com.floodpath.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        try {
            if (password == null || !password.equals(confirm)) throw new IllegalArgumentException("Passwords do not match.");
            User u = AuthService.getInstance().register(name, email, password);
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            req.getSession(true).setAttribute("user", u);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("name", name == null ? "" : name);
            req.setAttribute("email", email == null ? "" : email);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
