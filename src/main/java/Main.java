import Database.DatabaseConnection;
import Entities.Person;
import ORM.Manager;
import ORM.MetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            DatabaseConnection db = DatabaseConnection.getInstance();
            Manager manager = Manager.getInstance();
            Person timmy = new Person(1,"Timmy","Turner", LocalDate.now());
            MetaData metaData = new MetaData();
            List<MetaData.fieldData> timmyObject = metaData.fields(timmy);
            for( MetaData.fieldData field : timmyObject) {
                System.out.println(field.type + " - " + field.value);
            }
        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
