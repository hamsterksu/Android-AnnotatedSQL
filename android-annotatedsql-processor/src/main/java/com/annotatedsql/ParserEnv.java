package com.annotatedsql;

import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.processor.sql.TableResult;
import com.annotatedsql.util.Where;

import java.util.HashMap;
import java.util.Map;

public class ParserEnv {

    private final String rootClass;

    private Map<String, TableColumns> tableColumns = new HashMap<String, TableColumns>();
    private Map<String, TableResult> tables = new HashMap<String, TableResult>();

    public ParserEnv(String rootClass) {
        this.rootClass = rootClass;
    }

    public boolean isColumnExists(String table, String column) {
        TableColumns columns = tableColumns.get(table);
        return columns != null && columns.contains(column);
    }

    public void addTable(String table, TableColumns columns) {
        tableColumns.put(table, columns);
    }

    public void addTable(String table, TableResult columns) {
        tables.put(table, columns);
    }

    public TableColumns getColumns(String tableName) {
        return tableColumns.get(tableName);
    }

    public boolean containsTable(String name) {
        return tableColumns.containsKey(name);
    }

    public Where getTableWhere(String name) {
        TableResult result = tables.get(name);
        if (result == null)
            return null;
        return result.getWhere();
    }

    public String getRootClass() {
        return rootClass;
    }
}
