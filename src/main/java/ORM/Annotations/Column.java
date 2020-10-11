package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    boolean primary() default false;
    boolean autoIncrement() default false;
    boolean unique() default false;
    boolean nullable() default true;
    int length() default 255;
}
