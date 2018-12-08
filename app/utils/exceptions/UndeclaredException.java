package utils.exceptions;

/**
 * Created by Pavel Dudin
 * on 16.10.2017
 * padudin@dasreda.ru
 */
@Deprecated
public class UndeclaredException extends ControllerException {

    protected UndeclaredException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    /**
     * Create undeclared exception. MUST use specific exception from this package
     * or extend specific exception for new situations.
     *
     * @param excMessage message for logging
     * @param jsonMessage message for client`s api
     * @return
     */
    public static UndeclaredException createUndeclaredException(String excMessage, String jsonMessage) {
        return new UndeclaredException(excMessage, jsonMessage);
    }
}
