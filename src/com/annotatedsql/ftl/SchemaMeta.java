package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.List;

public class SchemaMeta {

	private final String className;
	private String pkgName;
	
	private final List<TableMeta> tables = new ArrayList<TableMeta>();
	private final List<IndexMeta> indexes = new ArrayList<IndexMeta>();
	private final List<ViewMeta> views = new ArrayList<ViewMeta>();
	
	public SchemaMeta(String className) {
		super();
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}
	
	public List<TableMeta> getTables() {
		return tables;
	}
	
	public List<IndexMeta> getIndexes() {
		return indexes;
	}
	
	public void addTable(TableMeta table){
		tables.add(table);
	}

	public void addIndex(IndexMeta indexMeta) {
		indexes.add(indexMeta);
	}
	
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	
	public String getPkgName() {
		return pkgName;
	}

	public boolean isEmpty() {
		return tables.size() == 0;
	}

	public void addView(ViewMeta viewMeta) {
		views.add(viewMeta);
	}
	
	public List<ViewMeta> getViews() {
		return views;
	}
}
