package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.RawJoin;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.ftl.ColumnMeta;
import com.annotatedsql.ftl.TableColumns;
import com.annotatedsql.ftl.ViewMeta;
import com.annotatedsql.ftl.ViewMeta.ViewTableInfo;
import com.annotatedsql.processor.sql.view.FromParser;
import com.annotatedsql.processor.sql.view.FromResult;
import com.annotatedsql.processor.sql.view.JoinParser;
import com.annotatedsql.processor.sql.view.RawJoinParser;
import com.annotatedsql.util.Where;

import java.util.HashMap;
import java.util.List;

import javax.lang.model.element.Element;

public class SimpleViewParser{

	protected final HashMap<String, Element> aliases = new HashMap<String, Element>();
	protected final Element f;
	protected final ParserEnv parserEnv;
	
	protected ViewMeta viewMeta;
	protected StringBuilder select;
	protected StringBuilder sql;
	
	public SimpleViewParser(Element c, ParserEnv parserEnv){
		this.f = c;
		this.parserEnv = parserEnv;
	}
	
	public ViewMeta parse(){
		SimpleView view = f.getAnnotation(SimpleView.class);
		String name = view.value();

		if(parserEnv.containsTable(name)){
			throw new AnnotationParsingException(String.format("Table/View with name '%s' alredy defined", name), f);
		}
		
		viewMeta = new ViewMeta(f.getSimpleName().toString(), name);
		
		List<? extends Element> fields = f.getEnclosedElements();
		
		select = new StringBuilder(fields.size() * 32);
		sql = new StringBuilder(fields.size() * 32);
		sql.append("CREATE VIEW ").append(name).append(" AS SELECT ");
		
		final int startSqlPos = sql.length();

		From from = null;
        FromResult fromResult = null;
		for (Element f : fields) {
			From tmpFrom = f.getAnnotation(From.class);
			if (tmpFrom != null){
				if(from != null){
					throw new AnnotationParsingException("Duplicate @From annotation", f);
				}
				handleFromResult(fromResult = new FromParser(parserEnv, this, f).parse(), true, startSqlPos);
				from = tmpFrom;
			}else{
				Join join = f.getAnnotation(Join.class);
				if(join != null){
					handleFromResult(new JoinParser(parserEnv, this, f).parse(), false, startSqlPos);
				}
				RawJoin rawJoin = f.getAnnotation(RawJoin.class);
				if(rawJoin != null){
					if(join != null){
						throw new AnnotationParsingException("element can have only one join", f);
					}
					handleFromResult(new RawJoinParser(parserEnv, this, f).parse(), false, startSqlPos);
				}
			}
		}
		if(from == null){
			throw new AnnotationParsingException("View doesn't have @From annotation", f);
		}
		sql.insert(startSqlPos, select.toString());
		//sql.setCharAt(startSqlPos, ' ');
		Where where = parserEnv.getTableWhere(from.value());
        if(where != null){
            sql.append(" where ").append(where.copy(fromResult.getAliasName()).getAsCondition());
        }
		viewMeta.setSql(sql.toString());
		addColumns2Env();
		return viewMeta;
	}
	
	protected void addColumns2Env(){
		TableColumns columns = new TableColumns(f.getSimpleName().toString(), true);
		for(ViewTableInfo t : viewMeta.getTables()){
			for(ColumnMeta c : t.getColumns()){
				columns.add(t.getName(), c.variableName, c.alias);
			}
		}
		parserEnv.addTable(viewMeta.getViewName(), columns);
	}
	
	protected void handleFromResult(FromResult result, boolean toHead, int startSqlPos){
		if(result == null)
			return;
		if(result.getColumns() != null){
			viewMeta.addTable(new ViewTableInfo(result.getAliasName(), result.getColumns()), toHead);
			if(toHead && select.length() > 0){
				select.insert(0, result.getSelectSql() + ", ");
			}else{
				if(select.length() > 0){
					select.append(',');
				}
				select.append(result.getSelectSql());
			}
		}
		if(toHead && sql.length() > 0){
			sql.insert(startSqlPos, result.getSql());
		}else{
			sql.append(result.getSql());
		}
	}
	
	public Element getAliasElement(String aliasName) {
		return aliases.get(aliasName);
	}
	
	public void regAlias(String aliasName, Element e) {
		aliases.put(aliasName, e);
	}

	public String getClassName() {
		return f.getSimpleName().toString();
	}

}
