package jp.ac.ttc.webapp.context;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public class WebRequestContext implements RequestContext {
    private Map<String, String[]> _params;
    private HttpServletRequest _req;

    public WebRequestContext() {}

    public String getCommandPath() {
        String servletPath = _req.getServletPath();
        System.out.println("WebRequestContext: servletPath = " + servletPath);
        if(servletPath.equals("/")) {
            return "all/productList";
        }
        String commandPath = servletPath.substring(1);
        return commandPath;
    }

    public String[] getParameter(String key) {
        String[] params = _params.get(key);
        return params;
    }

    public boolean hasParameter(String key) {
        String[] params = _params.get(key);
        if(params != null){
            return true;
        }else{
            return false;
        }
    }

    public Object getRequest() {
        return _req;
    }

    public void setRequest(Object req) {
        _req = (HttpServletRequest)req;
        _params = _req.getParameterMap();
    }

    public void setSessionAttribute(String key, Object value) {
        _req.getSession().setAttribute(key, value);
    }

    public Object getSessionAttribute(String key) {
        return _req.getSession().getAttribute(key);
    }

    public void removeSessionAttribute(String key) {
        _req.getSession().removeAttribute(key);
    } 

    public boolean hasSessionAttribute(String key) {
        Object atr = _req.getSession().getAttribute(key);
        if(atr != null){
            return true;
        }else{
            return false;
        }
    }
    public void invalidateSession() {
        if(_req.getSession(false) != null){
            _req.getSession().invalidate();
        }
    }
}
