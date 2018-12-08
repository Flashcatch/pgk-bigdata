package utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utils.dto.ErrorsDto;
import utils.exceptions.ControllerException;
import utils.exceptions.EntityAlreadyExistException;
import utils.exceptions.EntityNotFoundException;
import utils.exceptions.ForbiddenException;
import utils.exceptions.NotValidParameterException;
import utils.exceptions.PaymentRequiredException;
import utils.exceptions.RequiredObjectNotFoundException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

import static play.mvc.Results.status;

/**
 * @author SandQ
 */
@SuppressWarnings("unused")
@Singleton
public class DefaultErrorHandler implements HttpErrorHandler {

    public static final int HTTP_CODE_UNKNOWN_ERROR = 499;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

    /**
     * onClientError.
     * @param request req
     * @param statusCode st code
     * @param message msg
     * @return cp result
     */
    public CompletionStage<Result> onClientError(final Http.RequestHeader request, final int statusCode, final String message) {
        LOG.error("Client error has occurred: statusCode = {}, message = {}", statusCode, message);
        return CompletableFuture.completedFuture(status(statusCode, "Проблема в данных, отправляемых на сервер"));
    }

    /**
     * onServerError.
     * @param request req
     * @param e e
     * @return cp result
     */
    public CompletionStage<Result> onServerError(final Http.RequestHeader request, final Throwable e) {
        return CompletableFuture.completedFuture(onServerErrorCalcResult(request, e));
    }

    protected Result onServerErrorCalcResult(final Http.RequestHeader request, Throwable e) {
        JsonNode jsonNode;
        int status;

        if (e instanceof ControllerException) {

            jsonNode = Json.toJson(ErrorsDto.instanceOf(((ControllerException) e).getErrors()));
            if (e instanceof ForbiddenException) {
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
