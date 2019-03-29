package core;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

// TODO Decide on separate DB module necessity. It is necessary if its going to be imported from
//  admin panel server as well. Otherwise - NO

// TODO How to store MySQL scripts? Both DDL and DML ones. Should I keep DML ones as files or in code?
//  Where to store DDL? maybe keep them "gitignored" in Database module?

// TODO Test current AUTH methods + introduce refresh tokens.
//  Decide on whether it is that necessary to keep blacklist for logouts


public class JaxRsAppConfig extends ResourceConfig {
    public JaxRsAppConfig() {
        super();
        this.register(SQLExceptionHandler.class);
        this.register(BadRequestExceptionHandler.class);
        this.register(AuthenticationFilter.class);
        this.register(RolesAllowedDynamicFeature.class);
        this.packages("api");
        AppContext.getInstance().loadDatabaseController("db-remote.properties");
    }
}
