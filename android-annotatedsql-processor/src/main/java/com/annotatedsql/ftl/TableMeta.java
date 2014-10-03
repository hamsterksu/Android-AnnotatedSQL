package com.annotatedsql.ftl;


import org.apache.commons.lang.WordUtils;

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

    public String getTableNameCamelCase() {
        return WordUtils.capitalizeFully(tableName, new char[]{'_'}).replaceAll("_", "");
    }

}
