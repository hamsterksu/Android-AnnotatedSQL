package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.CursorWrapperMeta;
import com.annotatedsql.ftl.IndexMeta;
import com.annotatedsql.ftl.SchemaMeta;
import com.annotatedsql.ftl.TableMeta;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.processor.wrapper.WrapperParser;
import com.annotatedsql.processor.wrapper.WrapperResult;
import com.annotatedsql.util.TextUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SupportedAnnotationTypes({"com.annotatedsql.annotation.sql.Schema"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SQLProcessor extends AbstractProcessor {

    private ProcessorLogger logger;
    private Configuration cfg = new Configuration();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        logger = new ProcessorLogger(processingEnv.getMessager());
        logger.i("SQLProcessor started");
        if (annotations == null || annotations.size() == 0) {
            return false;
        }
        cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/res"));
        try {
            return processTable(roundEnv);
        } catch (AnnotationParsingException e) {
            logger.e(e.getMessage(), e.getElements());
            return false;
        }
    }

    private boolean processTable(RoundEnvironment roundEnv) {
        logger.i("SQLProcessor processTable");

        SchemaMeta schema;
        Set<? extends Element> schemaElements = roundEnv.getElementsAnnotatedWith(Schema.class);
        if (schemaElements != null && schemaElements.size() != 0) {
            if (schemaElements.size() != 1) {
                for (Element e : schemaElements) {
                    logger.e("Please use one schema file", e);
                }
                return false;
            } else {
                Element e = schemaElements.iterator().next();
                Schema schemaElement = e.getAnnotation(Schema.class);
                if (TextUtils.isEmpty(schemaElement.className())) {
                    logger.e("Schema name can't be empty", e);
                    return false;
                }
                schema = new SchemaMeta(e.getSimpleName().toString(), schemaElement.className(), e.getSimpleName().toString());
                schema.setDbName(schemaElement.dbName());
                schema.setDbVersion(schemaElement.dbVersion());

                PackageElement pkg = (PackageElement) e.getEnclosingElement();
                String pkgName = pkg.getQualifiedName().toString();
                logger.i("SQLProcessor pkgName found: " + pkgName);
                schema.setPkgName(pkgName);

            }
        } else {
            return false;
        }

        //create abstract cursor wrapper
        processAbstractCursorWrapper(schema);

        ParserEnv parserEnv = new ParserEnv(schema.getStoreClassName());
        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }
            logger.i("SQLProcessor table found: " + element.getSimpleName());
            TypeElement typeElement = (TypeElement) element;
            Table table = element.getAnnotation(Table.class);
            TableResult tableInfo = new TableParser(typeElement, parserEnv, logger).parse(processingEnv);

            schema.addTable(new TableMeta(table.value(), tableInfo.getSql()));

            List<IndexMeta> indexes = TableParser.proceedIndexes(typeElement);
            if (indexes != null) {
                for (IndexMeta i : indexes) {
                    schema.addIndex(i);
                }
            }

            WrapperResult lWrapperResult = new WrapperParser(typeElement, parserEnv, logger).parse(processingEnv);

            CursorWrapperMeta lCursorWrapperMeta = new CursorWrapperMeta(schema.getPkgName(), table.value(), element.getSimpleName().toString(), lWrapperResult.getColumns(), lWrapperResult.getColumnsToType());
            processCursorWrapper(lCursorWrapperMeta);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)) {
            logger.i("SQLProcessor simple view found: " + element.getSimpleName());
            schema.addView(new SimpleViewParser(element, parserEnv).parse());
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(RawQuery.class)) {
            logger.i("SQLProcessor raw query found: " + element.getSimpleName());
            schema.addQuery(new RawQueryParser(element, parserEnv).parse());
        }
        if (schema.isEmpty()) {
            return false;
        }
        processSchema(schema);
        processSchemaExt(schema);
        return true;
    }

    private void processSchema(SchemaMeta model) {
        processTemplateForModel(model, "schema.ftl", null);
    }

    private void processSchemaExt(SchemaMeta model) {
        processTemplateForModel(model, "schema_extend.ftl", "2");
    }

    private void processTemplateForModel(SchemaMeta model, String templateName, String postfix) {
        JavaFileObject file;
        try {
            file = processingEnv.getFiler().createSourceFile(model.getPkgName() + "." + model.getClassName() + (postfix == null ? "" : postfix));
            logger.i("Creating file:  " + model.getPkgName() + "." + file.getName());
            Writer out = file.openWriter();
            Template t = cfg.getTemplate(templateName);
            t.process(model, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.e("EntityProcessor IOException: ", e);
        } catch (TemplateException e) {
            logger.e("EntityProcessor TemplateException: ", e);
        }
    }

    private void processAbstractCursorWrapper(SchemaMeta model) {
        JavaFileObject file;
        try {
            file = processingEnv.getFiler().createSourceFile(model.getPkgName() + ".AbstractCursorWrapper");
            logger.i("Creating file:  " + model.getPkgName() + "." + file.getName());
            Writer out = file.openWriter();
            Template t = cfg.getTemplate("abstractcursorwrapper.ftl");
            t.process(model, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.e("EntityProcessor IOException: ", e);
        } catch (TemplateException e) {
            logger.e("EntityProcessor TemplateException: ", e);
        }
    }

    private void processCursorWrapper(CursorWrapperMeta tableMeta) {
        JavaFileObject file;
        try {
            file = processingEnv.getFiler().createSourceFile(tableMeta.getPkgName() + "." + tableMeta.getCursorWrapperName());
            logger.i("Creating file:  " + tableMeta.getPkgName() + "." + tableMeta.getCursorWrapperName());
            Writer out = file.openWriter();
            Template t = cfg.getTemplate("cursor_wrapper.ftl");
            t.process(tableMeta, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.e("EntityProcessor IOException: ", e);
        } catch (TemplateException e) {
            logger.e("EntityProcessor TemplateException: ", e);
        }
    }

}
