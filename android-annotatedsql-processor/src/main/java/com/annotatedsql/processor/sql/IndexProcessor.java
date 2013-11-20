package com.annotatedsql.processor.sql;

import javax.lang.model.element.Element;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Index;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.util.TextUtils;

public class IndexProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static String create(Element c) {
		Index index = (Index) c.getAnnotation(Index.class);
		Table table = (Table) c.getAnnotation(Table.class);
		if(index == null || table == null)
			throw new AnnotationParsingException("Index annotation is applied for table only", c);
		if(TextUtils.isEmpty(index.name()))
			throw new AnnotationParsingException("Index name can not be empty", c);
		
		String[] columns = index.columns();
		if(columns.length == 0)
			throw new AnnotationParsingException("Index can not be empty", c);
		
		final StringBuilder sql = new StringBuilder(columns.length * 32);
		sql.append("create index idx_").append(index.name()).append(" on ")
				.append(table.value()).append('(');
		for (final String column : columns) {
			sql.append(' ').append(column).append(',');
		}
		sql.setLength(sql.length() - 1); // chop off last comma
		sql.append(')');
		return sql.toString();
	}
}
