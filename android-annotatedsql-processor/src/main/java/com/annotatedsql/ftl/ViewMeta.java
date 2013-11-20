package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.List;

import com.annotatedsql.util.TextUtils;

public class ViewMeta {

	private final String viewClassName;
	
	private final String viewName;

	private String sql;
	
	private final List<ViewTableInfo> tables = new ArrayList<ViewTableInfo>();
	
	public ViewMeta(String viewClassName, String viewName) {
		super();
		this.viewName = viewName;
		this.viewClassName = viewClassName;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public String getViewName() {
		return viewName;
	}
	
	public void addTable(ViewTableInfo table, boolean toHead) {
		if(toHead && tables.size() > 0){
			tables.add(0, table);
			return;
		}
		tables.add(table);
	}
	
	public List<ViewTableInfo> getTables() {
		return tables;
	}
	
	public String getViewClassName() {
		return viewClassName;
	}
	
	public boolean isHasSubTables() {
		return tables.size() != 0;
	}
	
	public static class ViewTableInfo{
		
		private final String name;
		
		private final String className;
		
		private final List<ColumnMeta> columns;

		public ViewTableInfo(String name, List<ColumnMeta> columns) {
			super();
			this.name = name;
			this.className = TextUtils.var2class(name);
			this.columns = columns;
		}

		public String getName() {
			return name;
		}
		
		public List<ColumnMeta> getColumns() {
			return columns;
		}
		
		public String getClassName() {
			return className;
		}
	}
}
