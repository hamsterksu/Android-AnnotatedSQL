package com.annotatedsql.ftl;

import com.annotatedsql.annotation.sql.Column;

public class ColumnMeta {

	public final String fullName;
	public final String alias;
	public final String variableName;
	public final String variableAlias;
    public final Column.Type sqlType;
    public final boolean isNotNull;

	public ColumnMeta(String variableName, String fullName, String alias, String variableAlias, Column.Type sqlType, boolean isNotNull) {
		super();
		this.variableName = variableName;
		this.fullName = fullName;
		this.alias = alias;
		this.variableAlias = variableAlias;
        this.sqlType = sqlType;
        this.isNotNull = isNotNull;
	}
	

	public String getFullName() {
		return fullName;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public String getVariableAlias() {
		return variableAlias;
	}


}
