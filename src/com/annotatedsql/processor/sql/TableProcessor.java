package com.annotatedsql.processor.sql;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.processor.sql.ColumnProcessor.ColumnMeta;

public class TableProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static TableInfo create(Element c){
		TableInfo tableInfo = new TableInfo();
		
		Table table = c.getAnnotation(Table.class);
		String name = table.value();
		
		List<? extends Element> fields = c.getEnclosedElements();
		final StringBuilder sql = new StringBuilder(fields.size() * 32);
        sql.append("create table ").append(name);
        final int pos = sql.length();
        
        int columnCount = 0;
        boolean hasPrimaryKey = false;
		for(Element f : fields){
			Column column = f.getAnnotation(Column.class);
			if(column == null)
				continue;
			ColumnMeta meta = ColumnProcessor.create((VariableElement)f);
			tableInfo.addColumn(meta.name);
			hasPrimaryKey |= meta.isPrimary;
			sql.append(',').append(meta.sql);
			columnCount++;
		}
		if(columnCount == 0)
			throw new AnnotationParsingException("Table doesn't have columns", c);
		
		PrimaryKey pk = (PrimaryKey)c.getAnnotation(PrimaryKey.class);
		if(pk != null && pk.collumns().length > 0){
			if(hasPrimaryKey){
				throw new AnnotationParsingException("Table has more that one PRIMARY KEY", c);
			}
			proceedPk(sql, pk.collumns());
		}
		sql.setCharAt(pos, '(');
        sql.append(')');
        
        tableInfo.setSql(sql.toString());
        return tableInfo;
	}
	
	private static void proceedPk(final StringBuilder sql, String[] columns){
		sql.append(", PRIMARY KEY(");
		for (final String column : columns) {
			sql.append(' ').append(column).append(',');
		}
		sql.setLength(sql.length() - 1);
		sql.append(")");
	}
	
	static class TableInfo{
		String sql;
		List<String> columns = new ArrayList<String>();
		
		public void setSql(String sql) {
			this.sql = sql;
		}
		
		public void addColumn(String column){
			columns.add(column);
		}
		
		public String getSql() {
			return sql;
		}
		
		public List<String> getColumns() {
			return columns;
		}
	}
}
