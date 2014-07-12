package com.annotatedsql.processor.sql;

import com.annotatedsql.ParserResult;
import com.annotatedsql.util.Where;

import java.util.List;

public class TableResult extends ParserResult{
	
	private final List<String> columns;
    private final Where where;

	public TableResult(String sql, List<String> columns, Where where) {
		super(sql);
		this.columns = columns;
        this.where = where;
	}

    public Where getWhere() {
        return where;
    }

    public void addColumn(String column) {
		columns.add(column);
	}

	public List<String> getColumns() {
		return columns;
	}
}
