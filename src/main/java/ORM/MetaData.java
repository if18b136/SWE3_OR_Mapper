package ORM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetaData {
    static final Logger metaDataLogger = LogManager.getLogger("MetaData Logger");

    //TODO - Question: is it smart to declare a class for fieldData key+value?
    public class fieldData {
        public fieldData(String type, String name, String value) {this.type=type; this.name=name; this.value=value;}
        public String type;
        public String name;
        public String value;
    }

    public List<fieldData> fields (Object obj) throws IllegalAccessException {
        Field[] fieldArray = obj.getClass().getDeclaredFields(); //get all fields as single Array
        List<fieldData> fieldList = new ArrayList<>();           // list from helper class
        for(Field field : fieldArray) {
            field.setAccessible(true);
            fieldList.add(new fieldData(field.getAnnotatedType().toString(), field.getName(), field.get(obj).toString()));
        }
        return fieldList;
    }
}
