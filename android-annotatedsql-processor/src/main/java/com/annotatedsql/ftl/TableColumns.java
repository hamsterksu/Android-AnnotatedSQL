package com.annotatedsql.ftl;

import com.annotatedsql.annotation.sql.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableColumns implements Iterable<String> {

    private final boolean isView;

    private final String className;

    private final Map<String, String> column2variable = new LinkedHashMap<String, String>();

    private final Map<String, Column.Type> column2SqlType = new LinkedHashMap<String, Column.Type>();

    private final Map<String, String> variable2columns = new LinkedHashMap<String, String>();

    private final Map<String, Boolean> column2IsNotNull = new LinkedHashMap<String, Boolean>();

    public TableColumns(String className, boolean isView) {
        this.isView = isView;
        this.className = className;
    }

    public void add(String variable, String column, Column.Type sqlType, boolean isNotNull) {
        column2variable.put(column, variable);
        variable2columns.put(variable, column);
        column2SqlType.put(column, sqlType);
        column2IsNotNull.put(column, isNotNull);
    }

    public void add(String clazz, String variable, String column, Column.Type sqlType, boolean isNotNull) {
        String var = clazz.toUpperCase() + "_" + variable;

        column2variable.put(column, var);
        variable2columns.put(var, column);
        column2SqlType.put(column, sqlType);
        column2IsNotNull.put(column, isNotNull);
    }

    public String getColumn(String variable) {
        return variable2columns.get(variable);
    }

    public String getVariable(String column) {
        return column2variable.get(column);
    }

    public Column.Type getSqlType(String column) {
        return column2SqlType.get(column);
    }

    public boolean isColumnNotNull(String column) {
        return column2IsNotNull.get(column);
    }

    public boolean isEmpty() {
        return column2variable.isEmpty();
    }

    public boolean contains(String column) {
        return column2variable.containsKey(column);
    }

    @Override
    public Iterator<String> iterator() {
        return column2variable.keySet().iterator();
    }

    public List<String> toColumnsList() {
        return new ArrayList<String>(column2variable.keySet());
    }

    public boolean isView() {
        return isView;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getColumn2Variable() {
        return column2variable;
    }

    public Map<String, String> getVariable2Columns() {
        return variable2columns;
    }
}
