package utils.exceptions;

/**
 * Created by Pavel Dudin
 * on 23.09.2017
 * padudin@dasreda.ru
 */
public class EntityExpiredException extends ControllerException {

    private EntityExpiredException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    public static EntityExpiredException createEntityExpired() {
        return new EntityExpiredException(
                "Entity is already expired", "Время жизни сущности истекло");
    }
}
