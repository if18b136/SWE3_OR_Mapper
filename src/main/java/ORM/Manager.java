package ORM;

import Database.DatabaseConnection;
import ORM.Annotations.Column;
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
import java.util.Arrays;
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
        Class<?> clazz = ((obj instanceof Class) ? (Class<?>) obj : obj.getClass());
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
            select.setEntity(entity);   // needed for primary keys if a join will be called.
            select.addTables(entity.getTableName());
            List<String> targets = new ArrayList<>();
            for(Field field : entity.getInternalFields()) {
                targets.add(field.getColumnName());
            }

            addSuperClassTargets(select,targets,entity);

            select.addTargets(targets.toArray(new String[0]));
            int i = 0;
            for(Field field : entity.getPrimaryFields()) {
                select.addCondition(field.getColumnName(),pks[i]);
            }

            select.buildQuery();
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

//    //TODO - 24.11.2020 - Compare with other method for single return value handling from database
//    private static <T> T createObject(ResultSet res, Class<T> type) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
//        if(!type.isPrimitive() && /*!type.equals(String.class)*/ res.getMetaData().getColumnCount() > 1) {   //TODO make exclusion of non-custom objects more generic than this (date won't work either)
//            T t = type.getDeclaredConstructor().newInstance();
//            for(java.lang.reflect.Field field : type.getDeclaredFields()) {
//                field.setAccessible(true);
//                if ( field.getAnnotation(Column.class) != null && !field.getAnnotation(Column.class).ignore()) {    //should now ignore fields
//                    Object value = res.getObject(field.getName());
//                    Class<?> clazz = field.getType();
//                    if(clazz.isPrimitive()) {    //TODO check if own class does the same
//                        Class<?> boxed = boxPrimitiveClass(clazz);
//                        value = boxed.cast(value);
//                    } else if(value.getClass() == Date.class) {  // TODO convert externally?
//                        value = ((Date) value).toLocalDate();
//                    }
//                    field.set(t, value);
//                }
//            }
//            return t;
//        } else {
//            //TODO handle something like "select 2 strings" here -> would need an array or list as return.
//            if(res.getMetaData().getColumnCount() > 1) {
//                for(int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
//                    // do something multiple times here.
//                }
//                return null;
//            } else {
//                return (T) res.getObject(1);
//            }
//        }
//    }

    private static <T> T createObject(ResultSet res, Class<T> type) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if(!type.isPrimitive() && res.getMetaData().getColumnCount() > 1) {
            T t = type.getDeclaredConstructor().newInstance();

            Entity entity = getEntity(type);

            for(Field field : getEntity(type).getPrimaryFields()) {
                field.setValue(t, res.getObject(field.getColumnName()));
            }

            if(entity.getSuperClass() != null && !entity.getSuperClass().equals(Object.class)) {
                Entity superEntity = getEntity(entity.getSuperClass());
                for (Field field : superEntity.getFields()) {
                    setFieldValue(res, t, field);
                }
            }

            for(Field field : getEntity(type).getFields()) {
                setFieldValue(res, t, field);
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

    private static <T> void setFieldValue(ResultSet res, T t, Field field) throws SQLException {
        if(field.isPrimary() && !field.isForeign()) {
            field.setValue(t, res.getObject(field.getColumnName()));
        } else {
            Object value;
            if( field.isForeign()) {
                value = MetaData.toFieldType(field,res.getObject(field.getEntity().getPrimaryFields()[0].getColumnName()));
            } else {
                value = MetaData.toFieldType(field,res.getObject(field.getColumnName()));
            }
            field.setValue(t, value);
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
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    /** <h3>Inner Method addSuperClassTargets</h3>
     * Helper Method for parent class variable fetching. Calls parent class entities recursively and adds their inner fields to target list.
     * @param select    the current selectQuery
     * @param targets   the selectQuery target field list
     * @param entity    the current Entity
     */
    private static void addSuperClassTargets(SelectQuery select, List<String> targets, Entity entity) {
        if(entity.getSuperClass() != null && !entity.getSuperClass().equals(Object.class)) {
            System.out.println("Super class Name: " + entity.getSuperClass().getName());
            Entity superEntity = Entities.get(entity.getSuperClass());
            System.out.println("Super entity table name: "  + superEntity.getTableName());
            select.addTables(superEntity.getTableName());
            for (Field field : superEntity.getInternalFields()) {
                String foreignColumn = superEntity.getTableName()+"."+field.getColumnName();    // easier than trying to get the correct table for each column in the selectQuery Builder
                targets.add(foreignColumn);
            }
            if(Entities.get(entity.getSuperClass()) != null) {  // unfortunately getEntities ALWAYS returns a class (even if it needs to create a java.lang.class Class out of thin air without any help)
                addSuperClassTargets(select, targets, Entities.get(entity.getSuperClass()));
            }
        }
        else {
            System.out.println("Base entity in addSuperClassTargets");
        }
    }
}
