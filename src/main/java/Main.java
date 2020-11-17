import Database.DatabaseConnection;
import Entities.Person;
import Entities.Teacher;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.Queries.SelectQuery;
import ORM.Queries.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();   //DB Connection Test
            Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
            Teacher timmyTeacher = new Teacher(1);

            // get an Entity descriptor object from Timmy - does not contain any info from timmy
            Entity emptyDescriptor = Manager.getEntity(timmy);

            // create a new Entity with info from Timmy object
            Entity timmyEnt = new Entity(timmy);
            Entity timmyTeacherEnt = new Entity(timmyTeacher);

            //System.out.println("Timmy's class type: " + timmyEnt.getEntityClass().toString());
            //System.out.println("Table Name: " + timmyEnt.getTableName());
            Field[] timmyFields = timmyEnt.getFields();

            String insert = Statement.insert(timmyEnt);
            //System.out.println(insert);
            // Table init String creation Test
            String tableInit = Statement.initFromClass(timmy.getClass());
            //System.out.println(tableInit);

            //DB table creation Test
            PreparedStatement dropTable = db.prepareStatement("DROP TABLE if exists t_person");
            dropTable.execute();
            Manager.createTableFromObject(timmy);
            Manager.save(timmy);
            timmy = new Person(1,"Timmy","Neutron", LocalDate.of(1992,5,21));
            Manager.saveOrUpdate(timmy);


            SelectQuery selectQuery = new SelectQuery();
            selectQuery.addCondition("id",1);
            selectQuery.addTables(timmyEnt.getTableName());
            String select = selectQuery.buildQuery();

            Person selectedTimmy = Manager.executeSelect(Person.class, select);
            //System.out.println(selectedTimmy.getFirstName());
            //System.out.println("Timmy's ID: " + selectedTimmy.getId());

            SelectQuery selectVorname = new SelectQuery();
            selectVorname.addTargets("birthdate");
            selectVorname.addTables(timmyEnt.getTableName());
            selectVorname.addCondition("id", 1);
            select = selectVorname.buildQuery();
            //System.out.println(select);
            Date TimmysVorname = Manager.executeSelect(Date.class, select); //
            //System.out.println("Timmy's Geburtstag = " + TimmysVorname);

        // quick error message comparison - all 3 will be printed if eg. db is not reachable
        } catch (Exception e) {
            mainLogger.error(e.getCause());
            System.out.println("-------------------------------------------------");
            mainLogger.error(e);
            System.out.println("-------------------------------------------------");
            mainLogger.error(e.getMessage());
        }
    }
}
