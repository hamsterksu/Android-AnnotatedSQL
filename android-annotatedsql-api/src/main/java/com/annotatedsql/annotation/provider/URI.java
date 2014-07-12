package com.annotatedsql.annotation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface URI {
	
	String column() default "_id";
	Type type() default Type.DIR_AND_ITEM;
	String[] altNotify() default {};
	boolean onlyQuery() default false;
	
	public static enum Type{DIR, ITEM, DIR_AND_ITEM}
}
