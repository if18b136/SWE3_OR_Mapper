package ORM.Base;

import ORM.Annotations.Table;
import ORM.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity {
    private Class<?> entityClass;
    private Object object;
    private String tableName;
    private Field[] fields;
    private Field[] primaryFields;
    private Field[] internalFields;
    private Field[] externalFields;

    /**<h2>Entity Constructor from class</h2>
     * calls initEntity to initialize the Entity object
     * @param type Class from which the Entity will be from
     */
    public Entity(Class<?> type) {
        initEntity(type);
    }

    /**<h2>Entity Constructor from object</h2>
     * calls initEntity to initialize the Entity object
     * @param obj Object from which the Entity will be from
     */
    public Entity(Object obj) {
        this.object = obj;  //TODO if we don't want to save the whole object, we need to change the field annotation from Fields to methods of the classes, so we can access the method values instead of the whole object in Field.getValue()
        initEntity(obj.getClass());
    }

    private void initEntity(Class<?> type) {
        String tableName = MetaData.getAnnotationTableName(type);
        this.tableName = tableName == null || tableName.equals("") ? MetaData.buildTableName(type.getSimpleName()) : tableName;
        this.entityClass = type;

        List<Field> fieldsList = new ArrayList<>();
        List<Field> primaryFieldsList = new ArrayList<>();
        List<Field> internalFieldsList = new ArrayList<>();
        List<Field> externalFieldsList = new ArrayList<>();
        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for(java.lang.reflect.Field field : fields) {
            Field ormField = new Field(this, field);
            ormField.setFieldType(field.getDeclaringClass());
            fieldsList.add(ormField);
            if(ormField.isPrimary()) {
                primaryFieldsList.add(ormField);
            }
            if(ormField.isForeign()) {
                externalFieldsList.add(ormField);
            } else {
                internalFieldsList.add(ormField);
            }
        }
        this.fields = fieldsList.toArray(new Field[0]);
        this.primaryFields = primaryFieldsList.toArray(new Field[0]);
        this.internalFields = internalFieldsList.toArray(new Field[0]);
        this.externalFields = externalFieldsList.toArray(new Field[0]);
    }

    public String getTableName() { return tableName; }
    public Field[] getFields() { return fields; }
    public Class<?> getEntityClass() { return entityClass; }
    public Object getObject() { return object; }
    public Field[] getInternalFields() { return internalFields; }
    public Field[] getPrimaryFields() { return primaryFields; }
    public Field[] getExternalFields() { return externalFields; }

}
