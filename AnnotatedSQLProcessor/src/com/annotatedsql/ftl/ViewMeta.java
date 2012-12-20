package com.annotatedsql.ftl;

public class ViewMeta {

	private final String viewName;

	private final String sql;

	public ViewMeta(String viewName, String sql) {
		super();
		this.viewName = viewName;
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public String getViewName() {
		return viewName;
	}
	
}
