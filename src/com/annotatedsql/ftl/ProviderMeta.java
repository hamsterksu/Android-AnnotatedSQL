package com.annotatedsql.ftl;

import java.util.ArrayList;
import java.util.List;

import com.annotatedsql.util.TextUtils;

public class ProviderMeta {

	private String className;
	private String pkgName;
	private String schemaClassName;
	private String authority;
	private List<UriMeta> entities = new ArrayList<UriMeta>();
	private List<String> imports = new ArrayList<String>();
	private String openHelperClass;
	
	public ProviderMeta(String className){
		this.className = className;
	}
	
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	
	public String getSchemaClassName() {
		return schemaClassName;
	}
	
	public String getPkgName() {
		return pkgName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setSchemaClassName(String schemaClassName) {
		this.schemaClassName = schemaClassName;
	}
	
	public List<UriMeta> getEntities() {
		return entities;
	}

	public void addUris(List<UriMeta> list){
		entities.addAll(list);
	}
	
	public String getAuthority() {
		return authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public List<String> getImports() {
		return imports;
	}
	
	public void addImport(String importStr){
		imports.add(importStr);
	}
	
	public void setOpenHelperClass(String openHelperClass) {
		this.openHelperClass = openHelperClass;
	}
	
	public String getOpenHelperClass() {
		return openHelperClass;
	}
	
	public boolean isGenerateHelper(){
		return TextUtils.isEmpty(openHelperClass);
	}
}
