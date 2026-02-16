package jp.ac.ttc.webapp.context;

public interface RequestContext {
    public String getCommandPath();
    public String[] getParameter(String key);
    public boolean hasParameter(String key);
    public Object getRequest();
    public void setRequest(Object req);
    public void setSessionAttribute(String key, Object value);
    public Object getSessionAttribute(String key);
    public void removeSessionAttribute(String key);
    public boolean hasSessionAttribute(String key);
    public void invalidateSession();
}
