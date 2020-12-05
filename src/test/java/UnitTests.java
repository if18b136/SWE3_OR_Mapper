import Database.DatabaseConnection;
import Entities.Person;
import ORM.MetaData;
import ORM.Queries.SelectQuery;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;


/**
 * Old Unit test class.
 * will be split up later.
 */
public class UnitTests {

    /**
     * testing if a database connection can be established.
     *
     * @throws SQLException if no database connection could be established.
     */
    @Test
    public void DatabaseConnectionTest() throws SQLException {
        assertNotNull(DatabaseConnection.getInstance());
    }
}
