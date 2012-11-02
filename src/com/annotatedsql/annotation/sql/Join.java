package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Join {
	String joinTable();
	String joinColumn();
	
	String onTable();
	String onColumn();
	
	Type type() default Type.INNER;
	
	public static enum Type{
		INNER, LEFT, RIGHT, CROSS
	}
}
