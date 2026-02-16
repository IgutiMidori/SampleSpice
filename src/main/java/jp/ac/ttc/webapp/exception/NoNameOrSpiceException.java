package jp.ac.ttc.webapp.exception;

public class NoNameOrSpiceException extends LogicException {
    public NoNameOrSpiceException(String message, Throwable e) {
        super(message, e);
    }
}
