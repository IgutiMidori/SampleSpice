package jp.ac.ttc.webapp.exception;

import jakarta.servlet.ServletException;

public class AppException extends ServletException {
    public AppException(String message, Throwable e) {
        super(message, e);
    }
}
