package utils.exceptions;

/**
 * @author Denis Danilin | denis@danilin.name
 * 04.10.2017 13:13
 * core-router ☭ sweat and blood
 */
public class SiblingForwardException extends ControllerException {
    
    public SiblingForwardException(final String sibling, final String message) {
        super("Ошибка коммуникации с сервисом '" + sibling + "': " + message);
    }

    /**
     * Exception on external service side or in the connect layer. 
     * @return
     */
    public static SiblingForwardException createSiblingForwardException(final String sibling, final String message) {
        return new SiblingForwardException(sibling, message);
    }
    
}
