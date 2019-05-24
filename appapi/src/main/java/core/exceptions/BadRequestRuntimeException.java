package core.exceptions;

import api.common.Message;
import com.google.gson.Gson;

public class BadRequestRuntimeException extends RuntimeException {
    private Message message;
    private static final Gson g = new Gson();

    public BadRequestRuntimeException(String message) {
        super(g.toJson(message));
    }
}
