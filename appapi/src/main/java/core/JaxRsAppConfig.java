package core;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class JaxRsAppConfig extends ResourceConfig {
    public JaxRsAppConfig() {
        super();
        this.register(SQLExceptionHandler.class);
        this.register(BadRequestExceptionHandler.class);
        this.register(AuthenticationRequestFilter.class);
        this.register(RolesAllowedDynamicFeature.class);
        this.register(CORSResponseFilter.class);
        this.packages("api");
        AppContext.getInstance().loadDatabaseController("db-remote.properties");
    }
}
