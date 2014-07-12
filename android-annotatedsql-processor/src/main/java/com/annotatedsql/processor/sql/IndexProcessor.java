package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Index;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.IndexMeta;
import com.annotatedsql.util.TextUtils;

import javax.lang.model.element.Element;

public class IndexProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static IndexMeta create(Element c) {
		Index index = (Index) c.getAnnotation(Index.class);
        return create(c, index);
    }

    public static IndexMeta create(Element c, Index index) {
		Table table = (Table) c.getAnnotation(Table.class);
		if(index == null || table == null)
			throw new AnnotationParsingException(String.format("Index annotation is applied for table only(index = %s; table = %s; element = %s)", index, table, c.getSimpleName()), c);
		if(TextUtils.isEmpty(index.name()))
			throw new AnnotationParsingException("Index name can not be empty", c);
		
		String[] columns = index.columns();
		if(columns.length == 0)
			throw new AnnotationParsingException("Index can not be empty", c);
		
		final StringBuilder sql = new StringBuilder(columns.length * 32);
        String indexName = table.value() + "_" + index.name();
		sql.append("create index idx_").append(indexName)
                .append(" on ").append(table.value()).append('(');
		for (final String column : columns) {
			sql.append(' ').append(column).append(',');
		}
		sql.setLength(sql.length() - 1); // chop off last comma
		sql.append(')');
        return new IndexMeta(indexName, sql.toString());
	}
}
