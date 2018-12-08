package utils.temporal.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Denis Danilin | denis@danilin.name
 * 02.10.2017 17:27
 * core-router ☭ sweat and blood
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = JsonAdapter.LocalTimedD.class)
@JsonSerialize(using = JsonAdapter.LocalTimedS.class)
public @interface LocalTimed {
}
