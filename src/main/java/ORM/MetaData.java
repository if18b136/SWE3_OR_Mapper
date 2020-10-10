package ORM;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetaData {

    //TODO - Question: is it smart to declare a class for fieldData key+value?
    public class fieldData {
        public fieldData(String type, String value) {this.type=type; this.value=value;}
        public String type;
        public String value;

    }

    public List<fieldData> fields (Object obj) {
       Field[] fieldArray = obj.getClass().getDeclaredFields();
       List<fieldData> fieldList = new ArrayList<>();
       for(Field field : fieldArray) {
           fieldList.add(new fieldData(field.getAnnotatedType().toString(),field.getName()));
       }
       return fieldList;
    }
}
