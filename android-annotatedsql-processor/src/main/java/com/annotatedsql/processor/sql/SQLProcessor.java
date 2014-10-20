package com.annotatedsql.processor.sql;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.IndexMeta;
import com.annotatedsql.ftl.SchemaMeta;
import com.annotatedsql.ftl.TableMeta;
import com.annotatedsql.ftl.ViewMeta;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.processor.logger.ILogger;
import com.annotatedsql.processor.logger.TagLogger;
import com.annotatedsql.util.TextUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
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
@SupportedOptions({ProcessorLogger.ARG_LOG_LEVEL, SQLProcessor.ARG_PLUGINS})
public class SQLProcessor extends AbstractProcessor {

    public static final String ARG_PLUGINS = "plugins";

    private TagLogger logger;
    private Configuration cfg = new Configuration();
    private List<ISchemaPlugin> plugins = new ArrayList<ISchemaPlugin>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new TagLogger("SQLProcessor", new ProcessorLogger(processingEnv.getMessager(), processingEnv.getOptions()));
        logger.i("init");

        cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/res"));
        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            logger.i("init.options size = " + options.size());
            for (Entry<String, String> e : options.entrySet()) {
                logger.i("init.options " + e.getKey() + " = " + e.getValue());
            }
            if (options.containsKey(ARG_PLUGINS)) {
                registerPlugins(processingEnv, options.get(ARG_PLUGINS));
            }
        } else {
            logger.i("init.options EMPTY");
        }
    }

    private void registerPlugins(ProcessingEnvironment processingEnv, String plugins) {
        if (TextUtils.isEmpty(plugins)) {
            return;
        }
        String[] ar = plugins.split(" ");
        for (String s : ar) {
            try {
                Class<?> clazz = Class.forName(s);
                if (!ISchemaPlugin.class.isAssignableFrom(clazz)) {
                    logger.e("plugin " + s + " should extends ISchemaPlugin");
                    continue;
                }
                logger.i("plugin " + s + " .newInstance");
                ISchemaPlugin plugin = (ISchemaPlugin) clazz.newInstance();
                logger.i("plugin " + s + " .newInstance end");
                plugin.init(processingEnv, logger.getLogger());
                logger.i("plugin " + s + " .init end");
                this.plugins.add(plugin);
                logger.i("plugin " + s + " added");
            } catch (ClassNotFoundException e) {
                logger.e("Can't find plugin class: " + s, e);
                continue;
            } catch (InstantiationException e) {
                logger.e("Can't instantiate plugin: " + s, e);
                continue;
            } catch (IllegalAccessException e) {
                logger.e("Plugin should have open constructor: " + s, e);
                continue;
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger.i("started");
        if (annotations == null || annotations.size() == 0) {
            logger.i("no annotations");
            return false;
        }
        try {
            return processTable(roundEnv);
        } catch (AnnotationParsingException e) {
            logger.e(e.getMessage(), e.getElements());
            return false;
        }
    }

    private boolean processTable(RoundEnvironment roundEnv) {
        logger.i("processTable");

        SchemaMeta schema;
        TypeElement schemaElement;
        Set<? extends Element> schemaElements = roundEnv.getElementsAnnotatedWith(Schema.class);
        if (schemaElements != null && schemaElements.size() != 0) {
            if (schemaElements.size() != 1) {
                for (Element e : schemaElements) {
                    logger.e("Please use one schema file", e);
                }
                return false;
            } else {
                Element se = schemaElements.iterator().next();
                if (!(se instanceof TypeElement)) {
                    logger.e("Schema should be interface or class", se);
                    return false;
                }
                schemaElement = (TypeElement) se;
                Schema schemaAnnotation = schemaElement.getAnnotation(Schema.class);
                if (TextUtils.isEmpty(schemaAnnotation.className())) {
                    logger.e("Schema name can't be empty", schemaElement);
                    return false;
                }
                schema = new SchemaMeta(schemaElement.getSimpleName().toString(), schemaAnnotation.className(), schemaElement.getSimpleName().toString());
                schema.setDbName(schemaAnnotation.dbName());
                schema.setDbVersion(schemaAnnotation.dbVersion());

                PackageElement pkg = (PackageElement) schemaElement.getEnclosingElement();
                String pkgName = pkg.getQualifiedName().toString();
                logger.i("pkgName found: " + pkgName);
                schema.setPkgName(pkgName);

            }
        } else {
            return false;
        }

        ParserEnv parserEnv = new ParserEnv(schema.getStoreClassName());

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (!(element instanceof TypeElement))
                continue;
            logger.i("table found: " + element.getSimpleName());
            TypeElement typeElement = (TypeElement) element;

            TableResult tableInfo = new TableParser(typeElement, parserEnv, logger).parse();

            schema.addTable(new TableMeta(tableInfo.getTableName(), tableInfo.getSql()));

            List<IndexMeta> indexes = TableParser.proceedIndexes(typeElement);
            if (indexes != null) {
                for (IndexMeta i : indexes) {
                    schema.addIndex(i);
                }
            }
            processTableInPlugins(typeElement, tableInfo);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)) {
            if (!(element instanceof TypeElement))
                continue;
            TypeElement typeElement = (TypeElement) element;
            logger.i("simple view found: " + element.getSimpleName());
            ViewMeta viewMeta = new SimpleViewParser(typeElement, parserEnv).parse();
            schema.addView(viewMeta);
            processViewInPlugins(typeElement, viewMeta);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(RawQuery.class)) {
            if (!(element instanceof TypeElement))
                continue;
            TypeElement typeElement = (TypeElement) element;
            logger.i("raw query found: " + element.getSimpleName());
            ViewMeta rawMeta = new RawQueryParser(typeElement, parserEnv).parse();
            schema.addQuery(rawMeta);
            processRawQueryInPlugins(typeElement, rawMeta);
        }
        if (schema.isEmpty()) {
            return false;
        }
        processSchema(schema);
        processSchemaExt(schema);
        processSchemaInPlugins(schemaElement, schema);
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


    private void processSchemaInPlugins(TypeElement element, SchemaMeta model) {
        logger.i("processSchemaInPlugins");
        for (ISchemaPlugin plugin : plugins) {
            plugin.processSchema(element, model);
        }
        logger.i("processSchemaInPlugins end");
    }

    private void processTableInPlugins(TypeElement element, TableResult tableInfo) {
        logger.i("processTableInPlugins");
        for (ISchemaPlugin plugin : plugins) {
            plugin.processTable(element, tableInfo);
        }
        logger.i("processTableInPlugins end");
    }

    private void processViewInPlugins(TypeElement element, ViewMeta tableInfo) {
        logger.i("processViewInPlugins");
        for (ISchemaPlugin plugin : plugins) {
            plugin.processView(element, tableInfo);
        }
        logger.i("processViewInPlugins end");
    }

    private void processRawQueryInPlugins(TypeElement element, ViewMeta tableInfo) {
        for (ISchemaPlugin plugin : plugins) {
            plugin.processRawQuery(element, tableInfo);
        }
    }
}
