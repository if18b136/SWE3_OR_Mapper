import Database.DatabaseConnection;
import Entities.Person;
import Entities.Teacher;
import ORM.Manager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;


/**
 * Old Unit test class.
 * will be split up later.
 */
public class UnitTests {

    static Connection db;

    @BeforeAll
    static void init() throws SQLException {
        db = DatabaseConnection.getInstance().getConnection();
        db.prepareStatement("DROP TABLE if exists t_course").execute();
        db.prepareStatement("DROP TABLE if exists t_teacher").execute();
        db.prepareStatement("DROP TABLE if exists t_student").execute();
        db.prepareStatement("DROP TABLE if exists t_person").execute();
    }

    @AfterEach
    void release() throws SQLException {
        db.prepareStatement("DROP TABLE if exists t_course").execute();
        db.prepareStatement("DROP TABLE if exists t_teacher").execute();
        db.prepareStatement("DROP TABLE if exists t_student").execute();
        db.prepareStatement("DROP TABLE if exists t_person").execute();
        Manager.depleteCache();
        Manager.enableCaching(false);
    }


    /**
     * testing if a database connection can be established.
     *
     * @throws SQLException if no database connection could be established.
     */
    @Test
    public void DatabaseConnectionTest() throws SQLException {
        Assertions.assertNotNull(DatabaseConnection.getInstance());
    }

    @Test
    public void baseClassSaveAndGetTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.createTable(timmy);
        Manager.save(timmy);
        Person getTimmy =  Manager.get(Person.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmy.getFirstName(),getTimmy.getFirstName());
    }

    @Test
    public void baseClassUpsertTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.createTable(timmy);
        Manager.save(timmy);
        timmy.setFirstName("Thomas");
        Manager.saveOrUpdate(timmy);
        Person getTimmy =  Manager.get(Person.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmy.getFirstName(),getTimmy.getFirstName());
    }

    @Test
    public void subclassSaveAndGetTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.createTable(timmy);
        Manager.save(timmy);
        Teacher timmyTeacher = new Teacher(1);
        timmyTeacher.setFirstName("Timmy");
        timmyTeacher.setLastName("Turner");
        timmyTeacher.setBirthDate(LocalDate.of(1992,5,21));
        Manager.createTable(timmyTeacher);
        Manager.save(timmyTeacher);
        Teacher getTimmy = Manager.get(Teacher.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmyTeacher.getFirstName(),getTimmy.getFirstName());
    }

    @Test
    public void cachingUpsertTest() {
        Manager.enableCaching(true);
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.createTable(timmy);
        Manager.save(timmy);
        timmy.setFirstName("Thomas");
        Manager.saveOrUpdate(timmy);
        Person getTimmy =  Manager.get(Person.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmy.getFirstName(),getTimmy.getFirstName());
    }


}
