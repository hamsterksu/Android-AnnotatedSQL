package com.annotatedsql.annotation.provider;

public @interface Provider {

	String name();
	String schemaClass();
	String authority();
	String openHelperClass() default "";
	
	/**
	 * wrap methods bulkInsert and applyBatch to transaction 
	 */
	boolean supportTransaction() default true;

    /**
     * by default use REPLACE mode
     */
    String bulkInsertMode() default "REPLACE";

    /**
     * by default SQLiteDatabase.CONFLICT_REPLACE mode
     */
    int insertMode() default 5;
	
}
