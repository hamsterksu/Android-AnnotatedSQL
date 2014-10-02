package com.annotatedsql.processor.sql;

import com.annotatedsql.ParserResult;
import com.annotatedsql.util.Where;

import java.util.List;
import java.util.Map;

public class TableResult extends ParserResult{
	
	private final List<String> columns;
    private final Where where;

    private final Map<String, String> column2type;
    private final Map<String, String> column2Variable;


    public TableResult(String sql, List<String> columns, Where where, Map<String,String> column2type, Map<String,String> column2variable) {
		super(sql);
		this.columns = columns;
        this.where = where;
        this.column2type = column2type;
        this.column2Variable = column2variable;
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

    public Map<String, String> getColumn2type() {
        return column2type;
    }

    public Map<String, String> getColumn2Variable() {
        return column2Variable;
    }
}
