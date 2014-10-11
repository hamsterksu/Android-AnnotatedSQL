package com.annotatedsql.util;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamsterksu on 07.02.14.
 */
public class Where {

    private final List<WhereObject> where = new ArrayList<WhereObject>();
    private final List<WhereArgObject> whereArgs = new ArrayList<WhereArgObject>();

    private String alias;

    public Where(String alias) {
        this.alias = alias;
    }

    public Where copy(String newAlias) {
        Where copy = new Where(newAlias);
        copy.where.addAll(this.where);
        copy.whereArgs.addAll(this.whereArgs);
        return copy;
    }

    public Where add(String column, String value) {
        where.add(new WhereObject(column));
        whereArgs.add(new WhereArgObject(value));

        return this;
    }

    public String getQueryWhere() {
        assert where != null;
        return Joiner.on(" and ").skipNulls().join(where);
    }

    public String getWhereArgs() {
        assert whereArgs != null;
        return Joiner.on(", ").skipNulls().join(whereArgs);
    }

    public String getAsCondition() {
        StringBuffer condition = new StringBuffer(128);
        for (int i = 0; i < where.size(); i++) {
            if (i != 0) {
                condition.append(" and ");
            }
            condition.append(alias).append('.').append(where.get(i).field).append(" = ").append(whereArgs.get(i).obj);
        }
        return condition.toString();
    }

    public boolean isEmpty() {
        return where.isEmpty();
    }

    public Where exclude(String excludeWhere) {
        if (TextUtils.isEmpty(excludeWhere))
            return this;
        Where copyWhere = new Where(this.alias);
        for (int i = 0; i < where.size(); i++) {
            WhereObject o = where.get(i);
            WhereArgObject arg = whereArgs.get(i);
            if (!excludeWhere.equals(o.field)) {
                copyWhere.add(o.field, arg.obj);
            }
        }
        return copyWhere;
    }

    private class WhereObject {

        private final String field;

        public WhereObject(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return alias + "." + field + " = ?";
        }
    }

    private static class WhereArgObject {

        private final String obj;

        public WhereArgObject(String obj) {
            this.obj = obj;
        }

        @Override
        public String toString() {
            return "\"" + obj + "\"";
        }
    }

}