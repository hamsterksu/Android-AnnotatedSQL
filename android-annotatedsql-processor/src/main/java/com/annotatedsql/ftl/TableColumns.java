package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableColumns implements Iterable<String> {

    private final boolean isView;

    private final String className;

    private final Map<String, String> column2variable = new LinkedHashMap<String, String>();

    private final Map<String, String> variable2columns = new LinkedHashMap<String, String>();

    private final Map<String, String> column2type = new LinkedHashMap<String, String>();

    public TableColumns(String className, boolean isView) {
        this.isView = isView;
        this.className = className;
    }

    public void add(String variable, String column, String javaType) {
        column2variable.put(column, variable);
        variable2columns.put(variable, column);
        column2type.put(column, javaType);
    }

    public void add(String clazzTable, String variable, String column, String javaType) {
        String var = clazzTable.toUpperCase() + "_" + variable;

        column2variable.put(column, var);
        variable2columns.put(var, column);
        column2type.put(column, javaType);
    }

    public String getColumn(String variable) {
        return variable2columns.get(variable);
    }

    public String getVariable(String column) {
        return column2variable.get(column);
    }

    public String getJavaType(String column){
        return column2type.get(column);
    }

    public Map<String, String> getColumn2variable() {
        return column2variable;
    }

    public Map<String, String> getColumn2type() {
        return column2type;
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
}
