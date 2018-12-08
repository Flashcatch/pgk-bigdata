package utils.exceptions;

import utils.entity.Errors;

import java.util.List;
import java.util.Map;

/**
 * Use to incoming parameters
 *
 * Created by Pavel Dudin
 * on 29.09.2017
 * padudin@dasreda.ru
 */
public class NotValidParameterException extends ControllerException {

    private NotValidParameterException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    private NotValidParameterException(String logMessage, Map<String, List<String>> map) {
        super(logMessage, map);
    }

    private NotValidParameterException(String logMessage, Errors errors) {
        super(logMessage, errors);
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "_error": "Пустое тело запроса" }
     *   ]
     * }
     * </pre>
     * 
     * @return
     */
    public static NotValidParameterException createEmptyBodyException() {
        return new NotValidParameterException("Request body is empty", "Пустое тело запроса");
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { %fieldName%: "id пользователя пустой" }
     *   ]
     * }
     * , %fieldName% - method parameter
     * </pre>
     * 
     * @param fieldName
     * @return
     */
    public static NotValidParameterException createUserFieldIsNull(String fieldName) {
        return new NotValidParameterException(
                String.format("User id in field '%s' must be not null", fieldName),
                Errors.create(fieldName, "id пользователя пустой"));
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "_error": "id объекта пустой" }
     *   ]
     * }
     * </pre>
     * 
     * @return
     */
    public static NotValidParameterException createIdIsNull() {
        return new NotValidParameterException(
                "id must be not null for field",
                "id объекта пустой");
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { %fieldName%: "id пустой" }
     *   ]
     * }
     * , %fieldName% - method parameter
     * </pre>
     * 
     * @param fieldName
     * @return
     */
    public static NotValidParameterException createIdIsNull(String fieldName) {
        return new NotValidParameterException(
                "id must be not null for field " + fieldName,
                Errors.create(fieldName, "id пустой"));
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { %fieldName%: "Не заполнено обязательное поле" }
     *   ]
     * }
     * , %fieldName% - method parameter
     * </pre>
     * 
     * @param fieldName
     * @return
     */
    public static NotValidParameterException createCustomFieldIsNull(String fieldName) {
        return new NotValidParameterException(
                String.format("'%s' must be not null", fieldName),
                Errors.create(fieldName, "Не заполнено обязательное поле"));
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { "_error": "Некорретный формат JSON" }
     *   ]
     * }
     * </pre>
     * 
     * @return
     */
    public static NotValidParameterException createInvalidJson() {
        return new NotValidParameterException("Invalid JSON format", "Некорретный формат JSON");
    }


    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { %name%: "Не корректное значение параметра запроса" }
     *   ]
     * }
     * , %name% - method parameter
     * </pre>
     * 
     * @param name
     * @param value
     * @return
     */
    public static NotValidParameterException createNotValidQueryParameter(String name, String value) {
        return new NotValidParameterException(
                String.format("Invalid query parameters: name = %s, value = %s", name, value),
                Errors.create(name, "Не корректное значение параметра запроса"));
    }

    /**
     * future json format:
     * <pre>
     * {
     *   "errors": [
     *      { %name%: %jsonMessage% }
     *   ]
     * }
     * , %name%, %jsonMessage% - method parameters
     * </pre>
     * 
     * @param name
     * @param value
     * @param jsonMessage
     * @return
     */
    public static NotValidParameterException createNotValidParameter(String name, String value, String jsonMessage) {
        return new NotValidParameterException(
                String.format("Parameter is not valid: name = %s, value = %s", name, value),
                Errors.create(name, jsonMessage));
    }

    /**
     * Create NotValidParameterException with a lot of nested errors (by Map).
     * This is necessary for further forwarding to the front-end (in DefaultErrorHandler), example:
     * 
     * <pre>
     * {
     *   "errors": [
     *      { "username": "Пользователь уже зарегистрирован" },
     *      { "password": "Пароль должен содержать минимум 8 символов" },
     *      { "password": "Пароль слишком простой" }
     *  ]
     * }
     * </pre>
     *
     * @param logMessage log message
     * @param map key - field name; value - list of field problems
     * @return
     */
    public static NotValidParameterException createNotValidFields(String logMessage, Map<String, List<String>> map) {
        return new NotValidParameterException(logMessage, map);
    }

    /**
     * Create NotValidParameterException with a lot of nested errors (by Errors class).
     *
     * @param logMessage
     * @param errors
     * @return
     */
    public static NotValidParameterException createNotValidFields(String logMessage, Errors errors) {
        return new NotValidParameterException(logMessage, errors);
    }

}
