package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class SendSigninCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        resc.setTarget("signIn");
        return resc;
    }
     
}
