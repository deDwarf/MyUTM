package api.common;

import com.google.gson.Gson;
import core.AppContext;
import core.Database;
import exceptions.InvalidDateFormatException;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class CommonResource {
    protected static final Response.ResponseBuilder RESPONSE_BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST);
    protected static final Response RESPONSE_NOT_IMPLEMENTED = Response.status(Response.Status.NOT_IMPLEMENTED).build();

    protected final Database db = AppContext.getInstance().getDB();
    protected final Gson gson = AppContext.getInstance().GSON;

    private final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    protected final Date parseDate(String date) {
        Date parsedDate;
        try {
            parsedDate = f.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("date", date);
        }
        return parsedDate;
    }

    protected final String formatDate(Date date) {
        return f.format(date);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean validateNotEmpty(Object... params) {
        for (Object p: params) {
            if (p == null || (p instanceof String && ((String)p).trim().equals(""))) {
                return false;
            }
        }
        return true;
    }

 }
