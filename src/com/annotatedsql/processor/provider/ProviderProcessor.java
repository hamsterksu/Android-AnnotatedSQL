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
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.provider.URI.Type;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.ProviderMeta;
import com.annotatedsql.ftl.SchemaMeta;
import com.annotatedsql.ftl.UriMeta;
import com.annotatedsql.processor.ProcessorLogger;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SupportedAnnotationTypes({ "com.annotatedsql.annotation.sql.Table", 
							"com.annotatedsql.annotation.provider.Provider",
							"com.annotatedsql.annotation.provider.URI"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ProviderProcessor extends AbstractProcessor{

	private final static int MATCH_TYPE_ITEM = 0x0001;
	private final static int MATCH_TYPE_DIR = 0x0002;
	private final static int MATCH_TYPE_MASK = 0x000f;
	
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
			logger.e(e.getMessage(), e.getElement());
			return false;
		}
	}
	
	private boolean processSchema(RoundEnvironment roundEnv) {
		ProviderMeta provider = new ProviderMeta();
		
		String pkgName = null;
		
		for (Element element : roundEnv.getElementsAnnotatedWith(URI.class)) {
			logger.i("el = " + element.getSimpleName());
		}
		for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
			provider.addImport(((TypeElement)element).getQualifiedName().toString());
			provider.addUris(processTable(element));
		}
		for (Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)) {
			provider.addImport(((TypeElement)element).getQualifiedName().toString());
			provider.addUris(processTable(element));
		}
		provider.setPkgName("com.test.sql");
		provider.setClassName("TestProvider");
		provider.setSchemaClassName("SqlSchema");
		provider.setAuthority("com.test.AUTHORITY");
		processTemplateForModel(provider);
		return true;
	}

	private List<UriMeta> processTable(Element element) {
		String parentName = element.getSimpleName().toString();
		String nameField = findName(element);
		if(nameField == null){
			throw new AnnotationParsingException("Can't find table/view name. Please define field TABLE_NAME or VIEW_NAME ", element);
		}
		String from = parentName + "." + nameField; 
		List<UriMeta> uris = new ArrayList<UriMeta>(); 
		for(Element e : element.getEnclosedElements()){
			URI uri = e.getAnnotation(URI.class);
			if(uri == null){
				continue;
			}
			String pathValue = (String)((VariableElement)e).getConstantValue();
			String path = parentName + "." + e.getSimpleName().toString();
			if(uri.type() == Type.DIR_AND_ITEM){
				uris.add(createUriMeta(Type.DIR, path, uri.column(), pathValue, from));
				uris.add(createUriMeta(Type.ITEM, path, uri.column(), pathValue, from));
			}else{
				uris.add(createUriMeta(uri.type(), path, uri.column(), pathValue, from));
			}
		}
		return uris;
	}
	
	private UriMeta createUriMeta(Type type, String path, String selectColumn, String pathValue, String from){
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
		logger.i("add: " + code + "; path = " + path + "; from = " + from);
		return new UriMeta(path, code, type == Type.ITEM, selectColumn, from);
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
