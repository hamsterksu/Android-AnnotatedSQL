package com.annotatedsql.processor.wrapper;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.ftl.TableColumnsWrapper;
import com.annotatedsql.processor.ProcessorLogger;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

public class WrapperParser {

    private final TypeElement c;
    private final ParserEnv parserEnv;
    private final ProcessorLogger logger;

    private String tableName;

    public WrapperParser(TypeElement c, ParserEnv parserEnv, ProcessorLogger logger) {
        this.logger = logger;
        this.c = c;
        this.parserEnv = parserEnv;
    }

    public WrapperResult parse(ProcessingEnvironment env) {
        TableColumnsWrapper tableColumns = new TableColumnsWrapper(c.getSimpleName().toString(), false);

        List<Element> fields = new ArrayList<Element>();
        collectParentFields(fields, Arrays.asList(c.asType()));

        int columnCount = 0;
        for (Element f : fields) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            ColumnWrapperProcessor.ColumnWrapperMeta meta = ColumnWrapperProcessor.create(env,(VariableElement) f, logger);
            tableColumns.add(f.getSimpleName().toString(), meta.name, meta.classType);
            columnCount++;
        }
        if (columnCount == 0) {
            throw new AnnotationParsingException("Table doesn't have columns", c);
        }

        parserEnv.addTable(tableName, tableColumns);

        WrapperResult tableResult = new WrapperResult(tableColumns.toColumnsList(), tableColumns.getColumn2type());
        return tableResult;
    }

    private void collectParentFields(List<Element> fields, List<? extends TypeMirror> typeMirrors) {
        if (typeMirrors == null || typeMirrors.isEmpty()) {
            return;
        }
        for (TypeMirror p : typeMirrors) {
            if (p instanceof NoType) {
                continue;
            }
            Element superClass = ((DeclaredType) p).asElement();
            List<? extends Element> inner = superClass.getEnclosedElements();
            if (inner != null) {
                fields.addAll(inner);
            }
            if (superClass instanceof TypeElement) {
                TypeElement typeElement = ((TypeElement) superClass);
                TypeMirror superclass = typeElement.getSuperclass();
                if (superclass != null) {
                    collectParentFields(fields, Arrays.asList(superclass));
                }
                collectParentFields(fields, typeElement.getInterfaces());
            }
        }
    }

    private static <A extends Annotation> void collectAnnotation(List<A> annotations, Class<A> clazz, List<? extends TypeMirror> typeMirrors) {
        if (typeMirrors == null || typeMirrors.isEmpty()) {
            return;
        }
        for (TypeMirror t : typeMirrors) {
            if (!(t instanceof DeclaredType)) {
                continue;
            }
            Element element = ((DeclaredType) t).asElement();
            A a = element.getAnnotation(clazz);
            if (a != null) {
                annotations.add(a);
            }
            if (element instanceof TypeElement) {
                TypeElement typeElement = ((TypeElement) element);
                TypeMirror superclass = typeElement.getSuperclass();
                if (superclass != null) {
                    collectAnnotation(annotations, clazz, Arrays.asList(superclass));
                }
                collectAnnotation(annotations, clazz, ((TypeElement) element).getInterfaces());
            }
        }
    }

}
