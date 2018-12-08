package utils.validator;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.entity.Error;
import utils.entity.Errors;
import utils.exceptions.NotValidParameterException;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Request validator base on Play's Action class for only purpose provide form validation api. Created by
 * @author Timur Isachenko | tiisachenko@dasreda.ru at 15.11.2017 15:07
 */
public class RequestValidator extends Action<ValidateRequest> {

    @Inject
    private FormFactory formFactory;

    @Override
    @SuppressWarnings("unchecked")
    public CompletionStage<Result> call(Http.Context ctx) {
        JsonNode json = ctx.request().body().asJson();
        Class targetClass = configuration.value();
        Form form = formFactory.form(targetClass);
        Form requestValidationForm = form.bind(json, configuration.allowedFields());
        if (requestValidationForm.hasErrors()) {
            List<ValidationError> errors = requestValidationForm.allErrors();
            throw NotValidParameterException.createNotValidFields("Invalid request", buildMultipleErrors(errors));
        }
        return delegate.call(ctx);
    }

    private Errors buildMultipleErrors(List<ValidationError> validationErrors) {
        return new Errors(validationErrors
                .stream()
                .map(validationError -> new Error(validationError.key(),
                        "error.required".equals(validationError.message()) ? "Не заполнено обязательное поле" : "Некорректное значение параметра запроса"))
                .collect(Collectors.toList())
        );
    }

}
