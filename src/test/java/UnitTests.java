import Database.DatabaseConnection;
import Entities.Course;
import Entities.Person;
import Entities.Teacher;
import Entities.Testing;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.Queries.InsertQuery;
import ORM.Queries.SelectQuery;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Old Unit test class.
 * will be split up later.
 */
public class UnitTests {

    static Connection db;

    @BeforeAll
    static void init() throws SQLException {
        db = DatabaseConnection.getInstance().getConnection();
        db.prepareStatement("DROP TABLE if exists t_student_course").execute();
        db.prepareStatement("DROP TABLE if exists t_course").execute();
        db.prepareStatement("DROP TABLE if exists t_teacher").execute();
        db.prepareStatement("DROP TABLE if exists t_student").execute();
        db.prepareStatement("DROP TABLE if exists t_person").execute();
        db.prepareStatement("DROP TABLE if exists t_test").execute();
        db.prepareStatement("CREATE TABLE t_person (id int PRIMARY KEY, firstName varchar(50) NOT NULL, lastName varchar(50) NOT NULL, birthDate date NOT NULL)").execute();
        db.prepareStatement("CREATE TABLE t_teacher (id int PRIMARY KEY, salary double NOT NULL, FOREIGN KEY (id) REFERENCES t_person(id))").execute();
        db.prepareStatement("CREATE TABLE t_course (id int PRIMARY KEY, courseName varchar(50) NOT NULL, ects double NOT NULL, teacher int NOT NULL, FOREIGN KEY (teacher) REFERENCES t_teacher(id))").execute();
        db.prepareStatement("CREATE TABLE t_student (id int PRIMARY KEY, FOREIGN KEY (id) REFERENCES t_person(id))").execute();
        db.prepareStatement("CREATE TABLE t_student_course ( t_student_id int, t_course_id int, FOREIGN KEY (t_student_id) REFERENCES t_student(id), FOREIGN KEY (t_course_id) REFERENCES t_course(id))").execute();
    }

    @AfterEach
    void release() throws SQLException {
        db.prepareStatement("DELETE FROM t_student_course").execute();
        db.prepareStatement("DELETE FROM t_course").execute();
        db.prepareStatement("DELETE FROM t_teacher").execute();
        db.prepareStatement("DELETE FROM t_student").execute();
        db.prepareStatement("DELETE FROM t_person").execute();
        Manager.depleteCache();
        Manager.enableCaching(false);
    }

//-------------------------[Database Tests]-------------------------//
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
        Manager.save(timmy);
        Person getTimmy =  Manager.get(Person.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmy.getFirstName(),getTimmy.getFirstName());
    }

    @Test
    public void baseClassUpsertTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
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
        Manager.save(timmy);
        Teacher timmyTeacher = new Teacher(1, 2500.0);
        timmyTeacher.setFirstName("Timmy");
        timmyTeacher.setLastName("Turner");
        timmyTeacher.setBirthDate(LocalDate.of(1992,5,21));
        Manager.save(timmyTeacher);
        Teacher getTimmy = Manager.get(Teacher.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmyTeacher.getFirstName(),getTimmy.getFirstName());
    }

    @Test
    public void cachingUpsertTest() {
        Manager.enableCaching(true);
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(timmy);
        timmy.setFirstName("Thomas");
        Manager.saveOrUpdate(timmy);
        Person getTimmy =  Manager.get(Person.class, 1);
        Assertions.assertNotNull(getTimmy);
        Assertions.assertEquals(timmy.getFirstName(),getTimmy.getFirstName());
    }


    //-------------------------[Entity Tests]-------------------------//

    @Test
    public void getEntityTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
        Entity timmyEntity = Manager.getEntity(timmy);
        Assertions.assertNotNull(timmyEntity);
        Assertions.assertEquals(timmyEntity.getEntityClass(),Person.class);
    }

    @Test
    public void entityFieldTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
        Entity timmyEntity = Manager.getEntity(timmy);
        Assertions.assertNotNull(timmyEntity.getFields());
        Assertions.assertNotNull(timmyEntity.getPrimaryFields());
        Assertions.assertNotNull(timmyEntity.getInternalFields());
        Assertions.assertNotNull(timmyEntity.getExternalFields());
        Assertions.assertNotNull(timmyEntity.getManyFields());
    }


    //-------------------------[Query Tests]-------------------------//

    @Test
    public void createTableTest() throws Exception {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Manager.createTable(test);
        try {
            Assertions.assertTrue(Manager.tableExists(Manager.getEntity(test).getTableName()));
        } finally {
            db.prepareStatement("DROP TABLE if exists t_test").execute();
        }
    }

    @Test
    public void insertQueryTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        InsertQuery insert = new InsertQuery();
        insert.buildQuery(test,entity);
        Assertions.assertEquals("INSERT into t_Test (id, text35) VALUES (?, ?);",insert.getQuery());
    }

    @Test
    public void selectQueryTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);

        SelectQuery select = new SelectQuery();
        select.setEntity(entity);

        select.addTables(entity.getTableName());
        select.addTargets("text35");
        select.addCondition(entity.getPrimaryFields()[0].getColumnName(),15);
        select.buildQuery();
        Assertions.assertEquals("SELECT text35  FROM t_Test WHERE t_Test.id = 15;",select.getQuery());
    }


}
