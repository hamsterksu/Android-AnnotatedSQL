package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
public @interface PrimaryKey {
	String[] columns() default {};
}
