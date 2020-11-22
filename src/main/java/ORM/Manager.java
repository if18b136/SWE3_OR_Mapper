package ORM;

import Database.DatabaseConnection;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Queries.CreateTableQuery;
import ORM.Queries.InsertQuery;
import ORM.Queries.SelectQuery;
import ORM.Queries.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
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

    private static HashMap<Class<?>, Entity> Entities = new HashMap<>();
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


    // TODO currently only useful for single entries
    public static <T> T executeSelect(Class<T> type, String query) {
        try{
            java.sql.Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
            ResultSet res = stmt.executeQuery(query);

            if(res.next()){
                //T t = type.newInstance();     // deprecated
                //T t = type.getDeclaredConstructor().newInstance();    //needed for loadIntoObject(resultQuery,Object)
//                if(t.getClass().equals(String.class)) {     //TODO delete this bad code and replace with something that makes sense.
//                    System.out.println("String needs separate handling.");
//                    t = (T) res.getString(1);
//                } else {
                    //loadIntoObject(res,t); // why not with return value to write to t?
                T newT = createObject(res,type);
//                }
                return newT;
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
        }
        return null;
    }

//    private static void loadIntoObject(ResultSet res, Object object) throws SQLException, IllegalAccessException, NoSuchFieldException {
//        Class<?> objectClass = object.getClass();
//
//        if(!objectClass.isPrimitive() && !objectClass.equals(String.class)) {   //TODO make exclusion of non-custom objects more generic than this (date won't work either)
//            for(java.lang.reflect.Field field : objectClass.getDeclaredFields()) {
//                field.setAccessible(true);
//                Object value = res.getObject(field.getName());
//                Class<?> type = field.getType();
//                if(type.isPrimitive()) {    //TODO check if own class does the same
//                    Class<?> boxed = boxPrimitiveClass(type);
//                    value = boxed.cast(value);
//                } else if(value.getClass() == Date.class) {  // TODO convert externally?
//                    value = ((Date) value).toLocalDate();
//                }
//                System.out.println("--- loadIntoObject: " + value.toString());
//                field.set(object, value);
//            }
//        } else {
//            // This is the way to get to more than one columns, currently not needed
//            for(int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
//                //System.out.println("    " + res.getMetaData().getColumnName(i));
//                //java.lang.reflect.Field field = objectClass.getField(res.getMetaData().getColumnName(i));
//                for(java.lang.reflect.Field field : objectClass.getDeclaredFields()) {
//                    field.setAccessible(true);
//                    System.out.println("    " + field.getName());
//                }
//                java.lang.reflect.Field field = objectClass.getField("value");
//                field.setAccessible(true);
//                System.out.println("        " + field.getName());
//
////                field.setAccessible(true);
////                Object value = res.getObject(objectClass.getName());
////                field.set(object,value);
//            }
//        }
//    }

    public static <T> T getObject(Class<T> t, Object... pks) {
            return (T) createObject(t,pks);
    }

    //TODO heavy refactoring to prevent sql injection (don't directly insert the pks as values) and overall better readability
    public static Object createObject(Class clazz, Object... pks)  {
        try {
            System.out.println(clazz.getName());
            Entity entity = Entities.get(clazz);    // getEntity needs an object as argument and so does not work here

            SelectQuery select = new SelectQuery();
            select.addTables(entity.getTableName());
            List<String> targets = new ArrayList<>();
            for(Field field : entity.getInternalFields()) {
                targets.add(field.getColumnName());
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

    private static <T> T createObject(ResultSet res, Class<T> type) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if(!type.isPrimitive() && /*!type.equals(String.class)*/ res.getMetaData().getColumnCount() > 1) {   //TODO make exclusion of non-custom objects more generic than this (date won't work either)
            T t = type.getDeclaredConstructor().newInstance();
            System.out.println(res.getMetaData().getColumnCount());
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
            System.out.println(new CreateTableQuery().buildQuery(entity));
            PreparedStatement initStmt = db.prepareStatement(new CreateTableQuery().buildQuery(entity));
            initStmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//    //TODO refactor into CREATE TABLE query class
//    public static void createTableFromObject(Object object) {
//        try {
//            List<String> data = MetaData.getAnnotationColumnData(object.getClass());
//            StringBuilder initTable = new StringBuilder();
//            initTable.append("CREATE TABLE ").append(MetaData.getAnnotationTableName(object.getClass())).append(" (");
//            for (String sql : data) {
//                initTable.append(sql).append(" ");
//            }
//            initTable.append(");");
//            managerLogger.info("createTableFromObject() - SQL String: " + initTable.toString());
//            PreparedStatement initStmt = db.prepareStatement(initTable.toString());
//            initStmt.execute();
//        }catch (SQLException sql) {
//            managerLogger.error(sql);
//        }
//    }

    public static void save(Object object) {
        try{
            Entity entity = getEntity(object);
            PreparedStatement insertStmt = db.prepareStatement(new InsertQuery().buildQuery(object,entity));
            insertStmt.executeUpdate();
            managerLogger.info("A new Entry has been inserted into " + entity.getTableName() + ".");
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    public static void saveOrUpdate(Object object) {
        try{
            Entity entity = new Entity(object); // TODO change to object.getEntity method?
            InsertQuery insertQuery = new InsertQuery();
            insertQuery.enableUpsert();
            String insertString = insertQuery.buildQuery(object, entity);
            managerLogger.info("Upsert: " + insertString);
            PreparedStatement insertStmt = db.prepareStatement(insertString);
            insertStmt.executeUpdate();
            managerLogger.info("A new Entry has been inserted into " + entity.getTableName() + " or the entry has been updated.");
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }
}
