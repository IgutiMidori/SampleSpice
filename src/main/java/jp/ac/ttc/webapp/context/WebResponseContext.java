package jp.ac.ttc.webapp.context;

import jakarta.servlet.http.HttpServletResponse;

public class WebResponseContext implements ResponseContext {
    private Object _result;
    private String _target;
    private HttpServletResponse _res;
    private boolean isRedirect = false;
    
    public WebResponseContext(){}

    public void setTarget(String transferInfo) {
       if(isRedirect) {
            System.out.println("Redirect target: " + transferInfo);
           _target = transferInfo;
       } else {
           _target = "/WEB-INF/jsp/" + transferInfo + ".jsp";
           System.out.println("Forward target: " + _target);
       }
    }

    public String getTarget() {
        return _target;
    }

    public void setResult(Object bean) {
        _result = bean;
    }

    public Object getResult() {
        return _result;
    }

    public void setResponse(Object res) {
        _res = (HttpServletResponse)res;
    }

    public Object getResponse() {
        return _res;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean isRedirect) {
        this.isRedirect = isRedirect;
    }
}
