package core;

import org.glassfish.jersey.server.ResourceConfig;

// TODO Decide on separate DB module necessity. It is necessary if its going to be imported from
//  admin panel server as well. Otherwise - NO

// TODO If DB module is necessary, find how to import it along with all its dependencies.
//  Currently, all those are imported in root POM, however need to keep them in DB module POM only

// TODO Find a way to deploy this service so it is 24/7 accessible for client dev

// TODO How to store MySQL scripts? Both DDL and DML ones. Should I keep DML ones as files or in code?
//  Where to store DDL? maybe keep them "gitignored" in Database module?

// TODO Test current AUTH methods + introduce refresh tokens.
//  Decide on whether it is that necessary to keep blacklist for logouts


public class TomcatAppConfig extends ResourceConfig {
    public TomcatAppConfig() {
        super();
        this.register(SQLExceptionHandler.class);
        this.packages("api");
        Context.getInstance().loadDatabaseController("db-remote.properties");
    }
}
