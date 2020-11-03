package ORM;

import Database.DatabaseConnection;
import ORM.Base.Entity;
import ORM.Base.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Manager class operates as a single Instance for legal checks and other operational tests
 */
public final class Manager {
    final static Logger managerLogger = LogManager.getLogger("Manager");
    private static Connection db;

    static {
        try {
            db = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    private Manager() {}

    // altering the existing entity object was a bad idea - method creates a new one
    public static Entity SQLinsert(Entity entity) {
        try {
            String insertString = Statement.insert(entity);
            if(doesTableExist(entity.getTableName())) {
                PreparedStatement insert = db.prepareStatement(insertString, java.sql.Statement.RETURN_GENERATED_KEYS);
                insert.executeUpdate();
                ResultSet res = insert.getGeneratedKeys();
                if(res.next()) {
                    int id = res.getInt(1);
                    return null;
                }

            }
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
        return null;
    }

    //TODO create a generic select statement or enough options for different selects
   public static Entity SQLselectAll(Entity entity,int id) throws SQLException {
        PreparedStatement select = db.prepareStatement("select * from ? where ? = ?");
        select.setString(1,entity.getTableName());
        for(Field field : entity.getFields()) {
            if (field.isPrimary()) {
                select.setString(2,field.getColumnName());
                break;
            }
        }
        select.setInt(3,id);


        return null;
   }

    private static boolean doesTableExist(String tableName) throws SQLException {
        PreparedStatement table = db.prepareStatement("show tables like ?;");
        table.setString(1,tableName);
        ResultSet res = table.executeQuery();
        return res.next();
    }

}
