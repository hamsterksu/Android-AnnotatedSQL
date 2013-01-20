package com.annotatedsql.ftl;

public class ColumnMeta {

	public final String fullName;
	public final String alias;
	public final String variableName;

	public ColumnMeta(String tableVariable, String fullName, String alias) {
		super();
		this.variableName = tableVariable;
		this.fullName = fullName;
		this.alias = alias;
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
}
