import Database.DatabaseConnection;
import Entities.Course;
import Entities.Person;
import Entities.PersonNoAI;
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
import java.util.List;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();   //DB Connection Test
            Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
            PreparedStatement dropTable = db.prepareStatement("DROP TABLE if exists t_teacher");
            dropTable.execute();
            dropTable = db.prepareStatement("DROP TABLE if exists t_person");
            dropTable.execute();
            Manager.createTable(timmy);

            Manager.save(timmy);
            timmy.setFirstName("Thomas");
            Manager.saveOrUpdate(timmy);

            Person thomas = Manager.getObject(Person.class,1);
            System.out.println(thomas.getFirstName());

            Teacher thomasTeacher = new Teacher(1,1);

            Manager.createTable(thomasTeacher);
            Manager.save(thomasTeacher);



//            Teacher timmyTeacher = new Teacher(1);
//
//            // get an Entity descriptor object from Timmy - does not contain any info from timmy
//            Entity emptyDescriptor = Manager.getEntity(timmy);
//
//            // create a new Entity with info from Timmy object
//            Entity timmyEnt = new Entity(timmy);
//            Entity timmyTeacherEnt = new Entity(timmyTeacher);
//
//            //DB table creation Test
//
//            Manager.save(timmy);
//            timmy = new Person(1,"Timmy","Neutron", LocalDate.of(1992,5,21));
//            Manager.saveOrUpdate(timmy);
//
//
//            SelectQuery selectQuery = new SelectQuery();
//            selectQuery.addCondition("id",1);
//            selectQuery.addTables(timmyEnt.getTableName());
//            String select = selectQuery.buildQuery();
//
//            Person selectedTimmy = Manager.executeSelect(Person.class, select);
//            //System.out.println(selectedTimmy.getFirstName());
//            //System.out.println("Timmy's ID: " + selectedTimmy.getId());
//
//            SelectQuery selectVorname = new SelectQuery();
//            selectVorname.addTargets("birthdate");
//            selectVorname.addTables(timmyEnt.getTableName());
//            selectVorname.addCondition("id", 1);
//            select = selectVorname.buildQuery();
//            //System.out.println(select);
//            Date TimmysVorname = Manager.executeSelect(Date.class, select); //
//            //System.out.println("Timmy's Geburtstag = " + TimmysVorname);

        // quick error message comparison - all 3 will be printed if eg. db is not reachable
        } catch (Exception e) {
            mainLogger.error(e);
            e.printStackTrace();
        }
    }
}
