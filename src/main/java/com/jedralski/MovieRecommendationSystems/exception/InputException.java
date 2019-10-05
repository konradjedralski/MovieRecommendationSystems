package com.jedralski.MovieRecommendationSystems.exception;

public class InputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InputException() {
        super();
    }

    public InputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(Throwable cause) {
        super(cause);
    }
}
