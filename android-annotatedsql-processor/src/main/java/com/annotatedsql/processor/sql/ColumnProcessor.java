package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Autoincrement;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.NotNull;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Unique;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.util.TextUtils;

import java.util.Date;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class ColumnProcessor {

    public ColumnProcessor() {
    }

    /**
     * @throws AnnotationParsingException
     */
    public static ColumnMeta create(ProcessingEnvironment env, VariableElement f) {
        String columnName = null;
        try {
            columnName = (String) f.getConstantValue();
        } catch (Exception e) {
            throw new AnnotationParsingException("Can not find column name", f);
        }

        Column column = f.getAnnotation(Column.class);
        boolean isPrimary = f.getAnnotation(PrimaryKey.class) != null;
        boolean isAutoIncrement = f.getAnnotation(Autoincrement.class) != null;
        boolean isNotNull = f.getAnnotation(NotNull.class) != null;

        Unique unique = f.getAnnotation(Unique.class);

        StringBuilder sql = new StringBuilder(" ");
        sql.append(columnName).append(' ').append(getTypeFromClass(env, column));
        if (isPrimary) {
            sql.append(" PRIMARY KEY");
        }
        if (isAutoIncrement) {
            sql.append(" AUTOINCREMENT");
        }
        if (isNotNull) {
            sql.append(" NOT NULL");
        }

        if (unique != null) {
            sql.append(" UNIQUE ON CONFLICT ").append(unique.type());
        }
        String defVal = column.defVal();
        if (!TextUtils.isEmpty(defVal)) {
            sql.append(" DEFAULT (").append(defVal).append(")");
        }

        return new ColumnMeta(columnName, isPrimary, sql.toString());
    }

    private static Column.Type getTypeFromClass(ProcessingEnvironment env, Column column) {
        ProcessorLogger logger = new ProcessorLogger(env.getMessager());
        TypeMirror annotationClassField = null;
        Column.Type result;
        try {
            column.javaClass();
        } catch (MirroredTypeException e) {
            annotationClassField = e.getTypeMirror();
        }

        switch (annotationClassField.getKind()) {
            case INT:
            case BOOLEAN:
            case LONG:
                result = Column.Type.INTEGER;
                break;
            case DOUBLE:
            case FLOAT:
                result = Column.Type.REAL;
                break;
            case ARRAY:
                result = Column.Type.BLOB;
                break;
            case DECLARED:
                String declaringClass = env.getTypeUtils().asElement(annotationClassField).toString();
                if (Integer.class.getCanonicalName() == declaringClass
                        || Long.class.getCanonicalName() == declaringClass
                        || Boolean.class.getCanonicalName() == declaringClass
                        || Date.class.getCanonicalName() == declaringClass) {
                    result = Column.Type.INTEGER;
                } else if (Double.class.getCanonicalName() == declaringClass
                        || Float.class.getCanonicalName() == declaringClass) {
                    result = Column.Type.REAL;
                } else {
                    result = Column.Type.TEXT;
                }
                break;
            default:
                result = Column.Type.TEXT;
        }
        return result;
    }

    static class ColumnMeta {
        final String sql;
        final boolean isPrimary;
        final String name;

        public ColumnMeta(String name, boolean isPrimary, String sql) {
            super();
            this.name = name;
            this.sql = sql;
            this.isPrimary = isPrimary;
        }

    }
}
