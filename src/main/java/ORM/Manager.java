package ORM;

import Database.DatabaseConnection;
import Entities.Course;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Queries.CreateTableQuery;
import ORM.Queries.InsertQuery;
import ORM.Queries.Query;
import ORM.Queries.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Manager class operates as a single Instance for legal checks and other operational tests
 */
public final class Manager {
    /**
     * Manager Logger
     */
    final static Logger managerLogger = LogManager.getLogger("Manager");
    /**
     * Private boolean for cache enabling and disabling.
     */
    private static Boolean caching = false;
    /**
     * HashMap with cached entities objects, classified by their respective class object
     */
    protected static HashMap<Class<?>, Entity> entitiesCache = new HashMap<>();
    /**
     * HashMap with caches for each custom entity class.
     */
    private static final HashMap<Class<?>, Cache> objectCache = new HashMap<>();
    //TODO
    private static ArrayList<Entity> tableCache = new ArrayList<>();
    /**
     * Database connection as static variable to make database calls faster.
     */
    private static Connection db;

    // Exception handling for static database connection field.
    static {
        try {
            db = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    /**
     * Empty private constructor because of static non-instantiable class.
     */
    private Manager() {}

    /**
     * Searches for already created class entity.
     * If nothing found, creates new entry in Entities hashMap.
     *
     * @param obj   the Object which class (or itself if object is a class already) will be checked for an existing entry in Entities hashMap.
     * @return  An entity of the input object.
     */
   public static Entity getEntity(Object obj) {
        Class<?> type = ((obj instanceof Class) ? (Class<?>) obj : obj.getClass());
        if(entitiesCache.containsKey(type)) {
            return entitiesCache.get(type);
        }
        Entity ent = new Entity(type);
        entitiesCache.put(type,ent);
        return ent;
   }

    /**
     * Inserts a new entry into the cache.
     * If no cache entry for the specific custom entity already exists, will also create one.
     *
     * @param object    Cached entity object.
     */
   private static void insertCache (Object object) {
       if(caching) {
           if(!objectCache.containsKey(object.getClass())) {
               objectCache.put(object.getClass(),new Cache());
           }
           objectCache.get(object.getClass()).setEntry(getEntity(object).getPrimaryFields()[0].getValue(object),object);
       }
   }

   public static void enableCaching(Boolean bool) {
       caching = bool;
   }

   public static void depleteCache() {
       entitiesCache.clear();
       objectCache.clear();
   }

    /**
     * Checks if an entity of a certain class is stored in the Entities hashMap.
     *
     * @param type     the class which will be searched for.
     * @return          An existing entity or null if no entity found.
     */
   public static Entity getEntityIfExists(Class<?> type) {
       if(entitiesCache.containsKey(type)) {
           return entitiesCache.get(type);
       }
       managerLogger.info("Search for logged Entity " + type + " unsuccessful - no Entity for that class cached. Null returned.");
       return null;
   }

    /**
     * Checks if a table with the certain name exists in the database.
     *
     * @param tableName Database table name.
     * @return  True if exists, else false.
     * @throws SQLException if anything went wrong with the database connection.
     */
    private static boolean tableExists(String tableName) throws SQLException {
        PreparedStatement table = db.prepareStatement("show tables like ?;");
        table.setString(1,tableName);
        ResultSet res = table.executeQuery();
        return res.next();
    }

    /**
     * Alternative to having to cast the generic object to a certain class.
     * Made into an extra method because of unchecked cast warning so it is up to the user which implementation they want to use.
     *
     * @param type  the class, the object will be cast to
     * @param pks   the primary key(s) as object(s)
     * @param <T>   generic wildcard
     * @return      a object cast to the wildcard class
     */
    public static <T> T get(Class<T> type, Object... pks) {
       return (T) getObject(type,pks);
    }

    /**
     * Either finds the object in one of the caches or calls the create function below the make a new one.
     *
     * @param type  the class of the wanted object
     * @param pks   the identifier primary key(s) object(s)
     * @return      the object either from the cache or from the database.
     */
    public static Object getObject(Class<?> type, Object... pks) {
        if(objectCache.containsKey(type)) {
            return objectCache.get(type).contains(pks[0]) ? objectCache.get(type).getEntry(pks[0]) : createObject(type,pks);
        }
        return createObject(type,pks);
    }


    /**
     * Will be called if object is not cached already.
     * Creates a new SelectQuery with the given arguments.
     * Makes a database call and inserts the returning values into a new object of the input class.
     *
     * @param type  the class of which a new object will be created.
     * @param pks   the identifier primary key(s) object(s) from which the database select query will be constructed.
     * @return      A new object of type.Class with the stored database call values.
     */
    public static Object createObject(Class<?> type, Object... pks)  {
        try {
            Entity entity = entitiesCache.get(type);    // getEntity needs an object as argument and so does not work here

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
            Object newObject = null;
            try{

                if (res.next()){
                    newObject = createObject(res, type);
                }
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
            res.close();

            if(newObject == null) { throw new SQLException("No data."); }

            // check if there is already a cache for the class, create a new one before inserting if not
            insertCache(newObject);

            return newObject;
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return null;
    }

    /**
     * Taken out of other createObject method for visibility.
     *
     * @param res   Database call ResultSet
     * @param type  class for new object
     * @param <T>   wildcard class type
     * @return      new object of type.Class with inserted values from ResultSet
     * @throws SQLException     if anything went wrong with either the connection or the ResultSet
     * @throws IllegalAccessException   if reflection field access is not set to true
     * @throws NoSuchMethodException    if certain method is not found within the class
     * @throws InvocationTargetException    if getting a new object from type fails
     * @throws InstantiationException       if instancing a new object from type fails
     */
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
                    System.out.println("I love you.");
                }
                return null;
            } else {
                return (T) res.getObject(1);
            }
        }
    }

    /**
     * Function needed in two different places of T createObject methods so it got extracted to own method.
     *
     * @param res   the ResultSet from getObject.
     * @param t     wildcard.
     * @param field Field where a new value will be set.
     * @param <T>   to generify method.
     * @throws SQLException if ResultSet does not contain value of object with field column name.
     */
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

    //TODO check advantage of using boxed java classes
/*
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
*/

    /**
     * Can be used to create a new table in the database from a certain object.
     * Update 15.12.2020: Now also creates superclass tables if object is subclass and has not already been created
     *
     * @param object    the object from which fields a createTable query will be created and executed.
     */
    public static void createTable(Object object) {
        try {
            Class<?> type = ((object instanceof Class) ? (Class<?>) object : object.getClass());
            Entity entity = getEntity(type);
            if(!tableCache.contains(entity)) {
                if(entity.getSuperClass() != null && !entity.getSuperClass().equals(Object.class)) {
                    createTable(entity.getSuperClass());
                }
                String tableQuery = new CreateTableQuery().buildQuery(entity);
                PreparedStatement initStmt = db.prepareStatement(tableQuery);
                managerLogger.info(tableQuery);
                initStmt.execute();
                tableCache.add(entity);

                // m:n
                if(entity.getManyFields().length > 0) {
                    for (Field field : entity.getManyFields()) {
                        // check if corresponding table already exists
                        Class<?> corrClass = MetaData.getManyClass(field.getField());
                        if(corrClass != null) {
                            if(tableExists(MetaData.getAnnotationTableName(corrClass))) { // create m:n table query from this and every other class involved.
                                String manyTableQuery = new CreateTableQuery().buildManyQuery(field.getForeignTable(), entity, getEntity(MetaData.getManyClass(field.getField())));
                                initStmt = db.prepareStatement(manyTableQuery);
                                managerLogger.info(manyTableQuery);
                                initStmt.execute();
                            }
                            // if not ignore - user needs to create it before being able to insert anything here
                        }
                    }
                }
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    /**
     * Insert query.
     * Inserts a new object into the database
     *
     * @param object    the object of which it's data will be inserted into the database.
     */
    public static void save(Object object) {
        try{
            Entity entity = getEntity(object);
            InsertQuery insertQuery = new InsertQuery();
            insertQuery.buildQuery(object, entity);
            managerLogger.info(insertQuery.getQuery());
            PreparedStatement insertStmt = insertQuery.getStmt();   //TODO - adapt so it becomes temp too like buildManyQuery()
            insertStmt.executeUpdate();
            insertCache(object);

            if (entity.getManyFields().length > 0) {
                for (Field field : entity.getManyFields()) {
                    Class<?> corrClass = MetaData.getManyClass(field.getField());
                    if(corrClass != null) {
                        if (tableExists(MetaData.getAnnotationTableName(corrClass)) && field.getValue(object) != null) {
                            managerLogger.info(insertQuery.buildManyQuery(object, field.getValue(object)));     // more direct version with inner execute and no perma-stored query.
                        }
                    }
                }
            }
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    /**
     * Upsert query.
     * Inserts a new object into the database or updates a database entry if already in database.
     *
     * @param object   the object of which it's data will be upserted in the database.
     */
    public static void saveOrUpdate(Object object) {
        try{
            Entity entity = getEntity(object);
            InsertQuery insertQuery = new InsertQuery();
            insertQuery.enableUpsert();
            insertQuery.buildQuery(object, entity);
            managerLogger.info(insertQuery.getQuery());
            PreparedStatement insertStmt = insertQuery.getStmt();
            insertStmt.executeUpdate();
            insertCache(object);
            if (entity.getManyFields().length > 0) {
                for (Field field : entity.getManyFields()) {
                    Class<?> corrClass = MetaData.getManyClass(field.getField());
                    if(corrClass != null) {
                        if (tableExists(MetaData.getAnnotationTableName(corrClass))) {
                            managerLogger.info(insertQuery.buildManyQuery(object, field.getEntity()), getEntity(MetaData.getManyClass(field.getField())));
                        }
                    }
                }
            }
        } catch (SQLException sql) {
            managerLogger.error(sql);
        }
    }

    /**
     * Helper Method for parent class variable fetching. Calls parent class entities recursively and adds their inner fields to target list.
     *
     * @param select    the current selectQuery
     * @param targets   the selectQuery target field list
     * @param entity    the current Entity
     */
    private static void addSuperClassTargets(SelectQuery select, List<String> targets, Entity entity) {
        if(entity.getSuperClass() != null && !entity.getSuperClass().equals(Object.class)) {
//            System.out.println("Super class Name: " + entity.getSuperClass().getName());
            Entity superEntity = entitiesCache.get(entity.getSuperClass());
//            System.out.println("Super entity table name: "  + superEntity.getTableName());
            select.addTables(superEntity.getTableName());
            for (Field field : superEntity.getInternalFields()) {
                String foreignColumn = superEntity.getTableName()+"."+field.getColumnName();    // easier than trying to get the correct table for each column in the selectQuery Builder
                targets.add(foreignColumn);
            }
            if(entitiesCache.get(entity.getSuperClass()) != null) {  // unfortunately getEntities ALWAYS returns a class (even if it needs to create a java.lang.class Class out of thin air without any help)
                addSuperClassTargets(select, targets, entitiesCache.get(entity.getSuperClass()));
            }
        }
//        else {
//            System.out.println("Base entity in addSuperClassTargets");
//        }
    }
}
