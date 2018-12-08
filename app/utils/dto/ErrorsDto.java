package utils.dto;

import utils.entity.Error;
import utils.entity.Errors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Dudin
 * on 04.10.2017
 * padudin@dasreda.ru
 */
public class ErrorsDto {

    private List<ErrorDto> errors;

    public ErrorsDto() {
        errors = new ArrayList<>();
    }

    public List<ErrorDto> getErrors() {
        return errors;
    }

    public ErrorsDto addError(ErrorDto error) {
        this.errors.add(error);
        return this;
    }

    public static ErrorsDto create(String message) {
        ErrorsDto errors = new ErrorsDto();
        errors.addError(new ErrorDto("_error", message));
        return errors;
    }

    public static ErrorsDto instanceOf(Errors e) {
        ErrorsDto errors = new ErrorsDto();
        for (Error error : e.getErrors()) {
            errors.addError(ErrorDto.instanceOf(error));
        }
        return errors;
    }

    public static ErrorsDto instanceOf(Throwable th) {
        ErrorsDto errors = new ErrorsDto();
        errors.addError(new ErrorDto("_error", th.getMessage()));
        return errors;
    }

    @Override
    public String toString() {
        return "ErrorsDto{" +
                "errors=" + errors +
                '}';
    }
}
