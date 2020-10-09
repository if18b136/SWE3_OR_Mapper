package ORM;

import Database.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * The Manager class operates as a single Instance for legal checks and other operational tests
 */
public class Manager {
    final static Logger managerLogger = LogManager.getLogger("Manager");
    private static Manager manager;

    public Manager() {
    }

    public static Manager getInstance() {
        if (manager == null) {
            synchronized (Manager.class) {
                if (manager == null) {
                    manager = new Manager();
                    managerLogger.info("Manager initialized.");
                }
            }
        }
        return manager;
    }


}
