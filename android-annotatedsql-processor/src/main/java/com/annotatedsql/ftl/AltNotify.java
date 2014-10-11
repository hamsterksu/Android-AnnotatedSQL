package com.annotatedsql.ftl;

import com.annotatedsql.util.TextUtils;

public class AltNotify {

    private String value;

    public AltNotify(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        if (value.length() > 2 && isItemizedAltNotify()) {
            return value.substring(0, value.length() - 2);
        }
        return value;
    }

    public boolean isItemizedAltNotify() {
        return !TextUtils.isEmpty(value) && value.endsWith("/#");
    }
}
