package core;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

public class JaxRsAppConfig extends ResourceConfig {
    public JaxRsAppConfig() {
        super();
        this.register(SQLExceptionHandler.class);
        this.register(BadRequestRuntimeExceptionHandler.class);
        this.register(BadRequestExceptionHandler.class);
        this.register(AuthenticationRequestFilter.class);
        this.register(RolesAllowedDynamicFeature.class);
        this.register(CORSResponseFilter.class);
        this.packages("api");
        AppContext.getInstance().loadDatabaseController("db-remote.properties");

        property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "/pages/freemaker");
        register(FreemarkerMvcFeature.class);;
    }
}
