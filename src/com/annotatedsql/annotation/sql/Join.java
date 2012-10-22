package com.annotatedsql.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Join {
	String srcTable();
	String srcColumn();
	
	String destTable();
	String destColumn();
}
