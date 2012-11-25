package com.annotatedsql.processor.provider;

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

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.provider.Provider;
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
import com.annotatedsql.util.TextUtils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SupportedAnnotationTypes({"com.annotatedsql.annotation.provider.Provider"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ProviderProcessor extends AbstractProcessor{

	private final static int MATCH_TYPE_ITEM = 0x0001;
	private final static int MATCH_TYPE_DIR = 0x0002;
	//private final static int MATCH_TYPE_MASK = 0x000f;
	
	private int elementCode = 0x1000;
	
	private ProcessorLogger logger;
	private Configuration cfg = new Configuration();
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		logger = new ProcessorLogger(processingEnv.getMessager());
		if(annotations == null || annotations.size() == 0)
			return false;
		cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/res"));
		try{
			return processSchema(roundEnv);
		}catch (AnnotationParsingException e) {
			logger.e(e.getMessage(), e.getElements());
			return false;
		}
	}
	
	private boolean processSchema(RoundEnvironment roundEnv) {
		ProviderMeta provider;
		
		Set<? extends Element> providerElements = roundEnv.getElementsAnnotatedWith(Provider.class);
		if(providerElements != null && providerElements.size() != 0){
			if(providerElements.size() != 1){
				for(Element e : providerElements){
					logger.e("Please use one provider file", e);
				}
				return false;
			}else{
				Element e = providerElements.iterator().next();
				Provider providerElement = e.getAnnotation(Provider.class);
				if(TextUtils.isEmpty(providerElement.name())){
					logger.e("Provider name can't be empty", e);
					return false;
				}
				provider = new ProviderMeta(providerElement.name());
				PackageElement pkg = (PackageElement)e.getEnclosingElement();
				provider.setPkgName(pkg.getQualifiedName().toString());
				provider.setSchemaClassName(providerElement.schemaClass());
				provider.setOpenHelperClass(providerElement.openHelperClass());
				provider.setAuthority(providerElement.authority());
				provider.setSupportTransaction(providerElement.supportTransaction());
			}
		}else{
			return false;
		}
		
		for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
			provider.addImport(((TypeElement)element).getQualifiedName().toString());
			provider.addUris(processTable(element));
		}
		
		for (Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)) {
			provider.addImport(((TypeElement)element).getQualifiedName().toString());
			provider.addUris(processTable(element));
		}
		
		for (Element element : roundEnv.getElementsAnnotatedWith(RawQuery.class)) {
			provider.addImport(((TypeElement)element).getQualifiedName().toString());
			provider.addUris(processQuery(element));
		}
		processTemplateForModel(provider);
		return true;
	}
	

	private List<UriMeta> processQuery(Element element) {
		String parentName = element.getSimpleName().toString();
		RawQuery rawQuery = element.getAnnotation(RawQuery.class);
		
		String from = "SQL_QUERY_" + rawQuery.value().toUpperCase();
		
		return processUri(element, parentName, from, true);
	}
	
	private List<UriMeta> processTable(Element element) {
		String parentName = element.getSimpleName().toString();
		String nameField = findName(element);
		if(nameField == null){
			throw new AnnotationParsingException("Can't find table/view name. Please define field TABLE_NAME or VIEW_NAME ", element);
		}
		String from = parentName + "." + nameField; 
		return processUri(element, parentName, from, false);
	}

	private List<UriMeta> processUri(Element element, String parentName, String from, boolean rawQuery) {
		List<UriMeta> uris = new ArrayList<UriMeta>(); 
		for(Element e : element.getEnclosedElements()){
			URI uri = e.getAnnotation(URI.class);
			if(uri == null){
				continue;
			}
			
			List<TriggerMeta> triggersList = new ArrayList<TriggerMeta>();
			validateTrigger(e, triggersList, e.getAnnotation(Trigger.class));
			validateTrigger(e, triggersList, e.getAnnotation(Triggers.class));
			
			String pathValue = (String)((VariableElement)e).getConstantValue();
			String path = parentName + "." + e.getSimpleName().toString();
			if(uri.type() == Type.DIR_AND_ITEM && !rawQuery){
				uris.add(createUriMeta(Type.DIR, path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), triggersList, rawQuery));
				uris.add(createUriMeta(Type.ITEM, path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), null, rawQuery));
			}else{
				uris.add(createUriMeta(uri.type(), path, uri.column(), pathValue, from, uri.altNotify(), uri.onlyQuery(), triggersList, rawQuery));
			}
		}
		return uris;
	}
	
	private void validateTrigger(Element e, List<TriggerMeta> triggersList, Triggers triggers){
		if(triggers != null){
			for(Trigger t : triggers.value()){
				validateTrigger(e, triggersList, t);
			}
		}
	}
	
	private void validateTrigger(Element e, List<TriggerMeta> triggers, Trigger trigger){
		if(trigger == null){
			return;
		}
		String triggerMethod = trigger.name().trim();
		if(TextUtils.isEmpty(triggerMethod)){
			throw new AnnotationParsingException("Trigger method name is empty", e);
		}
		triggers.add(new TriggerMeta(triggerMethod, trigger.type(), trigger.when() == When.BEFORE));
	}
	
	private UriMeta createUriMeta(Type type, String path, String selectColumn, String pathValue, String from, String altNotify, boolean onlyQuery, List<TriggerMeta> triggers, boolean rawQuery){
		if(type == Type.ITEM && !pathValue.endsWith("#")){
			if(!pathValue.endsWith("/")){
				path += " + \"/#\"";
			}else{
				path += " + \"#\"";
			}
		}
		int typeMask = type == Type.DIR ? MATCH_TYPE_DIR : MATCH_TYPE_ITEM;
		int code  = elementCode | typeMask;
		elementCode += 0x0010;
		return new UriMeta(path, code, type == Type.ITEM, selectColumn, from, altNotify, onlyQuery, triggers, rawQuery);
	}
	
	private String findName(Element element){
		for(Element f : element.getEnclosedElements()){
			String simpleName = f.getSimpleName().toString();
			if("TABLE_NAME".equals(simpleName) || "VIEW_NAME".equals(simpleName)){
				return simpleName;
			}
		}
		return null;
	}
	
	private void processTemplateForModel(ProviderMeta model) {
		JavaFileObject file;
		try {
			file = processingEnv.getFiler().createSourceFile(model.getPkgName() + "." + model.getClassName());
			logger.i("Creating file:  " + model.getPkgName() + "." + file.getName());
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
