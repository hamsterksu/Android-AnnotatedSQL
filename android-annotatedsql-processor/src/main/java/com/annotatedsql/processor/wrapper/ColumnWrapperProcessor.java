package com.annotatedsql.processor.wrapper;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.ftl.ColumnWrapperMeta;
import com.annotatedsql.processor.ProcessorLogger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by jbanse on 26/09/2014.
 */
public class ColumnWrapperProcessor {

    /**
     * @throws com.annotatedsql.AnnotationParsingException
     */
    public static ColumnWrapperMeta create(ProcessingEnvironment env, VariableElement f, ProcessorLogger logger) {
        logger.i("create ColumnWrapperMeta");
        String columnName;
        try {
            columnName = f.toString();
        } catch (Exception e) {
            throw new AnnotationParsingException("Can not find column name", f);
        }
        Column column = f.getAnnotation(Column.class);

        TypeMirror annotationClassField = null;
        try {
            column.javaClass();
        } catch (MirroredTypeException e) {
            annotationClassField = e.getTypeMirror();
            logger.i("mirror type=" + e);
        }
        ColumnWrapperMeta lColumnWrapperMeta;
        if (annotationClassField.getKind() == TypeKind.DECLARED) {
            final String declaringClass = env.getTypeUtils().asElement(annotationClassField).getSimpleName().toString();
            lColumnWrapperMeta = new ColumnWrapperMeta(columnName, declaringClass);
        } else {
            logger.i("find type=" + String.valueOf(annotationClassField));
            lColumnWrapperMeta = new ColumnWrapperMeta(columnName, annotationClassField.toString());
        }

        return lColumnWrapperMeta;
    }

}
