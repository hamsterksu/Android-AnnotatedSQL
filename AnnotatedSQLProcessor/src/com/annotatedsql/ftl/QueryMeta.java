package com.annotatedsql.ftl;

public class QueryMeta {

	private final String queryName;

	private final String sql;

	public QueryMeta(String queryName, String sql) {
		super();
		this.queryName = queryName;
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public String getQueryName() {
		return queryName;
	}
}
