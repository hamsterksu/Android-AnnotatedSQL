package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.List;

public class SchemaMeta {

	private final String storeClassName;
	private final String className;
	private String pkgName;
	private final String defineClassName;
	
	private String dbName;
	private int dbVersion;
	
	private final List<TableMeta> tables = new ArrayList<TableMeta>();
	private final List<IndexMeta> indexes = new ArrayList<IndexMeta>();
	private final List<ViewMeta> views = new ArrayList<ViewMeta>();
	private final List<ViewMeta> queries = new ArrayList<ViewMeta>();
	
	public SchemaMeta(String storeClassName, String className, String defineClassName) {
		super();
		this.storeClassName = storeClassName;
		this.className = className;
		this.defineClassName = defineClassName;
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
	
	public void addQuery(ViewMeta table){
		queries.add(table);
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
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	
	public int getDbVersion() {
		return dbVersion;
	}
	
	public List<ViewMeta> getQueries() {
		return queries;
	}
	
	public String getDefineClassName() {
		return defineClassName;
	}
	
	public String getStoreClassName() {
		return storeClassName;
	}
}
