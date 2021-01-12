import Database.DatabaseConnection;
import Entities.Testing;
import ORM.Base.Entity;
import ORM.Manager;
import ORM.Queries.DeleteQuery;
import ORM.Queries.InsertQuery;
import ORM.Queries.SelectQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryTests {

    static Connection db;

    @BeforeAll
    static void init() throws SQLException {
        db = DatabaseConnection.getInstance().getConnection();
    }

    //--------------------[Create Table Query]--------------------//
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

    //--------------------[Insert into Query]--------------------//
    @Test
    public void insertQueryTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        InsertQuery insert = new InsertQuery();
        insert.buildQuery(test,entity);
        Assertions.assertEquals("INSERT into t_Test (id, text35) VALUES (?, ?);",insert.getQuery());
    }

    //--------------------[Select from Query]--------------------//
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

    //--------------------[Delete from Query]--------------------//
    @Test
    public void deleteQueryTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);

        DeleteQuery delete = new DeleteQuery();
        delete.buildQuery(test,entity);
        Assertions.assertEquals(delete.getQuery(),"DELETE from t_Test WHERE id = ?;");
    }
}
