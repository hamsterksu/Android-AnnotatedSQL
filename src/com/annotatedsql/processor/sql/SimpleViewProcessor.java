package com.annotatedsql.processor.sql;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.SimpleView;

public class SimpleViewProcessor {

	/**
	 * @throws AnnotationParsingException
	 */
	public static String create(Element c, Map<String, List<String>> tableColumns) {
		SimpleView view = c.getAnnotation(SimpleView.class);
		String name = view.value();

		List<? extends Element> fields = c.getEnclosedElements();
		
		final StringBuilder select = new StringBuilder(fields.size() * 32);
		final StringBuilder sql = new StringBuilder(fields.size() * 32);
		sql.append("CREATE VIEW ").append(name).append(" AS SELECT ");
		
		final int pos = sql.length();
		
		From from = null;
		for (Element f : fields) {
			From tmpFrom = f.getAnnotation(From.class);
			if (tmpFrom != null){
				if(from != null){
					throw new AnnotationParsingException("Dublicate @From annotation", f);
				}
				from = tmpFrom;
				
				String asName = (String)((VariableElement)f).getConstantValue();
				addTableColumn(select, tableColumns.get(from.value()), asName, true);		
				sql.insert(pos, " FROM " + from.value() + " AS " + asName);
			}else{
				Join join = f.getAnnotation(Join.class);
				if(join != null){
					String asName = (String)((VariableElement)f).getConstantValue();
					sql.append(" JOIN ").append(join.srcTable()).append(" AS ").append(asName)
					.append(" ON ").append(asName).append('.').append(join.srcColumn())
					.append(" = ").append(join.destTable()).append('.').append(join.destColumn());
					
					addTableColumn(select, tableColumns.get(join.srcTable()), asName, false);
				}
			}
		}
		if(from == null){
			throw new AnnotationParsingException("View doesn't have @From annotation", c);
		}
		sql.insert(pos, select.toString());
		sql.setCharAt(pos, ' ');
		//sql.append(')');
		return sql.toString();
	}
	
	private static void addTableColumn(StringBuilder select, List<String> coluumns, String asName, boolean ignoreId){
		if(coluumns == null || coluumns.isEmpty())
			return;
		for(String c : coluumns){
			if(ignoreId && "_id".equals(c)){
				select.append(", ").append(asName).append('.').append(c);
			}else{
				select.append(", ").append(asName).append('.').append(c).append(" as ").append(asName).append('_').append(c);
			}
		}
	}
}
