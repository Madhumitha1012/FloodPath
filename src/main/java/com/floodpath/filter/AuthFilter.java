package com.floodpath.filter;

import com.floodpath.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;


@WebFilter("/*")

public class AuthFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req=(HttpServletRequest)request; 
        HttpServletResponse resp=(HttpServletResponse)response; 

        String ctx=req.getContextPath(); 
        String path=req.getRequestURI().substring(ctx.length()); 

        if (isPublic(path)) { 
            chain.doFilter(request,response); 
            return; 
        } 

        HttpSession s=req.getSession(false); 
        User u=s==null?null:(User)s.getAttribute("user"); 

        boolean api=path.startsWith("/api/"); 

        if (u==null) { 
            if(api){
                resp.sendError(401);
                return;
            } 

            String next=req.getRequestURI(); 

            if(req.getQueryString()!=null) 
                next+="?"+req.getQueryString(); 

            resp.sendRedirect(ctx+"/login?next="+URLEncoder.encode(next,"UTF-8")); 
            return; 
        } 

        boolean admin=path.startsWith("/admin/") || path.equals("/api/simulate"); 

        boolean user=path.startsWith("/user/"); 

        if ((admin&&!u.isAdmin()) || (user&&u.isAdmin())) { 

            if(api)
                resp.sendError(403); 
            else
                resp.sendRedirect(ctx+"/dashboard"); 

            return; 
        } 

        chain.doFilter(request,response); 
    } 


    private boolean isPublic(String p){ 

        return p.equals("/")||p.equals("/index.jsp")||p.equals("/login")||p.equals("/register")||p.equals("/login.jsp")||p.equals("/register.jsp")||p.equals("/logout")||p.startsWith("/assets/")||p.startsWith("/ws/"); 
    } 
}