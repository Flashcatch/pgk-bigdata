package utils.entity;

/**
 * Created by Pavel Dudin
 * on 02.11.2017
 * padudin@dasreda.ru
 */
public class Error {

    private String name;

    private String message;

    public Error(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public Error(String message) {
        this.name = "_error";
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
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

        Error error = (Error) o;

        if (name != null ? !name.equals(error.name) : error.name != null) {
            return false;
        }
        return message != null ? message.equals(error.message) : error.message == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
