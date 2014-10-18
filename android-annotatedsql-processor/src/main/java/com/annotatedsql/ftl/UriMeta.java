package com.annotatedsql.ftl;

import com.annotatedsql.util.TextUtils;
import com.annotatedsql.util.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UriMeta {

    private final String path;
    private final int code;
    private final String tableLink;
    private final String codeHex;
    private final boolean isItem;
    private final String selectColumn;
    private final List<AltNotify> altNotify;
    private final boolean onlyQuery;
    private final List<TriggerMeta> triggers;
    private final boolean rawQuery;

    private final Where where;
    private final String customMimeType;

    public UriMeta(String path, int code, boolean isItem, String customMimeType, String selectColumn, String tableLink, String[] altNotify, boolean onlyQuery, List<TriggerMeta> triggers, boolean rawQuery, Where where) {
        super();
        this.path = path;
        this.code = code;
        this.tableLink = tableLink;
        this.isItem = isItem;
        this.selectColumn = selectColumn;
        this.onlyQuery = onlyQuery;
        this.triggers = triggers;
        this.rawQuery = rawQuery;
        this.where = where;
        this.codeHex = "0x" + Integer.toHexString(code);
        this.customMimeType = customMimeType;

        if (altNotify != null && altNotify.length != 0) {
            this.altNotify = new ArrayList<AltNotify>();
            for (String n : altNotify) {
                if (!TextUtils.isEmpty(n)) {
                    this.altNotify.add(new AltNotify(n));
                }
            }
        } else {
            this.altNotify = Collections.emptyList();
        }
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

    public List<AltNotify> getAltNotify() {
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
        return !altNotify.isEmpty();
    }

    public boolean isWhere() {
        return where != null && !where.isEmpty();
    }

    public String getQueryWhere() {
        assert where != null;
        return where.getQueryWhere();
    }

    public String getWhereArgs() {
        assert where != null;
        return where.getWhereArgs();
    }

    public String getCustomMimeType() {
        return customMimeType;
    }

    public boolean isHasCustomMimeType() {
        return !TextUtils.isEmpty(customMimeType);
    }
}
