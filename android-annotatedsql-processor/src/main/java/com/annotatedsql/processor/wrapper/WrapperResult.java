package com.annotatedsql.processor.wrapper;

import java.util.List;
import java.util.Map;

public class WrapperResult {

    private final List<String> columns;
    private final Map<String, String> columnsToType;

    public WrapperResult(List<String> columns, Map<String, String> columnsToType) {
        this.columns = columns;
        this.columnsToType = columnsToType;
    }

    public void addColumn(String column) {
        columns.add(column);
    }

    public List<String> getColumns() {
        return columns;
    }

    public Map<String, String> getColumnsToType() {
        return columnsToType;
    }
}
