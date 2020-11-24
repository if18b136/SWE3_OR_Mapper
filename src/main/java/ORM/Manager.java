package ORM;

import Database.DatabaseConnection;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Queries.CreateTableQuery;
import ORM.Queries.InsertQuery;
import ORM.Queries.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Manager class operates as a single Instance for legal checks and other operational tests
 */
public final class Manager {
    final static Logger managerLogger = LogManager.getLogger("Manager");
    private static Connection db;

    // shortens db call
    static {
        try {
            db = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    protected static HashMap<Class<?>, Entity> Entities = new HashMap<>();
    private static List<Object> objectCache = new ArrayList<>();

    private Manager() {}

   // this queries an entity of the object class for further use
   public static Entity getEntity(Object obj) {
        Class<?> clazz = obj.getClass();
        if(Entities.containsKey(clazz)) {
            return Entities.get(clazz);
        }
        Entity ent = new Entity(clazz);
        Entities.put(clazz,ent);
        return ent;
   }

   //TODO get better solution for type check in table creation and value inserting
   public static Entity isCached(Class<?> clazz) {
       if(Entities.containsKey(clazz)) {
           return Entities.get(clazz);
       }
       return null;
   }

   //TODO: write getCachedObject method
    public static Object getCachedObject(Class<?> type, ResultSet res){
        return null;
    }

    private static boolean tableExists(String tableName) throws SQLException {
        PreparedStatement table = db.prepareStatement("show tables like ?;");
        table.setString(1,tableName);
        ResultSet res = table.executeQuery();
        return res.next();
    }

    public static <T> T getObject(Class<T> t, Object... pks) {
            return (T) createObject(t,pks);
    }

    //TODO heavy refactoring to prevent sql injection (don't directly insert the pks as values) and overall better readability
    public static Object createObject(Class<?> clazz, Object... pks)  {
        try {
            Entity entity = Entities.get(clazz);    // getEntity needs an object as argument and so does not work here

            SelectQuery select = new SelectQuery();
            select.addTables(entity.getTableName());
            List<String> targets = new ArrayList<>();
            for(Field field : entity.getInternalFields()) {
                targets.add(field.getColumnName());
            }
            //TODO superclass targets
            if(entity.getSuperClass() != null) {
                Entity superEntity = Entities.get(entity.getSuperClass());
                select.addTables(superEntity.getTableName());
                for(Field field : superEntity.getInternalFields()) {
                    targets.add(field.getColumnName());
                }
            }

            select.addTargets(targets.toArray(new String[0]));
            int i = 0;
            for(Field field : entity.getPrimaryFields()) {
                select.addCondition(field.getColumnName(),pks[i]);
            }
            select.buildQuery();
            System.out.println(select.getQuery());
            PreparedStatement stmt = db.prepareStatement(select.getQuery());
            ResultSet res = stmt.executeQuery();
            Object rval = null;
            try{

                if (res.next()){
                    rval = createObject(res, clazz);
                }
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
            res.close();

            if(rval == null) { throw new SQLException("No data."); }
            return rval;
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return null;
    }

    //TODO - 24.11.2020 - Compare with other method for single return value handling from database
    private static <T> T createObject(ResultSet res, Class<T> type) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if(!type.isPrimitive() && /*!type.equals(String.class)*/ res.getMetaData().getColumnCount() > 1) {   //TODO make exclusion of non-custom objects more generic than this (date won't work either)
            T t = type.getDeclaredConstructor().newInstance();
            for(java.lang.reflect.Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = res.getObject(field.getName());
                Class<?> clazz = field.getType();
                if(clazz.isPrimitive()) {    //TODO check if own class does the same
                    Class<?> boxed = boxPrimitiveClass(clazz);
                    value = boxed.cast(value);
                } else if(value.getClass() == Date.class) {  // TODO convert externally?
                    value = ((Date) value).toLocalDate();
                }
                field.set(t, value);
            }
            return t;
        } else {
            //TODO handle something like "select 2 strings" here -> would need an array or list as return.
            if(res.getMetaData().getColumnCount() > 1) {
                for(int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                    // do something multiple times here.
                }
                return null;
            } else {
                return (T) res.getObject(1);
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

    public static void createTable(Object object) {
        try {
            Entity entity = getEntity(object);
            managerLogger.info(new CreateTableQuery().buildQuery(entity));
            PreparedStatement initStmt = db.prepareStatement(new CreateTableQuery().buildQuery(entity));
            initStmt.execute();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    public static void save(Object object) {
        try{
            Entity entity = getEntity(object);
            InsertQuery insertQuery = new InsertQuery();
            insertQuery.buildQuery(object, entity);
            managerLogger.info(insertQuery.getQuery());
            PreparedStatement insertStmt = insertQuery.getStmt();
            insertStmt.executeUpdate();
            managerLogger.info("A new Entry has been inserted into " + entity.getTableName() + ".");
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    public static void saveOrUpdate(Object object) {
        try{
            Entity entity = getEntity(object);
            InsertQuery insertQuery = new InsertQuery();
            insertQuery.enableUpsert();
            insertQuery.buildQuery(object, entity);
            managerLogger.info(insertQuery.getQuery());
            PreparedStatement insertStmt = insertQuery.getStmt();
            insertStmt.executeUpdate();
            managerLogger.info("A new Entry has been inserted into " + entity.getTableName() + " or the entry has been updated.");
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }
}
