package com.annotatedsql.processor.sql;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Columns;
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
				Columns columns = f.getAnnotation(Columns.class);
				String[] selectedColumns = columns != null ? columns.value() : null;
				
				String asName = (String)((VariableElement)f).getConstantValue();
				try{
					addTableColumn(select, tableColumns.get(from.value()), selectedColumns, asName, true);
				}catch (RuntimeException e) {
					throw new AnnotationParsingException(e.getMessage(), f);
				}
				sql.insert(pos, " FROM " + from.value() + " AS " + asName);
			}else{
				Join join = f.getAnnotation(Join.class);
				if(join != null){
					Columns columns = f.getAnnotation(Columns.class);
					String[] selectedColumns = columns != null ? columns.value() : null;
					String asName = (String)((VariableElement)f).getConstantValue();
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
					sql.append(join.srcTable()).append(" AS ").append(asName)
					.append(" ON ").append(asName).append('.').append(join.srcColumn())
					.append(" = ").append(join.destTable()).append('.').append(join.destColumn());
					try{
						addTableColumn(select, tableColumns.get(join.srcTable()), selectedColumns, asName, false);
					}catch (RuntimeException e) {
						throw new AnnotationParsingException(e.getMessage(), f);
					}
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
	
	private static void addTableColumn(StringBuilder select, List<String> coluumns, String[] selectedColumns, String asName, boolean ignoreId){
		if(coluumns == null || coluumns.isEmpty())
			return;
		if(selectedColumns != null && selectedColumns.length != 0){
			for(String selCol : selectedColumns){
				if(!coluumns.contains(selCol)){
					throw new RuntimeException("Table doesn't have column with name '" + selCol + "'");
				}
				addColumn(select, asName, ignoreId, selCol);
			}
		}else{
			for(String c : coluumns){
				addColumn(select, asName, ignoreId, c);
			}
		}
	}

	private static void addColumn(StringBuilder select, String asName, boolean ignoreId, String c) {
		if(ignoreId && "_id".equals(c)){
			select.append(", ").append(asName).append('.').append(c);
		}else{
			select.append(", ").append(asName).append('.').append(c).append(" as ").append(asName).append('_').append(c);
		}
	}
	
	
}
