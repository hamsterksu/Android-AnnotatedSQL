package com.annotatedsql.ftl;

import com.annotatedsql.util.TextUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by jbanse on 21/09/2014.
 */
public class CursorWrapperMeta {

    private String pkgName;

    private final String tableName;

    private final String tableClassName;

    private final Map<String, String> columnToType;

    private final List<String> columnNameList;

    public CursorWrapperMeta(String packageName, String tableClassName, String tableName, List<String> columnNameList, Map<String, String> columnToType) {
        pkgName = packageName;
        this.tableName = tableName;
        this.columnNameList = columnNameList;
        this.columnToType = columnToType;
        this.tableClassName = tableClassName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getCursorWrapperName() {
        return tableName + "Cursor";
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public String getClassTypeForColumn(String columnName) {
        return columnToType.get(columnName);
    }

    public String convertInCamelCase(String columnName) {
        return TextUtils.convertInCamelCase(columnName);
    }

    public String getTableClassName() {
        return tableClassName;
    }

}
