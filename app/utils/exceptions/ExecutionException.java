package utils.exceptions;

/**
 * @author Denis Danilin | denis@danilin.name
 * 05.10.2017 12:49
 * core-router ☭ sweat and blood
 */
public class ExecutionException extends ControllerException {

    public ExecutionException(final String message) {
        super(message);
    }

    protected ExecutionException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    public static ExecutionException createExecutionException(final String message) {
        return new ExecutionException(message);
    }

    @Deprecated
    public static ExecutionException createExternalServiceException() {
        return new ExecutionException(
                "Error occured while interaction with external service",
                "Ошибка при взаимодействии с внешним сервисом");
    }

}
