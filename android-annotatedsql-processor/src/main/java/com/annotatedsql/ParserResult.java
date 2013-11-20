package com.annotatedsql;

public class ParserResult {

	private final String sql;

	public ParserResult(String sql) {
		super();
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
}
