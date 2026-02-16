package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class SendInfoCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        resc.setTarget("user");
        return resc;
    }
    
}