package utils.exceptions;

import java.util.Arrays;

/**
 * @author Denis Danilin | denis@danilin.name
 * 04.10.2017 13:19
 * core-router ☭ sweat and blood
 */
public class WrongMethodException extends ControllerException {
    public WrongMethodException(final String used, final String ... allowedMethods) {
        super("Необслуживаемый метод запроса: " + used + (allowedMethods == null || allowedMethods.length == 0 
                ? "" : ". Допустимые методы: " + Arrays.toString(allowedMethods)));
    }

    public static WrongMethodException createWrongMethodException(final String used, final String ... allowedMethods) {
        return new WrongMethodException(used, allowedMethods);
    }
}
