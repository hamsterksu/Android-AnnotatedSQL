package com.annotatedsql.annotation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Trigger {
    String name();

    Type[] type() default Type.ALL;

    When when() default When.AFTER;

    public static enum Type {INSERT, DELETE, UPDATE, ALL}

    public static enum When {BEFORE, AFTER}
}
