package utils.validator;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be included above validation DTO classes. <code>@ValidateRequest(value =
 * LessonDTO.class)</code>
 * @author Timur Isachenko | tiisachenko@dasreda.ru at 15.11.2017 15:07. example:
 */
@With(RequestValidator.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateRequest {
    Class value() default Object.class;

    String[] allowedFields() default {};
}
