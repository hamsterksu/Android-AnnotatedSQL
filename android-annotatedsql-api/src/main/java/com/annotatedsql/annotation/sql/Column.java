package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Column {

    static final String EMPTY_DEF_VAL = "";

    Type type() default Type.TEXT;

    String defVal() default EMPTY_DEF_VAL;

    public static enum Type {
        INTEGER, REAL, TEXT, BLOB
    }
}
