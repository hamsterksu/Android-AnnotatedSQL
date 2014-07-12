package com.annotatedsql.processor.sql.view;

import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.ftl.ColumnMeta;
import com.annotatedsql.processor.sql.SimpleViewParser;

import java.util.List;

import javax.lang.model.element.Element;

public class FromParser extends ExcludeStaticWhereViewParser<FromResult, From>{

	public FromParser(ParserEnv parserEnv, SimpleViewParser parentParser, Element f) {
		super(parserEnv, parentParser, f, true);
	}

	@Override
	public FromResult parse() {
		List<ColumnMeta> columns = parseColumns();
		return new FromResult(aliasName, " FROM " + tableName + " AS " + aliasName, toSqlSelect(columns), columns, getExcludeStaticWhere());
	}

	@Override
	public Class<From> getAnnotationClass() {
		return From.class;
	}

	@Override
	public String parseTableName() {
		return annotation.value();
	}

}
