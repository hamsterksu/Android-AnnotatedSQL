package com.annotatedsql.processor.sql;

import com.annotatedsql.ParserResult;
import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.util.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableResult extends ParserResult{

    private String tableName;
    private TableColumns tableColumns;
	//private final List<String> columns;
    private final Where where;

	public TableResult(String tableName, String sql, TableColumns tableColumns, Where where) {
		super(sql);
        this.tableName = tableName;
		this.tableColumns = tableColumns;
        this.where = where;
	}

    public Where getWhere() {
        return where;
    }

    /*public void addColumn(String column) {
		columns.add(column);
	}*/

	public List<String> getColumns() {
        return tableColumns == null ? new ArrayList<String>(0) : tableColumns.toColumnsList();
	}

    public String getTableName() {
        return tableName;
    }

    public TableColumns getTableColumns() {
        return tableColumns;
    }
}
