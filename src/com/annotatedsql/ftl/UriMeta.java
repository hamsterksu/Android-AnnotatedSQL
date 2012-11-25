package com.annotatedsql.ftl;

import java.util.List;

import com.annotatedsql.util.TextUtils;

public class UriMeta {

	private final String path;
	private final int code;
	private final String tableLink;
	private final String codeHex;
	private final boolean isItem;
	private final String selectColumn;
	private final String altNotify;
	private final boolean onlyQuery;
	private final List<TriggerMeta> triggers;
	private final boolean rawQuery;
	
	public UriMeta(String path, int code, boolean isItem, String selectColumn, String tableLink, String altNotify, boolean onlyQuery, List<TriggerMeta> triggers, boolean rawQuery) {
		super();
		this.path = path;
		this.code = code;
		this.tableLink = tableLink;
		this.isItem = isItem;
		this.selectColumn = selectColumn;
		this.altNotify = altNotify;
		this.onlyQuery = onlyQuery;
		this.triggers = triggers;
		this.rawQuery = rawQuery;
		codeHex = "0x" + Integer.toHexString(code);
	}
	
	public String getPath() {
		return path;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getTableLink() {
		return tableLink;
	}
	
	public String getCodeHex() {
		return codeHex;
	}
	
	public boolean isItem() {
		return isItem;
	}
	
	public String getSelectColumn() {
		return selectColumn;
	}
	
	public String getAltNotify() {
		if(altNotify.length() > 2 && isItemizedAltNotify()){
			return altNotify.substring(0, altNotify.length() - 2);
		}
		return altNotify;
	}
	
	public boolean isOnlyQuery() {
		return onlyQuery || rawQuery;
	}

	public List<TriggerMeta> getTriggers() {
		return triggers;
	}
	
	public boolean isTriggered() {
		return triggers != null && triggers.size() != 0;
	}
	
	public boolean isRawQuery() {
		return rawQuery;
	}
	
	public boolean isHasAltNotify() {
		return !TextUtils.isEmpty(altNotify);
	}
	
	public boolean isItemizedAltNotify() {
		return !TextUtils.isEmpty(altNotify) && altNotify.endsWith("/#");
	}
}
