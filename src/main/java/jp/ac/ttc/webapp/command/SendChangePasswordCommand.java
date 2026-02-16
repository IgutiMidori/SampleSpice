package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class SendChangePasswordCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        resc.setTarget("forgotPassword");
        return resc;
    }
    
}
