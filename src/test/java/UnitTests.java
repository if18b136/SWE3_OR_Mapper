import Database.DatabaseConnection;
import Entities.Person;
import ORM.Manager;
import ORM.MetaData;
import ORM.Statement;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class UnitTests {

    @Test
    public void DatabaseConnectionTest() throws SQLException {
        assertNotNull(DatabaseConnection.getInstance());
    }

    @Test
    public void ManagerClassTest() {
        assertNotNull(Manager.getInstance());
    }

    @Test
    public void PersonClassTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        assertNotNull(timmy);
        assertEquals(timmy.getID(),1);
        assertEquals(timmy.getFirstName(),"Timmy");
        assertEquals(timmy.getLastName(),"Turner");
        assertEquals(timmy.getBirthDate(), LocalDate.of(1992,5,21));
    }

    @Test
    public void MetaDataExtractionTest() throws IllegalAccessException {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.now());
        List<MetaData.fieldData> timmyObject = MetaData.objectMetaData(timmy);
        for( MetaData.fieldData field : timmyObject) {
            assertNotNull(field.type);
            assertNotNull(field.value);
        }
    }

    @Test
    public void InsertStatementCreationTest() throws IllegalAccessException {
        Statement statement = new Statement();
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        String insertPersonString = statement.insert(timmy,"person");
        assertEquals(insertPersonString,"INSERT into person (ID, firstName, lastName, birthDate)  VALUES (\"1\", \"Timmy\", \"Turner\", \"1992-05-21\");");
    }

    @Test
    public void TableCreationTest() {
        Statement statement = new Statement();
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.now());
        String tableInit = statement.initTable(timmy.getClass());
        assertEquals(tableInit,"CREATE TABLE Person ( ID int, firstName varchar(255), lastName varchar(255), birthDate date, PRIMARY KEY (ID) );");
    }

    // DO NOT USE!!!
    @Test
    public void PreparedStatementTableCreationAndInsertTest() throws SQLException, IllegalAccessException {
        DatabaseConnection db = DatabaseConnection.getInstance();
        Statement statement = new Statement();
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        String tableInit = statement.initTable(timmy.getClass());
        String insertPersonString = statement.insert(timmy,"person");

        PreparedStatement dropTable = db.getConnection().prepareStatement("DROP TABLE if exists person");
        dropTable.execute();
        PreparedStatement initPerson = db.getConnection().prepareStatement(tableInit);
        initPerson.execute();
        PreparedStatement insertTimmy = db.getConnection().prepareStatement(insertPersonString);
        insertTimmy.execute();
    }

    @Test
    public void PreparedStatementTableCreationAndInsertTestWithAnnotations() throws SQLException, IllegalAccessException {
        DatabaseConnection db = DatabaseConnection.getInstance();
        Statement statement = new Statement();
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        String tableInit = statement.initFromClass(timmy.getClass());
        String insertPersonString = statement.insert(timmy,"person");

        PreparedStatement dropTable = db.getConnection().prepareStatement("DROP TABLE if exists t_person");
        dropTable.execute();
        PreparedStatement initPerson = db.getConnection().prepareStatement(tableInit);
        initPerson.execute();
//        PreparedStatement insertTimmy = db.getConnection().prepareStatement(insertPersonString);
//        insertTimmy.execute();
    }
}
