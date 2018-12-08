package utils.exceptions;

/**
 * @author Denis Danilin | denis@danilin.name
 * 04.10.2017 12:59
 * core-router ☭ sweat and blood
 */
public class RequiredObjectNotFoundException extends ControllerException {

    public RequiredObjectNotFoundException(final String objectName) {
        super("Не найден требуемый объект: " + objectName);
    }

    private RequiredObjectNotFoundException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    public static RequiredObjectNotFoundException createObjectNotFoundException(final String objectName) {
        return new RequiredObjectNotFoundException("Can not find needed object", "Не найден требуемый объект: " + objectName);
    }

}
