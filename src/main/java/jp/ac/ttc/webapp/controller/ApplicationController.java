package jp.ac.ttc.webapp.controller;

import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;

public interface ApplicationController {
    RequestContext getRequestContext(Object req);
    ResponseContext handleRequest(RequestContext reqc);
    void handleResponse(RequestContext reqc, ResponseContext resc);
}