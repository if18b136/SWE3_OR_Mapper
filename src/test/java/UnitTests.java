import Database.DatabaseConnection;
import Entities.Person;
import ORM.MetaData;
import ORM.Queries.SelectQuery;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class UnitTests {

    @Test
    public void DatabaseConnectionTest() throws SQLException {
        assertNotNull(DatabaseConnection.getInstance());
    }

    @Test
    public void PersonClassTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        assertNotNull(timmy);
        assertEquals(timmy.getId(),1);
        assertEquals(timmy.getFirstName(),"Timmy");
        assertEquals(timmy.getLastName(),"Turner");
        assertEquals(timmy.getBirthDate(), LocalDate.of(1992,5,21));
    }

    @Test
    public void selectQueryTest() {
        SelectQuery selectQuery = new SelectQuery();
        selectQuery.addTargets("birthdate");
        selectQuery.addTables("t_person");
        selectQuery.addCondition("id","1");
        assertNotNull(selectQuery.buildQuery());
        assertEquals("SELECT birthdate FROM t_person WHERE id = 1;",selectQuery.getQuery());
    }

    @Test
    public void selectQuery2TargetsTest() {
        SelectQuery selectQuery = new SelectQuery();
        selectQuery.addTargets("birthdate");
        selectQuery.addTargets("name");
        selectQuery.addTables("t_person");
        selectQuery.addCondition("id","1");
        assertNotNull(selectQuery.buildQuery());
        assertEquals("SELECT birthdate,name FROM t_person WHERE id = 1;",selectQuery.getQuery());
    }

    @Test
    public void selectQuery2TablesTest() {
        SelectQuery selectQuery = new SelectQuery();
        selectQuery.addTargets("birthdate");
        selectQuery.addTables("t_person");
        selectQuery.addTables("t_student");
        selectQuery.addCondition("id","1");
        assertNotNull(selectQuery.buildQuery());
        assertEquals("SELECT birthdate FROM t_person,t_student WHERE id = 1;",selectQuery.getQuery());
    }

    @Test
    public void selectQuery2ConditionsTest() {
        SelectQuery selectQuery = new SelectQuery();
        selectQuery.addTargets("birthdate");
        selectQuery.addTables("t_person");
        selectQuery.addCondition("firstname","John");
        selectQuery.addCondition("lastname","Wick");
        assertNotNull(selectQuery.buildQuery());
        assertEquals("SELECT birthdate FROM t_person WHERE firstname = John AND lastname = Wick;",selectQuery.getQuery());
    }


    // Outdated since entity creation
//    @Test
//    public void InsertStatementCreationTest() throws IllegalAccessException {
//        Statement statement = new Statement();
//        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
//        String insertPersonString = statement.insert(timmy,"person");
//        assertEquals(insertPersonString,"INSERT into person (id, firstName, lastName, birthDate)  VALUES (\"1\", \"Timmy\", \"Turner\", \"1992-05-21\");");
//    }
//
//    @Test
//    public void TableCreationTest() {
//        Person timmy = new Person("Timmy","Turner", LocalDate.now());
//        String tableInit = Statement.initTable(timmy.getClass());
//        assertEquals(tableInit,"CREATE TABLE t_erson ( id int, firstName varchar(255), lastName varchar(255), birthDate date, PRIMARY KEY (id) );");
//    }

    // DO NOT USE!!!
//    @Test
//    public void PreparedStatementTableCreationAndInsertTest() throws SQLException, IllegalAccessException {
//        DatabaseConnection db = DatabaseConnection.getInstance();
//        Statement statement = new Statement();
//        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
//        String tableInit = statement.initTable(timmy.getClass());
//        String insertPersonString = statement.insert(timmy,"person");
//
//        PreparedStatement dropTable = db.getConnection().prepareStatement("DROP TABLE if exists person");
//        dropTable.execute();
//        PreparedStatement initPerson = db.getConnection().prepareStatement(tableInit);
//        initPerson.execute();
//        PreparedStatement insertTimmy = db.getConnection().prepareStatement(insertPersonString);
//        insertTimmy.execute();
//    }

//    @Test
//    public void PreparedStatementTableCreationAndInsertTestWithAnnotations() throws SQLException, IllegalAccessException {
//        DatabaseConnection db = DatabaseConnection.getInstance();
//        Statement statement = new Statement();
//        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
//        String tableInit = statement.initFromClass(timmy.getClass());
//        String insertPersonString = statement.insert(timmy,"person");
//
//        PreparedStatement dropTable = db.getConnection().prepareStatement("DROP TABLE if exists t_person");
//        dropTable.execute();
//        PreparedStatement initPerson = db.getConnection().prepareStatement(tableInit);
//        initPerson.execute();
////        PreparedStatement insertTimmy = db.getConnection().prepareStatement(insertPersonString);
////        insertTimmy.execute();
//    }
}
