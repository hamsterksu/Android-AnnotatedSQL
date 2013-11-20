package com.annotatedsql.processor.sql;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.processor.sql.ColumnProcessor.ColumnMeta;

public class TableParser{

	private final Element c;
	private final ParserEnv parserEnv;
	
	public TableParser(Element c, ParserEnv parserEnv){
		this.c = c;
		this.parserEnv = parserEnv;
	}
	
	public TableResult parse() {
		TableColumns tableColumns = new TableColumns(c.getSimpleName().toString(), false); 
		
		Table table = c.getAnnotation(Table.class);
		String name = table.value();
		
		if(parserEnv.containsTable(name)){
			throw new AnnotationParsingException(String.format("Table/View with name '%s' alredy defined", name), c);
		}
		
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
			tableColumns.add(f.getSimpleName().toString(), meta.name);
			hasPrimaryKey |= meta.isPrimary;
			sql.append(',').append(meta.sql);
			columnCount++;
		}
		if(columnCount == 0)
			throw new AnnotationParsingException("Table doesn't have columns", c);
		
		PrimaryKey pk = (PrimaryKey)c.getAnnotation(PrimaryKey.class);
		if(pk != null && pk.columns().length > 0){
			if(hasPrimaryKey){
				throw new AnnotationParsingException("Table has more that one PRIMARY KEY", c);
			}
			proceedPk(sql, pk.columns());
		}
		sql.setCharAt(pos, '(');
        sql.append(')');
        
        parserEnv.addTable(name, tableColumns);
        return new TableResult(sql.toString(), tableColumns.toColumnsList());
	}

	private static void proceedPk(final StringBuilder sql, String[] columns){
		sql.append(", PRIMARY KEY(");
		for (final String column : columns) {
			sql.append(' ').append(column).append(',');
		}
		sql.setLength(sql.length() - 1);
		sql.append(")");
	}
}
