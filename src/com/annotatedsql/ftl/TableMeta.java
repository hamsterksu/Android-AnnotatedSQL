package com.annotatedsql.ftl;


public class TableMeta {

	private final String tableName;

	private final String sql;

	public TableMeta(String tableName, String sql) {
		super();
		this.tableName = tableName;
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public String getTableName() {
		return tableName;
	}

}
