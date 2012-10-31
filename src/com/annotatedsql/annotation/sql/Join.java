package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Join {
	String srcTable();
	String srcColumn();
	
	String destTable();
	String destColumn();
	
	Type type() default Type.INNER;
	
	public static enum Type{
		INNER, LEFT, RIGHT, CROSS
	}
}
