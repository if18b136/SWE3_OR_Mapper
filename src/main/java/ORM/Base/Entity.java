package ORM.Base;

import ORM.MetaData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Entity {
    private Class<?> entityClass;
    private String tableName;
    private Field[] fields;

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


}
