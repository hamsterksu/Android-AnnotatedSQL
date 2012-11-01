package com.annotatedsql.ftl;

public class UriMeta {

	private final String path;
	private final int code;
	private final String tableLink;
	private final String codeHex;
	private final boolean isItem;
	private final String selectColumn;
	private final String altNotify;
	private final boolean onlyQuery;
	private final TriggerMeta trigger;
	
	public UriMeta(String path, int code, boolean isItem, String selectColumn, String tableLink, String altNotify, boolean onlyQuery, TriggerMeta trigger) {
		super();
		this.path = path;
		this.code = code;
		this.tableLink = tableLink;
		this.isItem = isItem;
		this.selectColumn = selectColumn;
		this.altNotify = altNotify;
		this.onlyQuery = onlyQuery;
		this.trigger = trigger;
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
		return altNotify;
	}
	
	public boolean isOnlyQuery() {
		return onlyQuery;
	}

	public TriggerMeta getTrigger() {
		return trigger;
	}
	
	public boolean isTriggered() {
		return trigger != null;
	}
}
