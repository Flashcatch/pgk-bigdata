package utils.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import utils.entity.Error;

/**
 * Created by Pavel Dudin
 * on 04.10.2017
 * padudin@dasreda.ru
 */
@JsonSerialize(using = ErrorDtoSerializer.class)
public class ErrorDto {

    private String name;

    private String message;

    public ErrorDto() {
    }

    public ErrorDto(String name, String message) {
        this.name = name;
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
        return "ErrorDto{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static ErrorDto instanceOf(Error error) {
        return new ErrorDto(error.getName(), error.getMessage());
    }
}
