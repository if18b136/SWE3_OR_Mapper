package ORM;

import ORM.Annotations.Column;
import ORM.Base.Field;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class Utility {
    private Utility() {}

    // creates a list of all indexes of autoincrement fields
    public static List<Integer> hasPK(Field[] fields) {
        List<Integer> fieldList = new ArrayList<>();
        for(int i = 0 ; i < fields.length ; i++) {
            if(fields[i].isPrimary() && fields[i].isAutoIncrement()) {
                fieldList.add(i);
            }
        }
        return fieldList;
    }

    // creates a list with all indexes of non autoincrement fields
    public static List<Integer> noPKFields(Field[] fields) {
        List<Integer> pkList = hasPK(fields);
        List<Integer> fieldList = new ArrayList<>();
        for (int i = 0 ; i < fields.length ; i++ ) {
            if(!pkList.contains(i)) {
                fieldList.add(i);
            }
        }
        System.out.println(fieldList);
        return fieldList;
    }
}
