package com.annotatedsql.annotation.provider;

public @interface Provider {

	String name();
	String schemaClass();
	String authority();
	String openHelperClass() default "";
	
}
