package vut.fit.ija.proj1.data.exceptions;

public class StreetsNotConnectedException extends Exception{
    public StreetsNotConnectedException() {
    }

    public StreetsNotConnectedException(String message) {
        super(message);
    }

    public StreetsNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreetsNotConnectedException(Throwable cause) {
        super(cause);
    }

    public StreetsNotConnectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
