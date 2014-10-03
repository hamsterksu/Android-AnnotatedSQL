package com.annotatedsql.ftl;

import com.annotatedsql.util.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by jbanse on 21/09/2014.
 */
public class CursorWrapperMeta {

    private final String tableCanonicalName;
    private String pkgName;

    private final String tableClassName;

    private final Map<String, String> columnToType;
    private final Map<String, String> columnToVariable;

    private final List<String> columnNameList;

    public CursorWrapperMeta(String packageName, Element tableClassName, List<ViewMeta.ViewTableInfo> viewTableInfo) {
        pkgName = packageName;
        this.tableClassName = tableClassName.getSimpleName().toString();
        this.tableCanonicalName = ((TypeElement) tableClassName).getQualifiedName().toString();
        columnNameList = new ArrayList<String>();
        columnToVariable = new HashMap<String, String>();
        columnToType = new HashMap<String, String>();
        for (ViewMeta.ViewTableInfo lViewTableInfo : viewTableInfo) {
            for (ColumnMeta lColumnMeta : lViewTableInfo.getColumns()) {
                String columnName = lColumnMeta.alias;
                columnNameList.add(columnName);
                columnToVariable.put(columnName, lColumnMeta.getVariableAlias());
                columnToType.put(columnName, lColumnMeta.classType);
            }
        }
    }


    public CursorWrapperMeta(String packageName, Element tableClassName, List<String> columnNameList, Map<String, String> columnToType, Map<String, String> columnToVariable) {
        pkgName = packageName;
        this.columnNameList = columnNameList;
        this.columnToType = columnToType;
        this.tableClassName = tableClassName.getSimpleName().toString();
        this.tableCanonicalName = ((TypeElement) tableClassName).getQualifiedName().toString();
        this.columnToVariable = columnToVariable;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getCursorWrapperName() {
        return tableClassName.concat("Cursor");
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public String getClassTypeForColumn(String columnName) {
        return columnToType.get(columnName);
    }

    public String getVariableForColumn(String columnName) {
        return columnToVariable.get(columnName);
    }

    public String convertInCamelCase(String columnName) {
        return TextUtils.convertInCamelCase(columnName);
    }

    public String getTableClassName() {
        return tableClassName;
    }

    public String getTableCanonicalName() {
        return tableCanonicalName;
    }

}
