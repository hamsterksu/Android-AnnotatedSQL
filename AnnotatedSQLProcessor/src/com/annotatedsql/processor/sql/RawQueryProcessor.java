package com.annotatedsql.processor.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.RawJoin;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.SqlQuery;

public class RawQueryProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static String create(Element c, Map<String, List<String>> tableColumns) {
		HashMap<String, Element> aliases = new HashMap<String, Element>();
		ArrayList<String> selectColumns = new ArrayList<String>();
		
		RawQuery q = c.getAnnotation(RawQuery.class);
		String name = q.value();
		if(tableColumns.containsKey(name)){
			throw new AnnotationParsingException(String.format("Table/View/Query with name '%s' alredy defined", name), c);
		}
		tableColumns.put(name, selectColumns);
		
		List<? extends Element> fields = c.getEnclosedElements();
		
		final StringBuilder select = new StringBuilder(fields.size() * 32);
		final StringBuilder sql = new StringBuilder(fields.size() * 32);
		sql.append("SELECT ");
		
		final int pos = sql.length();
		
		From from = null;
		SqlQuery sqlQuery = null;
		for (Element f : fields) {
			sqlQuery = f.getAnnotation(SqlQuery.class);
			if(sqlQuery != null){
				sql.setLength(0);
				String sqlText = (String)((VariableElement)f).getConstantValue();
				sql.append(sqlText);
				break;
			}
			
			From tmpFrom = f.getAnnotation(From.class);
			if (tmpFrom != null){
				from = SimpleViewProcessor.proceedFrom(tableColumns, aliases, select, sql, pos, from, f, tmpFrom, selectColumns);
			}else{
				Join join = f.getAnnotation(Join.class);
				if(join != null){
					SimpleViewProcessor.proceedJoin(tableColumns, aliases, select, sql, f, join, selectColumns);
				}
				RawJoin rawJoin = f.getAnnotation(RawJoin.class);
				if(rawJoin != null){
					if(join != null)
						throw new AnnotationParsingException("element can have only one join", f);
					SimpleViewProcessor.proceedRawJoin(tableColumns, aliases, select, sql, f, rawJoin, selectColumns);
				}
			}
		}
		if(from == null && sqlQuery == null){
			throw new AnnotationParsingException("Query doesn't have @From/@SqlQuery annotation", c);
		}
		if(sqlQuery == null){
			sql.insert(pos, select.toString());
			sql.setCharAt(pos, ' ');
		}
		return sql.toString();
	}
}
