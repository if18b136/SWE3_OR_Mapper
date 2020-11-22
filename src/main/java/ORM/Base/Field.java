package ORM.Base;

import ORM.Manager;
import ORM.MetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

/**<h1>Class </h1>
 *
 */
public class Field {
    static final Logger fieldLogger = LogManager.getLogger("Field Logger");

    private Class _fieldType;
    private Entity entity;
    private java.lang.reflect.Field field;
    private String columnName;
    private boolean primary;
    private boolean autoIncrement;
    private boolean foreign;

    private String foreignTable;
    private String foreignColumn;

    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
        field.setAccessible(true);
        this.columnName = field.getName();
        this.primary = MetaData.isPrimary(field);
        this.autoIncrement = MetaData.isAutoIncrement(field);
        this.foreign = MetaData.isForeign(field);
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

//  _set.invoke(
//      obj, World._createObject(
//          _fieldType, new Object[] {
//              World.
//              __getEntity(_fieldType).
//              getPrimaryKeys()[0].
//              toFieldType(value)
//          }, objects
//      )
//  );

    // oldest implementation that relied on a saved object in the entity which was a bad idea
//    public Object getValue() {
//        try {
//            return field.get(this.entity.getObject());
//        } catch (IllegalAccessException iae) {
//            fieldLogger.error(iae);
//        }
//       return null; // won't ever be reached
//    }

    // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
    // Alternative is to call for the getter Methods but that would need a little bit more refactoring
    public Object getValue(Object object) {
        try {
            Object value = this.field.get(object);
            if(value != null) {
                return value;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getColumnName() { return this.columnName; }

    public boolean isPrimary() { return this.primary; }
    public boolean isAutoIncrement() { return this.autoIncrement; }
    public boolean isForeign() { return this.foreign; }

    public boolean isMtoN () {
        return this.foreignTable.isEmpty();
    }

    //TODO add sqlTypeParser Methods

    private Method get;
    private Method set;
    private java.lang.reflect.Field setF;

//    public void setValue(Object obj, Object value) {
//        if(this.foreign) {
////            if(this.isExternal) {} else {
//            try {
//                set.invoke(obj, Manager.createObject(_fieldType, new Object[] {Manager.getEntity(_fieldType).getPrimaryFields()[0].toFieldType(value) }));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
////        }
//        }
//    }
//
//    public Object getValue(Object obj) {
//        try {
//            return get.invoke(obj);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//
    /** Gets the column type.
     * @return Type. */
    public Class getFieldType()
    {
        return _fieldType;
    }

    /** Sets the column type.
     * @param value Type. */
    public void setFieldType(Class value)
    {
        _fieldType = value;
    }
//
//
//    public Object toFieldType(Object obj)
//    {
//        if(_fieldType.equals(boolean.class))
//        {
//            if(obj.getClass().equals(int.class))   { return (((int) obj)   != 0); }
//            if(obj.getClass().equals(short.class)) { return (((short) obj) != 0); }
//            if(obj.getClass().equals(long.class))  { return (((long) obj)  != 0); }
//        }
//
//        if(_fieldType.equals(short.class)) { return (short) obj; }
//        if(_fieldType.equals(int.class))   { return (int)   obj; }
//        if(_fieldType.equals(long.class))  { return (long)  obj; }
//
//        if(_fieldType.isEnum())
//        {
//            if(obj instanceof String) { return Enum.valueOf(_fieldType, (String) obj); }
//            return _fieldType.getEnumConstants()[(int) obj];
//        }
//
//        if(_fieldType.equals(java.sql.Date.class)) { return (LocalDate) obj; }
////        if(_fieldType.equals(Calendar.class))
////        {
////            Calendar rval = Calendar.getInstance();
////            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////            try
////            {
////                rval.setTime(f.parse(obj.toString()));
////                return rval;
////            }
////            catch (Exception ex) {}
////        }
//
//        return obj;
//    }
}