package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Column {

    static final String EMPTY_DEF_VAL = "";

//    Type type();

    String defVal() default EMPTY_DEF_VAL;

    Class javaType();

    public static enum Type {
        INTEGER, REAL, TEXT, BLOB
    }
}
