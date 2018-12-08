package utils.exceptions;

/**
 * @author Denis Danilin | denis@danilin.name
 * 10.10.2017 16:21
 * core-router ☭ sweat and blood
 */
public class ProhibitedOperationException extends ControllerException {
    
    public ProhibitedOperationException(final String message) {
        super("Операция недопустима: " + message);
    }

    public static ProhibitedOperationException createProhibitedOperationException(final String message) {
        return new ProhibitedOperationException(message);
    }
    
}
