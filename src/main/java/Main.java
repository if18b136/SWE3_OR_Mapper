import Database.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            DatabaseConnection db = DatabaseConnection.getInstance();
            System.out.println(db.hashCode());
        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
