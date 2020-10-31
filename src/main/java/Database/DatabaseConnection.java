package Database;
import Service.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    final static Logger DBConLogger = LogManager.getLogger("Database Connection");
    private static DatabaseConnection jdbc;
    private Connection con;
    private String url = "";
    private String username = "";
    private String password = "";

    // TODO - DBConnection as utility class perhaps?
    /**
     * Static Singleton initialisation.
     * The DBConnection is a Singleton that does not need to be initialized again for every call.
     * In the Constructor it also calls the Config class to receive it's database configuration data.
     *
     * @throws SQLException SqlException
     */
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName(Config.getInstance().getProperties().getProperty("driver"));
            this.url = Config.getInstance().getProperties().getProperty("url");
            this.username = Config.getInstance().getProperties().getProperty("username");
            this.password = Config.getInstance().getProperties().getProperty("password");
            this.con = DriverManager.getConnection(url, username, password);
            DBConLogger.info("Database initialised.");
        } catch (ClassNotFoundException cnf) {
            DBConLogger.error("Database Connection Creation Failed : " + cnf.getMessage());
        }
    }

    /**
     * Function to call the connection.
     *
     * @return the active Database Connection object
     */
    public Connection getConnection() {
        return this.con;
    }

    /**
     * Synchronizing the whole class creates huge thread overhead,
     * because only one thread can access the getInstance at a time
     * By making a second instance check, which we synchronize, we minimize that overhead
     *
     * @return the database connection singleton object
     * @throws SQLException if something went wrong with the connection establishing
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (jdbc == null) {
            synchronized (DatabaseConnection.class) {
                if (jdbc == null) {
                    jdbc = new DatabaseConnection();
                    DBConLogger.info("Database Connection created successfully.");
                }
            }
        } else if (jdbc.getConnection().isClosed()) {
            jdbc = new DatabaseConnection();
            DBConLogger.info("New Database Connection established.");
        }
        return jdbc;
    }

}
