package api;

import pojos.Group;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("groups")
public class GroupsResource extends CommonResource {

    @GET
    public Response getListOfGroups(@QueryParam("names_only") Boolean namesOnlyFlag) throws SQLException {
        if (namesOnlyFlag == null || !namesOnlyFlag) {
            List<Group> groups = db.getGroups();
            return Response.ok(gson.toJson(groups)).build();
        } else {
            List<String> groupNames = db.getGroupNamesOnly();
            return Response.ok(gson.toJson(groupNames)).build();
        }
    }

    @POST
    public Response createGroup() {
        return RESPONSE_NOT_IMPLEMENTED;
    }

    @PUT
    @Path("id/{groupId}")
    public Response updateGroupById(@PathParam("groupId") Long groupId) {
        return RESPONSE_NOT_IMPLEMENTED;
    }

    @PUT
    @Path("number/{groupNumber}")
    public Response updateGroupByNumber(@PathParam("groupNumber") Long groupNumber) {
        return RESPONSE_NOT_IMPLEMENTED;
    }
}
