package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;

public abstract class AbstractCommand {
    private RequestContext reqCon;
    public void init(RequestContext reqc) {
        reqCon = reqc;
    }

    public RequestContext getRequestContext() {
        return reqCon;
    }

    public abstract ResponseContext execute(ResponseContext resc);
}
