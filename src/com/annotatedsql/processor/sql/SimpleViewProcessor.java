package com.annotatedsql.processor.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Columns;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.RawJoin;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.util.TextUtils;

public class SimpleViewProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static String create(Element c, Map<String, List<String>> tableColumns) {
		HashMap<String, Element> aliases = new HashMap<String, Element>();
		ArrayList<String> selectColumns = new ArrayList<String>();
		
		SimpleView view = c.getAnnotation(SimpleView.class);
		String name = view.value();
		if(tableColumns.containsKey(name)){
			throw new AnnotationParsingException(String.format("Table/View with name '%s' alredy defined", name), c);
		}
		tableColumns.put(name, selectColumns);
		
		List<? extends Element> fields = c.getEnclosedElements();
		
		final StringBuilder select = new StringBuilder(fields.size() * 32);
		final StringBuilder sql = new StringBuilder(fields.size() * 32);
		sql.append("CREATE VIEW ").append(name).append(" AS SELECT ");
		
		final int pos = sql.length();
		
		From from = null;
		for (Element f : fields) {
			From tmpFrom = f.getAnnotation(From.class);
			if (tmpFrom != null){
				from = proceedFrom(tableColumns, aliases, select, sql, pos, from, f, tmpFrom, selectColumns);
			}else{
				Join join = f.getAnnotation(Join.class);
				if(join != null){
					proceedJoin(tableColumns, aliases, select, sql, f, join, selectColumns);
				}
				RawJoin rawJoin = f.getAnnotation(RawJoin.class);
				if(rawJoin != null){
					if(join != null)
						throw new AnnotationParsingException("element can have only one join", f);
					proceedRawJoin(tableColumns, aliases, select, sql, f, rawJoin, selectColumns);
				}
			}
		}
		if(from == null){
			throw new AnnotationParsingException("View doesn't have @From annotation", c);
		}
		sql.insert(pos, select.toString());
		sql.setCharAt(pos, ' ');
		return sql.toString();
	}

	public static void proceedJoin(Map<String, List<String>> tableColumns,
			HashMap<String, Element> aliases, final StringBuilder select,
			final StringBuilder sql, Element f, Join join,  ArrayList<String> selectColumns) {
		Columns columns = f.getAnnotation(Columns.class);
		String[] selectedColumns = columns != null ? columns.value() : null;
		String asName = (String)((VariableElement)f).getConstantValue();
		checkAlias(aliases, f, asName);
		switch (join.type()) {
			case INNER:
				sql.append(" JOIN ");
				break;
			case LEFT:
				sql.append(" LEFT OUTER JOIN ");
				break;
			case RIGHT:
				sql.append(" RIGHT OUTER JOIN ");
				break;
			case CROSS:
				sql.append(" CROSS JOIN ");
				break;
		}
		checkColumn(f, join.joinTable(), tableColumns.get(join.joinTable()), join.joinColumn());
		//checkColumn(f, join.onTable(), tableColumns.get(join.onTable()), join.onColumn());
		sql.append(join.joinTable()).append(" AS ").append(asName)
		.append(" ON ").append(asName).append('.').append(join.joinColumn())
		.append(" = ").append(join.onTable()).append('.').append(join.onColumn());
		try{
			addTableColumn(select, tableColumns.get(join.joinTable()), selectedColumns, asName, false, selectColumns);
		}catch (RuntimeException e) {
			throw new AnnotationParsingException(e.getMessage(), f);
		}
	}
	
	public static void proceedRawJoin(Map<String, List<String>> tableColumns,
			HashMap<String, Element> aliases, final StringBuilder select,
			final StringBuilder sql, Element f, RawJoin join,  ArrayList<String> selectColumns) {
		Columns columns = f.getAnnotation(Columns.class);
		String[] selectedColumns = columns != null ? columns.value() : null;
		String asName = (String)((VariableElement)f).getConstantValue();
		checkAlias(aliases, f, asName);
		if(TextUtils.isEmpty(join.onCondition())){
			throw new AnnotationParsingException("'ON' condition is empty", f);
		}
		switch (join.type()) {
			case INNER:
				sql.append(" JOIN ");
				break;
			case LEFT:
				sql.append(" LEFT OUTER JOIN ");
				break;
			case RIGHT:
				sql.append(" RIGHT OUTER JOIN ");
				break;
			case CROSS:
				sql.append(" CROSS JOIN ");
				break;
		}
		sql.append(join.joinTable()).append(" AS ").append(asName).append(" ON ").append(join.onCondition());
		try{
			addTableColumn(select, tableColumns.get(join.joinTable()), selectedColumns, asName, false, selectColumns);
		}catch (RuntimeException e) {
			throw new AnnotationParsingException(e.getMessage(), f);
		}
	}

	public static From proceedFrom(Map<String, List<String>> tableColumns,
			HashMap<String, Element> aliases, final StringBuilder select,
			final StringBuilder sql, final int pos, From from, Element f,
			From tmpFrom, ArrayList<String> selectColumns) {
		if(from != null){
			throw new AnnotationParsingException("Dublicate @From annotation", f);
		}
		from = tmpFrom;
		Columns columns = f.getAnnotation(Columns.class);
		String[] selectedColumns = columns != null ? columns.value() : null;
		
		String asName = (String)((VariableElement)f).getConstantValue();
		try{
			addTableColumn(select, tableColumns.get(from.value()), selectedColumns, asName, true, selectColumns);
		}catch (RuntimeException e) {
			throw new AnnotationParsingException(e.getMessage(), f);
		}
		checkAlias(aliases, f, asName);
		sql.insert(pos, " FROM " + from.value() + " AS " + asName);
		return from;
	}
	
	public static void addTableColumn(StringBuilder select, List<String> coluumns, String[] selectedColumns, String asName, boolean ignoreId, ArrayList<String> selectColumns){
		if(coluumns == null || coluumns.isEmpty())
			return;
		if(selectedColumns != null && selectedColumns.length != 0){
			for(String selCol : selectedColumns){
				if(!coluumns.contains(selCol)){
					throw new RuntimeException("Table doesn't have column '" + selCol + "'");
				}
				addColumn(select, asName, ignoreId, selCol, selectColumns);
			}
		}else{
			for(String c : coluumns){
				addColumn(select, asName, ignoreId, c, selectColumns);
			}
		}
	}

	public static void addColumn(StringBuilder select, String asName, boolean ignoreId, String c, ArrayList<String> selectColumns) {
		if(ignoreId && "_id".equals(c)){
			selectColumns.add(c);
			select.append(", ").append(asName).append('.').append(c);
		}else{
			String fullName = asName + '_' + c;
			selectColumns.add(fullName);
			select.append(", ").append(asName).append('.').append(c).append(" as ").append(fullName);
		}
	}
	
	public static void checkColumn(final Element e, final String tableName, final List<String> coluumns, final String column){
		if(coluumns == null || coluumns.isEmpty() || TextUtils.isEmpty(column) || !coluumns.contains(column))
			throw new AnnotationParsingException(String.format("Column '%s' doesn't exist in table '%s'", column, tableName), e);
	}
	
	public static void checkAlias(HashMap<String, Element> aliases, Element e, String alias){
		Element aliasElement = aliases.get(alias);
		if(aliasElement != null)
			throw new AnnotationParsingException(String.format("Duplicate alias '%s'", alias), e, aliasElement);
		aliases.put(alias, e);
	}
	
	
}
