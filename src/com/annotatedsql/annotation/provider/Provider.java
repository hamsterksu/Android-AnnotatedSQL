package com.annotatedsql.annotation.provider;

public @interface Provider {

	String name();
	String schemaClass();
	String authority();
	String openHelperClass() default "";
	
	/**
	 * wrap methods bulkInsert and applyBatch to transaction 
	 * @return
	 */
	boolean supportTransaction() default true; 
	
}
