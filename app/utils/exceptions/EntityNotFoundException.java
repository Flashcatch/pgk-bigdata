package utils.exceptions;

/**
 * Use to db entities Created by Pavel Dudin on 22.09.2017 padudin@dasreda.ru
 */
public class EntityNotFoundException extends ControllerException {

    private EntityNotFoundException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    /**
     * creates Entity Not Found exception.
     * @param id of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createEntityNotFound(Long id) {
        return new EntityNotFoundException("Can not find entity with id = " + id, "Запись с данным id не найдена");
    }

    /**
     * creates Entity Not Found exception.
     * @param name of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createEntityNotFoundByName(String name) {
        return new EntityNotFoundException("Can not find entity with name = " + name, "Запись с данным именем не найдена");
    }

    /**
     * creates Entity Not Found exception.
     * @param fieldName of entity
     * @param value     of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createEntityNotFoundByField(String fieldName, Object value) {
        return new EntityNotFoundException(
                String.format("Can not find entity by field = '%s' with value = '%s'", fieldName, value == null ? null : value.toString()),
                String.format("Запись не найдена: имя поля = '%s', значение = '%s'", fieldName, value == null ? null : value.toString()));
    }

    /**
     * creates Entity Not Found exception.
     * @param entityName of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createEntityNotFound(String entityName) {
        return new EntityNotFoundException("Can not find any '" + entityName + "' entity", "Записи не найдены");
    }

    /**
     * creates Entity Not Found exception.
     * @param userId of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createEntityNotFoundByUserID(Long userId) {
        return new EntityNotFoundException("Can not find entity by userId = " + userId, "Запись для заданного userId не найдена");
    }

    /**
     * creates Entity Not Found exception.
     * @param slug of entity
     * @return EntityNotFoundException
     */
    public static EntityNotFoundException createSlugNotExist(String slug) {
        return new EntityNotFoundException("Can not find entity with slug = " + slug, "Страница с данным slug не найдена");
    }

}
