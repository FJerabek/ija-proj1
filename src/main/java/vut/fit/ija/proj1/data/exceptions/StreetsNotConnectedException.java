/**
 * @author xjerab25
 * Contains definition of StreetNotConnectedException
 * which is thrown when path is being generated on non connecting streets
 */
package vut.fit.ija.proj1.data.exceptions;

/**
 * Represent exception thrown when path is being generated on non connecting streets
 */
public class StreetsNotConnectedException extends Exception{

    /**
     * Default exception without message
     */
    public StreetsNotConnectedException() {
    }

    /**
     * Exception with description message
     * @param message Exception message
     */
    public StreetsNotConnectedException(String message) {
        super(message);
    }

    /**
     * Exception with description message and cause of the exception
     * @param message Exception message
     * @param cause Exception cause
     */
    public StreetsNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with cause
     * @param cause Exception cause
     */
    public StreetsNotConnectedException(Throwable cause) {
        super(cause);
    }
}
