package api;

import com.google.gson.Gson;
import core.AppContext;
import core.Database;

import javax.ws.rs.core.Response;

class CommonResource {
    static final Response.ResponseBuilder RESPONSE_BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST);
    static final Response RESPONSE_NOT_IMPLEMENTED = Response.status(Response.Status.NOT_IMPLEMENTED).build();

    Database db = AppContext.getInstance().getDB();
    Gson gson = AppContext.getInstance().GSON;
}
