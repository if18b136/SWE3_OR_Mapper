package ORM.Base;

import ORM.MetaData;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private Class<?> entityClass;
    private Object object;
    private String tableName;
    private Field[] fields;
    private Field[] internalFields;
    private Field[] externalFields;

    // construct Entity from test class - will not have any values in fields
    public Entity(Class<?> type) {
        this.tableName = MetaData.getAnnotationTableName(type); // TODO default to className?
        this.entityClass = type;

        List<Field> fieldsList = new ArrayList<>();
        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for(java.lang.reflect.Field field : fields) {
            Field ormField = new Field(this, field);
            fieldsList.add(ormField);
        }
        this.fields = fieldsList.toArray(new Field[0]);
    }

    // construct Entity from test class object
    public Entity(Object obj) {
        this.tableName = MetaData.getAnnotationTableName(obj.getClass()); // TODO default to className?
        this.entityClass = obj.getClass();
        this.object = obj;

        List<Field> fieldsList = new ArrayList<>();
        List<Field> internalFieldsList = new ArrayList<>();
        List<Field> externalFieldsList = new ArrayList<>();
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
        for(java.lang.reflect.Field field : fields) {
            Field ormField = new Field(this, field);
            fieldsList.add(ormField);
            if(ormField.isForeign()) {
                externalFieldsList.add(ormField);
            } else {
                internalFieldsList.add(ormField);
            }
        }
        this.fields = fieldsList.toArray(new Field[0]);
        this.internalFields = internalFieldsList.toArray(new Field[0]);
        this.externalFields = externalFieldsList.toArray(new Field[0]);

    }

    public String getTableName() { return tableName; }
    public Field[] getFields() { return fields; }
    public Class<?> getEntityClass() { return entityClass; }
    public Object getObject() { return object; }

}
