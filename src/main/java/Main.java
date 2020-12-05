import Database.DatabaseConnection;

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

            timmy.setFirstName("Thomas");
            Manager.saveOrUpdate(timmy);

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

        } catch (Exception e) {
            mainLogger.error(e);
            e.printStackTrace();
        }
    }
}
