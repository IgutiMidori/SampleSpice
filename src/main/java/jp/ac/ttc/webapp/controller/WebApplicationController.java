package jp.ac.ttc.webapp.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.ttc.webapp.command.AbstractCommand;
import jp.ac.ttc.webapp.command.CommandFactory;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.context.WebRequestContext;
import jp.ac.ttc.webapp.context.WebResponseContext;

public class WebApplicationController implements ApplicationController{
    @Override
    public RequestContext getRequestContext(Object req) {
        RequestContext reqc = new WebRequestContext();
        reqc.setRequest(req);
        return reqc;
    }

    @Override
    public ResponseContext handleRequest(RequestContext reqc) {
        AbstractCommand command = CommandFactory.getCommand(reqc);
        command.init(reqc);
        ResponseContext resc = command.execute(new WebResponseContext());
        return resc;
    }

    @Override
    public void handleResponse(RequestContext reqc, ResponseContext resc) {
        HttpServletRequest req = (HttpServletRequest)reqc.getRequest();
        HttpServletResponse res = (HttpServletResponse)resc.getResponse();

        Object result = resc.getResult();
        req.setAttribute("result", result);
        System.out.println("target: " + resc.getTarget());
        System.out.println("isRedirect: " + resc.isRedirect());
        if(resc.isRedirect()) {
            try {
                res.sendRedirect(resc.getTarget());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RequestDispatcher dispatcher = req.getRequestDispatcher(resc.getTarget());
            try {
                dispatcher.forward(req, res);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
