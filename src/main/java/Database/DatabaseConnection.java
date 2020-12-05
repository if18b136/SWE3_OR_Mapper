package Database;
import Service.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton implementation of the database connection.
 * currently receives login data from config file in plain text.
 */
public class DatabaseConnection {
    /**
     * Database connection logger.
     */
    final static Logger DBConLogger = LogManager.getLogger("Database Connection");
    /**
     * Static database Connection object.
     */
    private static DatabaseConnection jdbc;
    /**
     * private database connection.
     */
    private Connection con;

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

            this.con = DriverManager.getConnection(
                    Config.getInstance().getProperties().getProperty("url"),
                    Config.getInstance().getProperties().getProperty("username"),
                    Config.getInstance().getProperties().getProperty("password"));
            DBConLogger.info("Database initialised.");
        } catch (ClassNotFoundException cnf) {
            DBConLogger.error("Database Connection Creation Failed : " + cnf.getMessage());
        }
    }

    /**
     * Public connection call.
     *
     * @return the active Database Connection object
     */
    public Connection getConnection() {
        return this.con;
    }

    /**
     * Race condition handler for threading.
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
