package utils.exceptions;

/**
 * Created by Pavel Dudin
 * on 23.09.2017
 * padudin@dasreda.ru
 */
public class AccessDeniedException extends ControllerException {

    protected AccessDeniedException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    public static AccessDeniedException createJwtAndJsonIdsNotEquals(Long ssoId, Long jsonCheckedFieldId) {
        return new AccessDeniedException(
                String.format("Json`s data and soo-id are not equals: ssoId = %d, json checked field id = %d", ssoId, jsonCheckedFieldId),
                "Переданные данные не соответствуют данным из JWT" );
    }

    public static AccessDeniedException createJwtAndEntityIdsNotEquals(Long ssoId, Long entityCheckedFieldId) {
        return new AccessDeniedException(
                String.format("Entity`s data and soo-id are not equals: ssoId = %d, entity checked field id = %d", ssoId, entityCheckedFieldId),
                "Данные из JWT не соответствуют сохраненным данным");
    }

    public static AccessDeniedException createModifyNotOwnEntity(Long ssoId, Long entityCheckedFieldId) {
        return new AccessDeniedException(
                String.format("User try modify not own entity: ssoId = %d, entity checked field id = %d", ssoId, entityCheckedFieldId),
                "Пользователь не может модифицировать чужую запись");
    }

    public static AccessDeniedException createDeletingNotOwnEntities(Long ssoId) {
        return new AccessDeniedException(
                String.format("User can not delete not own entities: ssoId = %d", ssoId),
                "Пользователь не может удалить чужую запись");
    }

    public static AccessDeniedException createAdminRoleRequired(String msg) {
        return new AccessDeniedException(
                "Required admin role for this operation: " + msg,
                "Необходима роль админа для выполнения данной операции: " + msg);
    }
}
