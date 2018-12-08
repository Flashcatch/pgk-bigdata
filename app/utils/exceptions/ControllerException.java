package utils.exceptions;

import utils.entity.Error;
import utils.entity.Errors;

import java.util.List;
import java.util.Map;

/**
 * Created by Pavel Dudin
 * on 11.10.2017
 * padudin@dasreda.ru
 */
public abstract class ControllerException extends RuntimeException {

    private Errors errors;

    protected ControllerException(String message) {
        super(message);
        errors = new Errors();
        errors.add(new Error(message));
    }

    protected ControllerException(String message, String jsonMessage) {
        super(message);
        errors = new Errors();
        errors.add(new Error(jsonMessage));
    }

    protected ControllerException(String logMessage, Map<String, List<String>> map) {
        super(logMessage);
        errors = new Errors();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String value : entry.getValue()) {
                errors.add(new Error(entry.getKey(), value));
            }
        }
    }

    protected ControllerException(String logMessage, Errors errors) {
        super(logMessage);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ControllerException that = (ControllerException) o;

        return errors != null ? errors.equals(that.errors) : that.errors == null;

    }

    @Override
    public int hashCode() {
        return errors != null ? errors.hashCode() : 0;
    }
}
