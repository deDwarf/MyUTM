package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class AppContext {
    private static AppContext inst = new AppContext();
    private Database db;
    public final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd")
            .create();

    public static AppContext getInstance() {
        return inst;
    }

    private AppContext() {
    }

    public void loadDatabaseController(String propertiesFile) {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try (InputStream propFileStream = loader.getResourceAsStream(propertiesFile)){
            prop.load(propFileStream);
            String host = prop.getProperty("db-hostname");
            String port = prop.getProperty("db-port");
            String dbname = prop.getProperty("db-name");
            String uname = prop.getProperty("db-login");
            String password = prop.getProperty("db-password");
            try {
                db = Database.create(host, Integer.parseInt(port), dbname, uname, password);
            } catch (SQLException e) {
                throw new RuntimeException("Not able to connect to database instance");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find DB properties file: " + propertiesFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from DB properties file: " + propertiesFile);
        }
    }

    public Database getDB() {
        return db;
    }
}
