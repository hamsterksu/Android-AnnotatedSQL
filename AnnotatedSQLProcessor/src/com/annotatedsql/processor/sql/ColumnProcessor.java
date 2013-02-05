package com.annotatedsql.processor.sql;

import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Autoincrement;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.NotNull;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Unique;
import com.annotatedsql.util.TextUtils;

public class ColumnProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static ColumnMeta create(VariableElement f) {
		String columnName = null;
		try {
			columnName = (String)f.getConstantValue();
		} catch (Exception e) {
			throw new AnnotationParsingException("Can not find column name", f);
		}
		
		Column column = f.getAnnotation(Column.class);
		boolean isPrimary = f.getAnnotation(PrimaryKey.class) != null;
		boolean isAutoIncrement = f.getAnnotation(Autoincrement.class) != null;
		boolean isNotNull = f.getAnnotation(NotNull.class) != null;
		
		Unique unique = f.getAnnotation(Unique.class);
		
		StringBuilder sql = new StringBuilder(" ");
		sql.append(columnName).append(' ').append(column.type());
		if(isPrimary){
			sql.append(" PRIMARY KEY");
		}
		if(isAutoIncrement){
			sql.append(" AUTOINCREMENT");
		}
		if(isNotNull){
			sql.append(" NOT NULL");
		}
		
		if(unique != null){
			sql.append(" UNIQUE ON CONFLICT ").append(unique.type());
		}
		String defVal = column.defVal();
		if(!TextUtils.isEmpty(defVal)){
			sql.append(" DEFAULT (").append(defVal).append(")");
		}
				
		return new ColumnMeta(columnName, isPrimary, sql.toString());
	}

	static class ColumnMeta{
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
