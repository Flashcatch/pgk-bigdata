package utils.exceptions;

/**
 * Created by Andrey Nikulin
 * on 02.10.2018
 * avnikulin@dasreda.ru
 */
public class PaymentRequiredException extends ControllerException {

    protected PaymentRequiredException(String message, String jsonMessage) {
        super(message, jsonMessage);
    }

    public static PaymentRequiredException createPaymentRequiredException() {
        return new PaymentRequiredException(
                "Required to pay for this operation." ,
                "Оплатите материал, чтобы получить к нему доступ.");
    }
}
