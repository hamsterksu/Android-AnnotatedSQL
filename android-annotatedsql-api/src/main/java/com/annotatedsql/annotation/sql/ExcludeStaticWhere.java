package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by hamsterksu on 02.04.14.
 */
@Target(ElementType.FIELD)
public @interface ExcludeStaticWhere {
    String value();
}
