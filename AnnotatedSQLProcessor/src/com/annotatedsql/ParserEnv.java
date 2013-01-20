package com.annotatedsql;

import java.util.HashMap;
import java.util.Map;

import com.annotatedsql.ftl.TableColumns;

public class ParserEnv {
	
	private Map<String, TableColumns> tableColumns = new HashMap<String, TableColumns>();
	
	public boolean isColumnExists(String table, String column){
		TableColumns columns = tableColumns.get(table);
		return columns != null && columns.contains(column) ;
	}
	
	public void addTable(String table, TableColumns columns){
		tableColumns.put(table, columns);
	}

	public TableColumns getColumns(String tableName) {
		return tableColumns.get(tableName);
	}

	public boolean containsTable(String name) {
		return tableColumns.containsKey(name);
	}
}
