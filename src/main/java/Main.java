import Database.DatabaseConnection;
import Service.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{ ;
            DatabaseConnection db = DatabaseConnection.getInstance();
            Connection con = db.getConnection();
        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
