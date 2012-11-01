package com.annotatedsql.ftl;

import com.annotatedsql.annotation.provider.Trigger.Type;

public class TriggerMeta {
	
	private final String methodName;
	private boolean isDelete = false; 
	private boolean isInsert = false;
	private boolean isUpdate = false;
	private final boolean isBefore;

	public TriggerMeta(String name, Type[] types, boolean before) {
		super();
		this.isBefore = before;
		this.methodName = name;
		if(types == null || types.length == 0){
			isDelete = true;
			isInsert = true;
			isUpdate = true;
		}else{
			for(Type t : types){
				switch (t) {
				case DELETE:
					isDelete = true;
					break;
				case INSERT:
					isInsert = true;
					break;
				case UPDATE:
					isUpdate = true;
					break;
				default:
					isDelete = true;
					isInsert = true;
					isUpdate = true;
					break;
				}
			}
		}
	}
	
	public boolean isDelete() {
		return isDelete;
	}
	
	public boolean isInsert() {
		return isInsert;
	}
	
	public boolean isUpdate() {
		return isUpdate;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public boolean isBefore() {
		return isBefore;
	}
	
	public boolean isAfter() {
		return !isBefore;
	}
}
