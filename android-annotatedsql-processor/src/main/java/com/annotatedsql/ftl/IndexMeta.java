package com.annotatedsql.ftl;

public class IndexMeta {

	private final String indexName;

	private final String sql;

	public IndexMeta(String indexName, String sql) {
		super();
		this.indexName = indexName;
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
}
