package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by hamsterksu on 07.02.14.
 */
@Target(ElementType.TYPE)
public @interface StaticWhere {
    String column();
    String value();
}
