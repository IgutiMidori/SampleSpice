package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class SendAdminTopCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        resc.setTarget("adminTop");
        return resc;
    }
    
}
