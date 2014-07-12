package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableColumns implements Iterable<String>{

	private final boolean isView;
	
	private final String className;
	
	private final Map<String, String> column2variable = new LinkedHashMap<String, String>();
	
	private final Map<String, String> variable2columns = new LinkedHashMap<String, String>();
	
	public TableColumns(String className, boolean isView){
		this.isView = isView;
		this.className = className;
	}
	
	public void add(String variable, String column){
		column2variable.put(column, variable);
		variable2columns.put(variable, column);
	}
	
	public void add(String clazz, String variable, String column){
		String var = clazz.toUpperCase() + "_" + variable;
		
		column2variable.put(column, var);
		variable2columns.put(var, column);
	}
	
	public String getColumn(String variable){
		return variable2columns.get(variable);
	}
	
	public String getVariable(String column){
		return column2variable.get(column);
	}
	
	public boolean isEmpty(){
		return column2variable.isEmpty();
	}

	public boolean contains(String column) {
		return column2variable.containsKey(column);
	}

	@Override
	public Iterator<String> iterator() {
		return column2variable.keySet().iterator();
	}
	
	public List<String> toColumnsList(){
		return new ArrayList<String>(column2variable.keySet());
	}
	
	public boolean isView() {
		return isView;
	}
	
	public String getClassName() {
		return className;
	}
}
