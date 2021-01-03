import Database.DatabaseConnection;

import Entities.*;
import ORM.Manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

/**
 * @author Maximilian Rotter
 * @version 0.something
 * Main class that executes program with given arguments
 */
public class Main {
    /**
     * Logger instance for main class.
     */
    static final Logger mainLogger = LogManager.getLogger("Main Logger");

    /**
     * Runnable main class for program start.
     *
     * @param args  Program start input arguments.
     */
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

            timmy.setFirstName("Thomas");
            Manager.saveOrUpdate(timmy);

            //TODO superclass instance needs to be created if not exists - but how to determine when it needs to be created and when not?
            //Not necessary - just describe it in show program

            Student std = new Student(1);
            Manager.createTable(std);
            Manager.save(std);

            Teacher thomasTeacher = new Teacher(1);
            Manager.createTable(thomasTeacher);
            Manager.save(thomasTeacher);

            Teacher getThomas =  Manager.get(Teacher.class, 1);
            System.out.println(getThomas.getFirstName());

            Teacher getThomasFromCache =  Manager.get(Teacher.class, 1);
            System.out.println(getThomasFromCache.getBirthDate());

            Course mathematics = new Course(1,"Math", 3.5, thomasTeacher);
            Manager.createTable(mathematics);
            Manager.save(mathematics);

        } catch (Exception e) {
            mainLogger.error(e);
            e.printStackTrace();
        }
    }
}
