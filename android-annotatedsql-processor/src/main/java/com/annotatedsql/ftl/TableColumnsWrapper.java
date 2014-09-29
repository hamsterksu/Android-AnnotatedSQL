package com.annotatedsql.ftl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jbanse on 26/09/2014.
 */
public class TableColumnsWrapper extends TableColumns {

    private final Map<String, String> column2type = new LinkedHashMap<String, String>();

    public TableColumnsWrapper(String className, boolean isView) {
        super(className, isView);
    }

    public void add(String variable, String column, String type) {
        super.add(variable, column);
        column2type.put(column, type);
    }

    public Map<String, String> getColumn2type() {
        return column2type;
    }

}
