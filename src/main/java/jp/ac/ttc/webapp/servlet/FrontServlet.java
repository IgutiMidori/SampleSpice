package jp.ac.ttc.webapp.servlet;
import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.controller.ApplicationController;
import jp.ac.ttc.webapp.controller.WebApplicationController;

public class FrontServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws IOException, ServletException {
            doPost(req, res);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws IOException, ServletException{
            req.setCharacterEncoding("UTF-8");

            //ここでJ2EE環境にCommandFactoryが依存
            
           ApplicationController app = new WebApplicationController();
           String servletPath = req.getServletPath();
           if(servletPath.startsWith("/css") || servletPath.startsWith("/scripts") || servletPath.startsWith("/images")) {
                System.out.println("元々の送信方法で転送" + servletPath);
                RequestDispatcher dispatcher = req.getServletContext().getNamedDispatcher("default");
                System.out.println(dispatcher);
                System.out.println("is commited:" + res.isCommitted());
                dispatcher.forward(req, res);
                return;
            } else {
                RequestContext reqc = app.getRequestContext(req);
                System.out.println(reqc);
                ResponseContext resc = app.handleRequest(reqc);
                System.out.println(resc);
                resc.setResponse(res);
                app.handleResponse(reqc, resc);
            }
    }
}
