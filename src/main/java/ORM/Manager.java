package ORM;

import Database.DatabaseConnection;
import ORM.Annotations.Column;
import ORM.Base.Entity;
import ORM.Base.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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


    // TODO currently only useful for single entries
    public static <T> T executeSelect(Class<T> type, String query) {
        try{
            java.sql.Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
            ResultSet res = stmt.executeQuery(query);

            if(res.next()){
                //T t = type.newInstance();     // deprecated
                T t = type.getDeclaredConstructor().newInstance();
//                if(t.getClass().equals(String.class)) {     //TODO delete this bad code and replace with something that makes sense.
//                    System.out.println("String needs separate handling.");
//                    t = (T) res.getString(1);
//                } else {
                    loadIntoObject(res,t);
//                }
                return t;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void loadIntoObject(ResultSet res, Object object) throws SQLException, IllegalAccessException, NoSuchFieldException {
        Class<?> objectClass = object.getClass();

        if(!objectClass.isPrimitive() && !objectClass.equals(String.class)) {   //TODO make exclusion of non-custom objects more generic than this (date won't work either)
            for(java.lang.reflect.Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = res.getObject(field.getName());
                Class<?> type = field.getType();
                if(type.isPrimitive()) {    //TODO check if own class does the same
                    Class<?> boxed = boxPrimitiveClass(type);
                    value = boxed.cast(value);
                } else if(value.getClass() == Date.class) {  // TODO convert externally?
                    value = ((Date) value).toLocalDate();
                }
                field.set(object, value);
            }
        } else {
            // This is the way to get to more than one columns, currently not needed
            for(int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                //System.out.println("    " + res.getMetaData().getColumnName(i));
                //java.lang.reflect.Field field = objectClass.getField(res.getMetaData().getColumnName(i));
                for(java.lang.reflect.Field field : objectClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    System.out.println("    " + field.getName());
                }
                java.lang.reflect.Field field = objectClass.getField("value");
                field.setAccessible(true);
                System.out.println("        " + field.getName());

//                field.setAccessible(true);
//                Object value = res.getObject(objectClass.getName());
//                field.set(object,value);
            }
        }
    }

    private static boolean isPrimitive(Class<?> type) {
        return (type == int.class ||
                type == long.class ||
                type == double.class ||
                type == float.class ||
                type == boolean.class ||
                type == byte.class ||
                type == char.class ||
                type == short.class);
    }

    //TODO check advantage of using boxed java classes
    private static Class<?> boxPrimitiveClass(Class<?> type) {
        if (int.class.equals(type)) {
            return Integer.class;
        } else if (long.class.equals(type)) {
            return Long.class;
        } else if (double.class.equals(type)) {
            return Double.class;
        } else if (float.class.equals(type)) {
            return Float.class;
        } else if (boolean.class.equals(type)) {
            return Boolean.class;
        } else if (byte.class.equals(type)) {
            return Byte.class;
        } else if (char.class.equals(type)) {
            return Character.class;
        } else if (short.class.equals(type)) {
            return Short.class;
        } else {
            String iae = "Class " + type.getName() + "is not primitive.";
            throw new IllegalArgumentException(iae);
        }
    }

}
