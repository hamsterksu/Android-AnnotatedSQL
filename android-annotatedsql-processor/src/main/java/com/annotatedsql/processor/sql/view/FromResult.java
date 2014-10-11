package com.annotatedsql.processor.sql.view;

import com.annotatedsql.ParserResult;
import com.annotatedsql.ftl.ColumnMeta;

import java.util.List;

public class FromResult extends ParserResult {

    private final String aliasName;

    private final String tableName;

    private final String selectSql;

    private final List<ColumnMeta> columns;

    private final String excludeStaticWhere;

    public FromResult(String aliasName, String tableName, String sql, String selectSql, List<ColumnMeta> columns, String excludeStaticWhere) {
        super(sql);
        this.aliasName = aliasName;
        this.tableName = tableName;
        this.selectSql = selectSql;
        this.columns = columns;
        this.excludeStaticWhere = excludeStaticWhere;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getExcludeStaticWhere() {
        return excludeStaticWhere;
    }

    public String getTableName() {
        return tableName;
    }
}
