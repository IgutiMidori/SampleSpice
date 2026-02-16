package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.ResponseContext;

public class StartCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        String url = "start";
        resc.setTarget(url);
        return resc;
    }
    
}
