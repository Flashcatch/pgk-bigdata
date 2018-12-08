package utils.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Dudin on 02.11.2017 padudin@dasreda.ru
 */
public class Errors {

    private List<Error> errors;

    public Errors(List<Error> errors) {
        this.errors = errors;
    }

    public Errors() {
        errors = new ArrayList<>();
    }

    public static Errors create(String name, String message) {
        return new Errors().add(new Error(name, message));
    }

    public Errors add(String name, String message) {
        return add(new Error(name, message));
    }

    public Errors add(Error error) {
        errors.add(error);
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Errors errors1 = (Errors) o;

        return errors != null ? errors.equals(errors1.errors) : errors1.errors == null;

    }

    @Override
    public int hashCode() {
        return errors != null ? errors.hashCode() : 0;
    }
}
