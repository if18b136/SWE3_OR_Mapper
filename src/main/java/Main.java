import Database.DatabaseConnection;
import Entities.Person;
import ORM.Manager;
import ORM.MetaData;
import ORM.Statement;
import ORM.StatementUtilityAlternative;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            DatabaseConnection db = DatabaseConnection.getInstance();   //DB Connection Test
            Manager manager = Manager.getInstance();                    // Manager Class Test
            Person timmy = new Person(1,"Timmy","Turner", LocalDate.now());     // Person Class Test
            MetaData metaData = new MetaData();                         // MetaData Class Test

            // MetaData extraction Test
            List<MetaData.fieldData> timmyObject = metaData.fields(timmy);
            for( MetaData.fieldData field : timmyObject) {
                System.out.println(field.type + " - " + field.value);
            }

            // Statement creation Test
            Statement insertPerson = new Statement();
            String insertPersonString = insertPerson.insert(timmy,"t_person");
            System.out.println(insertPersonString);
            String testString = StatementUtilityAlternative.insert(timmy, "t_person");

        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
