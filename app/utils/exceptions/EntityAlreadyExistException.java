package utils.exceptions;

import utils.entity.Errors;

/**
 * Created by Pavel Dudin on 29.09.2017 padudin@dasreda.ru
 */
public class EntityAlreadyExistException extends ControllerException {

    private EntityAlreadyExistException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    private EntityAlreadyExistException(String message, Errors errors) {
        super(message, errors);
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "slug": "Сущность с данным slug уже существует" }
     *   ]
     * }
     * </pre>
     * @param slug
     * @return
     */
    public static EntityAlreadyExistException createSlugExist(String slug) {
        return new EntityAlreadyExistException(
                "Entity with slug " + slug + " already exists",
                Errors.create("slug", "Сущность с данным slug уже существует"));
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "entity": "Сущность entity уже существует" }
     *   ]
     * }
     * </pre>
     * @param entity
     * @return
     */
    public static EntityAlreadyExistException createEntityExist(String entity) {
        return new EntityAlreadyExistException(
                "Entity: " + entity + " already exists",
                Errors.create("entity", "Сущность " + entity + "уже существует"));
    }
}
