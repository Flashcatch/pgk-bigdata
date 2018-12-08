package utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utils.dto.ErrorsDto;
import utils.exceptions.*;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.status;

/**
 * Created by Pavel Dudin
 * on 12.10.2017
 * padudin@dasreda.ru
 */
@SuppressWarnings("unused")
@Singleton
public class DefaultErrorHandler implements HttpErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

    public static final int HTTP_CODE_UNKNOWN_ERROR = 499;

    public CompletionStage<Result> onClientError(final Http.RequestHeader request, final int statusCode, final String message) {
        LOG.error("Client error has occurred: statusCode = {}, message = {}", statusCode, message);
        return CompletableFuture.completedFuture(status(statusCode, "Проблема в данных, отправляемых на сервер"));
    }

    public CompletionStage<Result> onServerError(final Http.RequestHeader request, final Throwable e) {
        return CompletableFuture.completedFuture(onServerErrorCalcResult(request, e));
    }

    protected Result onServerErrorCalcResult(final Http.RequestHeader request, Throwable e) {
        JsonNode jsonNode;
        int status;

        while (e instanceof CompletionException && e.getCause() != null) {
            e = e.getCause();
        }

        if (e instanceof ControllerException) {

            jsonNode = Json.toJson(ErrorsDto.instanceOf(((ControllerException)e).getErrors()));
            if (e instanceof AccessDeniedException) {
                status = Http.Status.FORBIDDEN;
            } else if (e instanceof ForbiddenException) {
                status = Http.Status.UNAUTHORIZED;
            } else if (e instanceof EntityAlreadyExistException) {
                status = Http.Status.CONFLICT;
            } else if (e instanceof NotValidParameterException) {
                status = Http.Status.BAD_REQUEST;
            } else if (e instanceof EntityNotFoundException) {
                status = Http.Status.BAD_REQUEST;
            } else if (e instanceof RequiredObjectNotFoundException) {
                status = Http.Status.BAD_REQUEST;
            } else if (e instanceof PaymentRequiredException) {
                status = Http.Status.PAYMENT_REQUIRED;
            } else {
                status = HTTP_CODE_UNKNOWN_ERROR;
            }

        } else {
            jsonNode = Json.toJson(ErrorsDto.create("Произошла ошибка на сервере"));
            status = HTTP_CODE_UNKNOWN_ERROR;
        }

        LOG.debug("status = {}, body = {}", status, jsonNode.toString());
        LOG.error(e.getMessage(), e);
        return status(status, jsonNode);
    }

}
