package com.annotatedsql.processor.sql.view;

import com.annotatedsql.AnnotationParser;
import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.ParserResult;
import com.annotatedsql.annotation.sql.Columns;
import com.annotatedsql.annotation.sql.IgnoreColumns;
import com.annotatedsql.ftl.ColumnMeta;
import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.processor.sql.SimpleViewParser;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public abstract class ViewTableColumnParser<T extends ParserResult, A extends Annotation> implements AnnotationParser {

    private TableColumns tableColumns;

    protected final String aliasName;
    protected final String aliasVariale;

    protected final boolean ignoreId;
    protected final Element field;
    protected final A annotation;
    protected final String tableName;

    protected final ParserEnv parserEnv;
    protected final SimpleViewParser parentParser;

    public ViewTableColumnParser(ParserEnv parserEnv, SimpleViewParser parentParser, Element f, boolean ignoreId) {
        this.parserEnv = parserEnv;
        this.parentParser = parentParser;
        this.field = f;
        this.annotation = (A) f.getAnnotation(getAnnotationClass());
        this.ignoreId = ignoreId;
        this.aliasName = (String) ((VariableElement) this.field).getConstantValue();
        this.aliasVariale = this.field.getSimpleName().toString();
        this.tableName = parseTableName();
        this.tableColumns = parserEnv.getColumns(tableName);
        checkAlias();
    }

    protected Element getField() {
        return field;
    }

    public abstract Class<A> getAnnotationClass();

    public abstract String parseTableName();

    protected List<ColumnMeta> parseColumns() {
        boolean ignoreColumns = field.getAnnotation(IgnoreColumns.class) != null;
        Columns columnsA = field.getAnnotation(Columns.class);
        String[] selectedColumns = columnsA != null ? columnsA.value() : null;
        List<ColumnMeta> columns = null;
        if (!ignoreColumns) {
            columns = parseColumns(selectedColumns);
        }
        return columns;
    }

    protected List<ColumnMeta> parseColumns(String[] selectedColumns) {
        if (this.tableColumns == null || this.tableColumns.isEmpty()) {
            return Collections.emptyList();
        }

        List<ColumnMeta> resultList = new ArrayList<ColumnMeta>();
        if (selectedColumns != null && selectedColumns.length != 0) {
            for (String c : selectedColumns) {
                checkColumnExists(c);
                resultList.add(getColumnName(c));
            }
        } else {
            for (String c : this.tableColumns) {
                resultList.add(getColumnName(c));
            }
        }
        return resultList;
    }

    protected ColumnMeta getColumnName(String c) {
        String variable = tableColumns.getVariable(c);
        if (ignoreId && "_id".equals(c)) {
            final String variableAlias;
            if (tableColumns.isView()) {
                variableAlias = String.format("\"%s_%s\"", aliasName, c);
            } else {
                variableAlias = parserEnv.getRootClass() + "." + tableColumns.getClassName() + "." + variable;
            }
            final String columnName = tableColumns.getColumn(variable);
            return new ColumnMeta(variable, aliasName + "." + c, c, variableAlias, tableColumns.getSqlType(columnName), tableColumns.isColumnNotNull(columnName));
        } else {
            final String variableAlias;
            if (tableColumns.isView()) {
                variableAlias = String.format("\"%s_%s\"", aliasName, c);
            } else {
                variableAlias = String.format("%s.%s.%s + \"_\" + %s.%s.%s",
                        parserEnv.getRootClass(), parentParser.getClassName(), aliasVariale,
                        parserEnv.getRootClass(), tableColumns.getClassName(), variable);
            }
            final String columnName = tableColumns.getColumn(variable);
            return new ColumnMeta(variable, aliasName + "." + c,
                    aliasName + "_" + c, variableAlias, tableColumns.getSqlType(columnName), tableColumns.isColumnNotNull(columnName));
        }
    }

    public static String toSqlSelect(List<ColumnMeta> columns) {
        if (columns == null || columns.isEmpty()) {
            return null;
        }
        StringBuilder select = new StringBuilder();
        for (ColumnMeta c : columns) {
            select.append(' ').append(c.fullName).append(" as ").append(c.alias).append(',');
        }
        if (select.length() > 0) {
            select.setLength(select.length() - 1);
        }
        return select.toString();
    }

    protected void checkAlias() {
        Element aliasElement = parentParser.getAliasElement(aliasName);
        if (aliasElement != null) {
            throw new AnnotationParsingException(String.format("Duplicate alias '%s'", aliasElement), field, aliasElement);
        }
        parentParser.regAlias(aliasName, field);
    }

    protected void checkColumnExists(String c) {
        if (!this.tableColumns.contains(c)) {
            throw new RuntimeException("Table doesn't have column '" + c + "'");
        }
    }
}
