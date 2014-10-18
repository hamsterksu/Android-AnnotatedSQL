package com.annotatedsql.processor.provider;

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.ParserEnv;
import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.Providers;
import com.annotatedsql.annotation.provider.Trigger;
import com.annotatedsql.annotation.provider.Trigger.When;
import com.annotatedsql.annotation.provider.Triggers;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.provider.URI.Type;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.ProviderMeta;
import com.annotatedsql.ftl.TriggerMeta;
import com.annotatedsql.ftl.UriMeta;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.processor.sql.TableParser;
import com.annotatedsql.processor.sql.TableResult;
import com.annotatedsql.util.TextUtils;
import com.annotatedsql.util.Where;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SupportedAnnotationTypes({"com.annotatedsql.annotation.provider.Provider", "com.annotatedsql.annotation.provider.Providers"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ProviderProcessor extends AbstractProcessor {

    private final static int MATCH_TYPE_ITEM = 0x0001;
    private final static int MATCH_TYPE_DIR = 0x0002;
    private final static int MATCH_TYPE_CUSTOM = 0x0003;
    //private final static int MATCH_TYPE_MASK = 0x000f;

    private int elementCode = 0x1000;

    private ProcessorLogger logger;
    private Configuration cfg = new Configuration();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger = new ProcessorLogger(processingEnv.getMessager());
        if (annotations == null || annotations.size() == 0)
            return false;
        cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/res"));
        try {
            return processProviders(roundEnv);
        } catch (AnnotationParsingException e) {
            logger.e(e.getMessage(), e.getElements());
            return false;
        }
    }

    private boolean processProviders(RoundEnvironment roundEnv) {
        Set<? extends Element> providersElements = roundEnv.getElementsAnnotatedWith(Providers.class);
        if (providersElements != null && !providersElements.isEmpty()) {
            if (providersElements.size() != 1) {
                logger.e("Please use one @Providers", providersElements);
                return false;
            }
            Element e = providersElements.iterator().next();
            Providers ps = e.getAnnotation(Providers.class);
            if (ps == null) {
                logger.e("ps is null", e);
                return false;
            }
            Provider[] providers = ps.value();
            if (providers == null || providers.length == 0) {
                logger.e("No one provider defined in @Providers", e);
                return false;
            }
            boolean b = true;
            for (Provider p : providers) {
                b &= processProvider(roundEnv, e, p);
            }
            return b;
        } else {
            Set<? extends Element> providerElements = roundEnv.getElementsAnnotatedWith(Provider.class);
            if (providerElements == null || providerElements.isEmpty()) {
                return false;
            }
            if (providerElements.size() != 1) {
                logger.e("Please use one provider file or @Providers for the same schema", providerElements);
                return false;
            }
            Element e = providerElements.iterator().next();
            return processProvider(roundEnv, e, null);
        }
    }

    private boolean processProvider(RoundEnvironment roundEnv, Element e, Provider providerElement) {
        logger.i("processProvider start");
        if (e == null)
            return false;

        if (providerElement == null) {
            providerElement = e.getAnnotation(Provider.class);
        }
        if (TextUtils.isEmpty(providerElement.name())) {
            logger.e("Provider name can't be empty", e);
            return false;
        }
        ProviderMeta provider = new ProviderMeta(e.getSimpleName().toString(), providerElement.name());
        PackageElement pkg = (PackageElement) e.getEnclosingElement();
        provider.setPkgName(pkg.getQualifiedName().toString());
        provider.setSchemaClassName(providerElement.schemaClass());

        provider.setOpenHelperClass(providerElement.openHelperClass());

        provider.setAuthority(providerElement.authority());
        provider.setSupportTransaction(providerElement.supportTransaction());

        provider.setBulkInsertMode(providerElement.bulkInsertMode());
        provider.setInsertMode(providerElement.insertMode());

        processSchema(roundEnv, provider);

        logger.i("processProvider before generate: " + provider.getClassName());

        processTemplateForModel(provider);

        logger.i("processProvider end");
        return true;
    }

    private void processSchema(RoundEnvironment roundEnv, ProviderMeta provider) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (!(element instanceof TypeElement))
                continue;
            TableResult tableInfo = new TableParser((TypeElement) element, new ParserEnv(null), logger).parse();
            List<UriMeta> uris = processTable(element, tableInfo.getWhere());
            if (uris != null && !uris.isEmpty()) {
                provider.addImport(((TypeElement) element).getQualifiedName().toString());
                provider.addUris(uris);
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)) {
            List<UriMeta> uris = processTable(element, null);
            if (uris != null && !uris.isEmpty()) {
                provider.addImport(((TypeElement) element).getQualifiedName().toString());
                provider.addUris(uris);
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(RawQuery.class)) {
            List<UriMeta> uris = processQuery(element);
            if (uris != null && !uris.isEmpty()) {
                provider.addImport(((TypeElement) element).getQualifiedName().toString());
                provider.addUris(processQuery(element));
            }
        }
    }

    private List<UriMeta> processQuery(Element element) {
        String parentName = element.getSimpleName().toString();
        RawQuery rawQuery = element.getAnnotation(RawQuery.class);

        String from = "SQL_QUERY_" + rawQuery.value().toUpperCase();

        return processUri(element, parentName, from, null, true);
    }

    private List<UriMeta> processTable(Element element, Where where) {
        String parentName = element.getSimpleName().toString();
        String nameField = findName(element);
        if (nameField == null) {
            throw new AnnotationParsingException("Can't find table/view name. Please define field TABLE_NAME or VIEW_NAME ", element);
        }
        String from = parentName + "." + nameField;
        return processUri(element, parentName, from, where, false);
    }

    private List<UriMeta> processUri(Element element, String parentName, String from, Where builder, boolean rawQuery) {
        List<UriMeta> uris = new ArrayList<UriMeta>();
        for (Element e : element.getEnclosedElements()) {
            URI uri = e.getAnnotation(URI.class);
            if (uri == null) {
                continue;
            }

            List<TriggerMeta> triggersList = new ArrayList<TriggerMeta>();
            validateTrigger(e, triggersList, e.getAnnotation(Trigger.class));
            validateTrigger(e, triggersList, e.getAnnotation(Triggers.class));

            String pathValue = (String) ((VariableElement) e).getConstantValue();
            String path = parentName + "." + e.getSimpleName().toString();
            if (uri.type() == Type.DIR_AND_ITEM && !rawQuery) {
                uris.add(createUriMeta(Type.DIR, uri.customMimeType(), path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), triggersList, builder, rawQuery));
                uris.add(createUriMeta(Type.ITEM, uri.customMimeType(), path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), null, builder, rawQuery));
            } else {
                uris.add(createUriMeta(uri.type(), uri.customMimeType(), path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), triggersList, builder, rawQuery));
            }
        }
        return uris;
    }

    private void validateTrigger(Element e, List<TriggerMeta> triggersList, Triggers triggers) {
        if (triggers != null) {
            for (Trigger t : triggers.value()) {
                validateTrigger(e, triggersList, t);
            }
        }
    }

    private void validateTrigger(Element e, List<TriggerMeta> triggers, Trigger trigger) {
        if (trigger == null) {
            return;
        }
        String triggerMethod = trigger.name().trim();
        if (TextUtils.isEmpty(triggerMethod)) {
            throw new AnnotationParsingException("Trigger method name is empty", e);
        }
        triggers.add(new TriggerMeta(triggerMethod, trigger.type(), trigger.when() == When.BEFORE));
    }

    private UriMeta createUriMeta(Type type, String customMimeType, String path, String selectColumn, String pathValue, String from, String[] altNotify, boolean onlyQuery, List<TriggerMeta> triggers, Where builder, boolean rawQuery) {
        if (type == Type.ITEM && !pathValue.endsWith("#")) {
            if (!pathValue.endsWith("/")) {
                path += " + \"/#\"";
            } else {
                path += " + \"#\"";
            }
        }
        int typeMask = type == Type.DIR ? MATCH_TYPE_DIR : MATCH_TYPE_ITEM;
        int code = elementCode | typeMask;
        elementCode += 0x0010;
        return new UriMeta(path, code, type == Type.ITEM, customMimeType, selectColumn, from, altNotify, onlyQuery, triggers, rawQuery, builder);
    }

    private String findName(Element element) {
        for (Element f : element.getEnclosedElements()) {
            String simpleName = f.getSimpleName().toString();
            if ("TABLE_NAME".equals(simpleName) || "VIEW_NAME".equals(simpleName)) {
                return simpleName;
            }
        }
        return null;
    }

    private void processTemplateForModel(ProviderMeta model) {
        JavaFileObject file;
        try {
            String filePath = model.getPkgName() + "." + model.getClassName();
            logger.i("Creating file: " + filePath);
            file = processingEnv.getFiler().createSourceFile(filePath);
            logger.i("File created: " + filePath);
            Writer out = file.openWriter();
            Template t = cfg.getTemplate("provider.ftl");
            t.process(model, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.e("EntityProcessor IOException: ", e);
        } catch (TemplateException e) {
            logger.e("EntityProcessor TemplateException: ", e);
        }
    }

}
