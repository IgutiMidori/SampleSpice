package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class SendLoginCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        String url = "login";
        resc.setTarget(url);
        return resc;
    }
    
}
