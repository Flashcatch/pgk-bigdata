package utils.exceptions;

/**
 * @author Denis Danilin | denis@danilin.name
 * 04.10.2017 13:53
 * core-router ☭ sweat and blood
 */
public class ForbiddenException extends ControllerException {
    
    public ForbiddenException() {
        super("Доступ запрещён");
    }
    
    public static ForbiddenException createForbiddenException() {
        return new ForbiddenException();
    }
    
}
