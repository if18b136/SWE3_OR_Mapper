import Database.DatabaseConnection;
import Entities.Person;
import ORM.Annotations.Table;
import ORM.Manager;
import ORM.MetaData;
import ORM.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            DatabaseConnection db = DatabaseConnection.getInstance();   //DB Connection Test
            Manager manager = Manager.getInstance();                    // Manager Class Test
            Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test

            // MetaData extraction Test
            List<MetaData.fieldData> timmyObject = MetaData.objectMetaData(timmy);
            for( MetaData.fieldData field : timmyObject) {
                System.out.println(field.type + " - " + field.value);
            }

            // Statement creation Test
            Statement statement = new Statement();
            String insertPersonString = statement.insert(timmy,"person");
            System.out.println(insertPersonString);

            // Table init String creation Test
            String tableInit = statement.initTable(timmy.getClass());
            System.out.println(tableInit);

            //DB table creation Test
            PreparedStatement dropTable = db.getConnection().prepareStatement("DROP TABLE if exists person");
            dropTable.execute();
            PreparedStatement initPerson = db.getConnection().prepareStatement(tableInit);
            initPerson.execute();
            PreparedStatement insertTimmy = db.getConnection().prepareStatement(insertPersonString);
            insertTimmy.execute();

            //Annotation Test
            Table an = timmy.getClass().getAnnotation(Table.class);
            System.out.println(an.name());
            System.out.println(an);
            System.out.println(an.annotationType());
        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
