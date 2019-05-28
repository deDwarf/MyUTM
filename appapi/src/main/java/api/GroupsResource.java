package api;

import api.common.SimpleCUDResource;
import core.Roles;
import pojos.Group;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("groups")
public class GroupsResource extends SimpleCUDResource<Group> {

    public GroupsResource() {
        super(Group.class, "groups");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfGroups(@QueryParam("names_only") Boolean namesOnlyFlag) throws SQLException {
        if (namesOnlyFlag == null || !namesOnlyFlag) {
            List<Group> groups = db.getGroups();
            return Response.ok(gson.toJson(groups)).build();
        } else {
            List<String> groupNames = db.getGroupNamesOnly();
            return Response.ok(gson.toJson(groupNames)).build();
        }
    }

    @Override
    @POST
    @RolesAllowed({Roles.ADMIN})
    public Response add(String json) {
        return super.add(json);
    }

    @Override
    @PUT
    @RolesAllowed({Roles.ADMIN})
    public Response update(@FormParam("pk") Long id,
                           @FormParam("name") String fieldName,
                           @FormParam("value") String fieldValue) {
        return super.update(id, fieldName, fieldValue);
    }

    @Override
    @DELETE
    @RolesAllowed({Roles.ADMIN})
    @Path("/{groupId}")
    public Response delete(@PathParam("groupId") Long id) {
        return super.delete(id);
    }
}
