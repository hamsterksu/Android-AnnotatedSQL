package com.annotatedsql.processor.sql.view;

import com.annotatedsql.ParserEnv;
import com.annotatedsql.ParserResult;
import com.annotatedsql.annotation.sql.ExcludeStaticWhere;
import com.annotatedsql.processor.sql.SimpleViewParser;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * Created by hamsterksu on 02.04.14.
 */
public abstract class ExcludeStaticWhereViewParser<T extends ParserResult, A extends Annotation> extends ViewTableColumnParser<T, A>{

    public ExcludeStaticWhereViewParser(ParserEnv parserEnv, SimpleViewParser parentParser, Element f, boolean ignoreId) {
        super(parserEnv, parentParser, f, ignoreId);
    }

    public String getExcludeStaticWhere(){
        ExcludeStaticWhere excludeAnn = field.getAnnotation(ExcludeStaticWhere.class);
        return excludeAnn == null ? null : excludeAnn.value();
    }
}
