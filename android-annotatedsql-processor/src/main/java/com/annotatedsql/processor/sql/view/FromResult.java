package com.annotatedsql.processor.sql.view;

import com.annotatedsql.ParserResult;
import com.annotatedsql.ftl.ColumnMeta;

import java.util.List;

public class FromResult extends ParserResult{

	private final String aliasName;
	
	private final String selectSql;
	
	private final List<ColumnMeta> columns;

	public FromResult(String aliasName, String sql, String selectSql, List<ColumnMeta> columns) {
		super(sql);
		this.aliasName = aliasName;
		this.selectSql = selectSql;
		this.columns = columns;
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
}
