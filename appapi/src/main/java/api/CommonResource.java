package api;

import com.google.gson.Gson;
import core.AppContext;
import core.Database;
import exceptions.InvalidDateFormatException;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class CommonResource {
    static final Response.ResponseBuilder RESPONSE_BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST);
    static final Response RESPONSE_NOT_IMPLEMENTED = Response.status(Response.Status.NOT_IMPLEMENTED).build();

    final Database db = AppContext.getInstance().getDB();
    final Gson gson = AppContext.getInstance().GSON;

    private final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    final Date parseDate(String date) {
        Date parsedDate;
        try {
            parsedDate = f.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("date", date);
        }
        return parsedDate;
    }

    final String formatDate(Date date) {
        return f.format(date);
    }

 }
