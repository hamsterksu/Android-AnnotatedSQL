package com.annotatedsql.processor.sql;

import java.util.List;

import com.annotatedsql.ParserResult;

public class TableResult extends ParserResult{
	
	private final List<String> columns;

	public TableResult(String sql, List<String> columns) {
		super(sql);
		this.columns = columns;
	}

	public void addColumn(String column) {
		columns.add(column);
	}

	public List<String> getColumns() {
		return columns;
	}
}
