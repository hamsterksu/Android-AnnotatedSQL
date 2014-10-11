package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.RawJoin;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.SqlQuery;
import com.annotatedsql.ftl.ViewMeta;
import com.annotatedsql.processor.sql.view.FromParser;
import com.annotatedsql.processor.sql.view.JoinParser;
import com.annotatedsql.processor.sql.view.RawJoinParser;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class RawQueryParser extends SimpleViewParser {

    public RawQueryParser(Element c, ParserEnv parserEnv) {
        super(c, parserEnv);
    }

    public ViewMeta parse() {
        RawQuery q = f.getAnnotation(RawQuery.class);
        String name = q.value();

        if (parserEnv.containsTable(name)) {
            throw new AnnotationParsingException(String.format("Table/View/Query with name '%s' alredy defined", name), f);
        }

        viewMeta = new ViewMeta(f.getSimpleName().toString(), name);

        List<? extends Element> fields = f.getEnclosedElements();

        select = new StringBuilder(fields.size() * 32);
        sql = new StringBuilder(fields.size() * 32);
        sql.append("SELECT ");

        final int pos = sql.length();

        From from = null;
        SqlQuery sqlQuery = null;
        for (Element f : fields) {
            sqlQuery = f.getAnnotation(SqlQuery.class);
            if (sqlQuery != null) {
                sql.setLength(0);
                String sqlText = (String) ((VariableElement) f).getConstantValue();
                sql.append(sqlText);
                break;
            }

            From tmpFrom = f.getAnnotation(From.class);
            if (tmpFrom != null) {
                if (from != null) {
                    throw new AnnotationParsingException("Dublicate @From annotation", f);
                }
                handleFromResult(new FromParser(parserEnv, this, f).parse(), true, pos);
                from = tmpFrom;
            } else {
                Join join = f.getAnnotation(Join.class);
                if (join != null) {
                    handleFromResult(new JoinParser(parserEnv, this, f).parse(), false, pos);
                }
                RawJoin rawJoin = f.getAnnotation(RawJoin.class);
                if (rawJoin != null) {
                    if (join != null) {
                        throw new AnnotationParsingException("element can have only one join", f);
                    }
                    handleFromResult(new RawJoinParser(parserEnv, this, f).parse(), false, pos);
                }
            }
        }
        if (from == null && sqlQuery == null) {
            throw new AnnotationParsingException("Query doesn't have @From/@SqlQuery annotation", f);
        }

        if (sqlQuery == null) {
            sql.insert(pos, select.toString());
        }

        viewMeta.setSql(sql.toString());
        addColumns2Env();
        return viewMeta;
    }

}
