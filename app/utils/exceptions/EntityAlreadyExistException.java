package utils.exceptions;

import utils.entity.Errors;

/**
 * @author SandQ
 */
@SuppressWarnings("serializable")
public final class EntityAlreadyExistException extends ControllerException {

    private EntityAlreadyExistException(String message, Errors errors) {
        super(message, errors);
    }

    /**
     * createSlugExist.
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "slug": "Сущность с данным slug уже существует" }
     *   ]
     * }
     * </pre>
     *
     * @param slug slug
     * @return EntityAlreadyExistException
     */
    public static EntityAlreadyExistException createSlugExist(String slug) {
        return new EntityAlreadyExistException(
            "Entity with slug " + slug + " already exists",
            Errors.create("slug", "Сущность с данным slug уже существует"));
    }
}
