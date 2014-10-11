package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.Index;
import com.annotatedsql.annotation.sql.Indexes;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.StaticWhere;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.IndexMeta;
import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.processor.sql.ColumnProcessor.ColumnMeta;
import com.annotatedsql.util.ClassUtil;
import com.annotatedsql.util.Where;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class TableParser {

    private final TypeElement c;
    private final ParserEnv parserEnv;
    private final ProcessorLogger logger;

    private String tableName;

    public TableParser(TypeElement c, ParserEnv parserEnv, ProcessorLogger logger) {
        this.logger = logger;
        this.c = c;
        this.parserEnv = parserEnv;
    }

    public TableResult parse() {
        TableColumns tableColumns = new TableColumns(c.getSimpleName().toString(), false);

        Table table = c.getAnnotation(Table.class);
        tableName = table.value();

        if (parserEnv.containsTable(tableName)) {
            throw new AnnotationParsingException(String.format("Table/View with name '%s' already defined", tableName), c);
        }

        List<Element> fields = ClassUtil.getAllClassFields(c);

        final StringBuilder sql = new StringBuilder(fields.size() * 32);
        sql.append("create table ").append(tableName);
        final int pos = sql.length();

        int columnCount = 0;
        boolean hasPrimaryKey = false;
        for (Element f : fields) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            ColumnMeta meta = ColumnProcessor.create((VariableElement) f);
            tableColumns.add(f.getSimpleName().toString(), meta.name, meta.dataType, meta.isNotNull);
            hasPrimaryKey |= meta.isPrimary;
            sql.append(',').append(meta.sql);
            columnCount++;
        }
        if (columnCount == 0) {
            throw new AnnotationParsingException("Table doesn't have columns", c);
        }

        PrimaryKey pk = (PrimaryKey) c.getAnnotation(PrimaryKey.class);
        if (pk != null && pk.columns().length > 0) {
            if (hasPrimaryKey) {
                throw new AnnotationParsingException("Table has more that one PRIMARY KEY", c);
            }
            proceedPk(sql, pk.columns());
        }
        sql.setCharAt(pos, '(');
        sql.append(')');

        parserEnv.addTable(tableName, tableColumns);

        TableResult tableResult = new TableResult(table.value(), sql.toString(), tableColumns, parseWhere());
        parserEnv.addTable(tableName, tableResult);
        return tableResult;
    }

    private Where parseWhere() {
        List<StaticWhere> annotations = new ArrayList<StaticWhere>();
        collectAnnotation(annotations, StaticWhere.class, Arrays.asList(c.asType()));

        Where builder = new Where(tableName);
        for (StaticWhere w : annotations) {
            builder.add(w.column(), w.value());
        }
        return builder;
    }

    private static <A extends Annotation> void collectAnnotation(List<A> annotations, Class<A> clazz, List<? extends TypeMirror> typeMirrors) {
        if (typeMirrors == null || typeMirrors.isEmpty()) {
            return;
        }
        for (TypeMirror t : typeMirrors) {
            if (!(t instanceof DeclaredType)) {
                continue;
            }
            Element element = ((DeclaredType) t).asElement();
            A a = element.getAnnotation(clazz);
            if (a != null) {
                annotations.add(a);
            }
            if (element instanceof TypeElement) {
                TypeElement typeElement = ((TypeElement) element);
                TypeMirror superclass = typeElement.getSuperclass();
                if (superclass != null) {
                    collectAnnotation(annotations, clazz, Arrays.asList(superclass));
                }
                collectAnnotation(annotations, clazz, ((TypeElement) element).getInterfaces());
            }
        }
    }

    private static void proceedPk(final StringBuilder sql, String[] columns) {
        sql.append(", PRIMARY KEY(");
        for (final String column : columns) {
            sql.append(' ').append(column).append(',');
        }
        sql.setLength(sql.length() - 1);
        sql.append(")");
    }

    public static List<IndexMeta> proceedIndexes(TypeElement c) {

        List<Index> indexes = new ArrayList<Index>();

        List<Indexes> annotations = new ArrayList<Indexes>();
        List<TypeMirror> me = Arrays.asList(c.asType());

        collectAnnotation(annotations, Indexes.class, me);
        for (Indexes i : annotations) {
            Index[] values = i.value();
            if (values != null) {
                Collections.addAll(indexes, values);
            }
        }
        collectAnnotation(indexes, Index.class, me);

        List<IndexMeta> result = new ArrayList<IndexMeta>();
        for (Index i : indexes) {
            IndexMeta indexMeta = IndexProcessor.create(c, i);
            result.add(indexMeta);
        }
        return result;
    }
}
