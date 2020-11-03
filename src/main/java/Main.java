import Database.DatabaseConnection;
import Entities.Person;
import Entities.PersonNoAI;
import ORM.Annotations.Table;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.MetaData;
import ORM.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();   //DB Connection Test
            Person timmy = new Person("Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
            PersonNoAI test = new PersonNoAI(5,"Timmy","Turner", LocalDate.of(1992,5,21));

            // get an Entity object from Timmy
            Entity timmyEnt = new Entity(timmy);
            System.out.println("Timmy's class type: " + timmyEnt.getEntityClass().toString());
            System.out.println("Table Name: " + timmyEnt.getTableName());
            Field[] timmyFields = timmyEnt.getFields();
            for(Field field : timmyFields) {
                System.out.println("Value: " + field.getValue() + " - Type: " + field.getFieldType());
            }


            String insert = Statement.insert(timmyEnt);
            System.out.println(insert);
            // Table init String creation Test
            String tableInit = Statement.initFromClass(timmy.getClass());
            System.out.println(tableInit);

            //DB table creation Test
//            PreparedStatement dropTable = db.prepareStatement("DROP TABLE if exists t_person");
//            dropTable.execute();
//            PreparedStatement initPerson = db.prepareStatement(tableInit);
//            initPerson.execute();
            PreparedStatement insertTimmy = db.prepareStatement(insert, java.sql.Statement.RETURN_GENERATED_KEYS);
            insertTimmy.executeUpdate();
            ResultSet resultSet = insertTimmy.getGeneratedKeys();
            while(resultSet.next())
                System.out.println("Key: " + resultSet.getInt(1));








//            insert = stmt.insert(new Entity(test));
//            insertTimmy = db.getConnection().prepareStatement(insert);
//            insertTimmy.execute();

//            // MetaData extraction Test
//            List<MetaData.fieldData> timmyObject = MetaData.objectMetaData(timmy);
//            for( MetaData.fieldData field : timmyObject) {
//                System.out.println(field.type + " - " + field.value);
//            }
//

//            Person james = new Person(15, "test", "test",LocalDate.now());
//            insertPersonString = statement.insert(james,"t_person");
//            db.getConnection().prepareStatement(insertPersonString);
//            insertTimmy.execute();

            //Annotation Test
//            Table an = timmy.getClass().getAnnotation(Table.class);
//            System.out.println(an.name());
//            System.out.println(an);
//            System.out.println(an.annotationType());
//
//            //Init Table from Annotations Test
//            String timmyInit = statement.initFromClass(timmy.getClass());
//
//            System.out.println(timmyInit);


        } catch (Exception e) {
            mainLogger.error(e);
        }
    }
}
