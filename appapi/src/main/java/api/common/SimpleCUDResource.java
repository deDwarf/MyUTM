package api.common;

import com.google.gson.JsonSyntaxException;
import core.GenericCUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.sql.SQLException;

public abstract class SimpleCUDResource<T> extends CommonResource {
    private final String SCHEMA_NAME = "fcimapp";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private GenericCUDService<T> cudService;
    private boolean cudSeviceInitialized;
    private Class<T> clazz;

    public SimpleCUDResource(Class<T> clazz, String tableName) {
        try {
            this.clazz = clazz;
            cudService = new GenericCUDService<T>(db, SCHEMA_NAME, tableName, clazz);
            cudSeviceInitialized = true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("CUD service for class <".concat(clazz.getName()).concat("> has not been initialized"));
            cudSeviceInitialized = false;
        }
    }

    public Response add(String json){
        log.info("add request received: <>", json);

        if (!cudSeviceInitialized) {
            return Response.status(500).build();
        }
        T obj = gson.fromJson(json, clazz);
        if (obj == null) {
            return Response.status(400).entity("Invalid input data").build();
        }
        try {
            json = (json == null ? "" : json);
            BigInteger id = cudService.insert(gson.fromJson(json, clazz));
            return Response.ok(id).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).entity("Failed to insert teacher <".concat(json).concat(">")).build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("Wrong representation of an object").build();
        }
    }

    public Response update(Long id, String fieldName, String fieldValue){
        log.info("update received: <{}, {}, {}>", id, fieldName, fieldValue);

//        log.info("update received: <{}>", params);
//        Type paramsType = new TypeToken<Map<String, String>>() {}.getType();
//        Map<String, String> parsedParams = gson.fromJson(params, paramsType);
//        if (parsedParams == null
//                || !parsedParams.containsKey("pk")
//                || !parsedParams.containsKey("value")
//                || !parsedParams.containsKey("name")) {
//            return Response.status(400).entity("Invalid input data").build();
//        }
//        Long id = Long.valueOf(parsedParams.get("pk"));
//        String fieldName = parsedParams.get("name");
//        String fieldValue = parsedParams.get("value");

        if (!cudSeviceInitialized) {
            return Response.status(500).build();
        }
        try {
            cudService.update(id, fieldName, fieldValue);
            return Response.ok().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    public Response delete(Long id){
        if (!cudSeviceInitialized) {
            return Response.status(500).build();
        }
        if (id == null || id <= 0) {
            return Response.status(400).entity("Invalid input data").build();
        }
        try {
            cudService.delete(id);
            return Response.ok().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    private Response getById(Long id){
        if (!cudSeviceInitialized) {
            return Response.status(500).build();
        }
        return RESPONSE_NOT_IMPLEMENTED;
    }

    private Response getAll(){
        if (!cudSeviceInitialized) {
            return Response.status(500).build();
        }
        return RESPONSE_NOT_IMPLEMENTED;
    }
}
