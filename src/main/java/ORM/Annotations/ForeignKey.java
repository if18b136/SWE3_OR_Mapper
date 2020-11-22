package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    String table() default "";          //table which the fk is referencing
    String column() default "";         //column which the fk is referencing
    String foreignColumn() default "";  //m:n column which also references the fk
}
