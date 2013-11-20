package com.annotatedsql.processor.sql.view;

import java.util.List;

import javax.lang.model.element.Element;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.RawJoin;
import com.annotatedsql.ftl.ColumnMeta;
import com.annotatedsql.processor.sql.SimpleViewParser;
import com.annotatedsql.util.TextUtils;

public class RawJoinParser extends ViewTableColumnParser<FromResult, RawJoin> {

	public RawJoinParser(ParserEnv parserEnv, SimpleViewParser parentParser,
			Element f) {
		super(parserEnv, parentParser, f, false);
	}

	@Override
	public FromResult parse() {
		if(TextUtils.isEmpty(annotation.onCondition())){
			throw new AnnotationParsingException("'ON' condition is empty", field);
		}
		StringBuilder sql = new StringBuilder();
		switch (annotation.type()) {
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
		
		sql.append(annotation.joinTable()).append(" AS ").append(aliasName)
			.append(" ON ").append(annotation.onCondition());
		
		List<ColumnMeta> columns = parseColumns();
		return new FromResult(aliasName, sql.toString(), toSqlSelect(columns), columns);
	}

	@Override
	public Class<RawJoin> getAnnotationClass() {
		return RawJoin.class;
	}

	@Override
	public String parseTableName() {
		return annotation.joinTable();
	}
}
