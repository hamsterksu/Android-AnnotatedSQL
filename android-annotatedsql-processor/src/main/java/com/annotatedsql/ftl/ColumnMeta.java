package com.annotatedsql.ftl;

public class ColumnMeta {

	public final String fullName;
	public final String alias;
	public final String variableName;
	public final String variableAlias;

	public ColumnMeta(String variableName, String fullName, String alias, String variableAlias) {
		super();
		this.variableName = variableName;
		this.fullName = fullName;
		this.alias = alias;
		this.variableAlias = variableAlias;
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
