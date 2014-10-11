package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Unique {

    ConflictType type() default ConflictType.REPLACE;

    enum ConflictType {ROLLBACK, ABORT, FAIL, IGNORE, REPLACE}
}
