package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class LogoutCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {

        getRequestContext().invalidateSession();

        String url = "productList";
        resc.setRedirect(true);
        resc.setTarget(url);
        return resc;
    }
}
