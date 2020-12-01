import Database.DatabaseConnection;
import Entities.Course;
import Entities.Person;
import Entities.Student;
import Entities.Teacher;
import ORM.Manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class Main {
    static final Logger mainLogger = LogManager.getLogger("Main Logger");
    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();   //DB Connection Test
            PreparedStatement dropTable = db.prepareStatement("DROP TABLE if exists t_course");
            dropTable.execute();
            dropTable = db.prepareStatement("DROP TABLE if exists t_teacher");
            dropTable.execute();
            dropTable = db.prepareStatement("DROP TABLE if exists t_student");
            dropTable.execute();
            dropTable = db.prepareStatement("DROP TABLE if exists t_person");
            dropTable.execute();

            Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
            Manager.createTable(timmy);
            Manager.save(timmy);
            Person getTimmy = Manager.getObject(Person.class, 1);

            timmy.setFirstName("Thomas");
            Manager.saveOrUpdate(timmy);

            Student std = new Student(1);
            Manager.createTable(std);
            Manager.save(std);

            Teacher thomasTeacher = new Teacher(1);
            Manager.createTable(thomasTeacher);
            Manager.save(thomasTeacher);

            Teacher getThomas = Manager.getObject(Teacher.class, 1);
            System.out.println(getThomas.getFirstName());


//            Person thomas = Manager.getObject(Person.class,1);
//            System.out.println(thomas.getFirstName());


//            Course math = new Course(1,"Applied Mathematics",4.5, thomasTeacher);
//            Manager.createTable(math);
//            Manager.save(math);

//            Person getThomas = Manager.getObject(Person.class,1);
//            System.out.println("Got Person from SQl. " + getThomas.getId() + " => " + getThomas.getFirstName());

//            //TODO - now get Person data when calling for Teacher
//            Teacher getTeacher  = Manager.getObject(Teacher.class, 1);
//            System.out.println(getTeacher.getId());

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
