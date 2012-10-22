package com.annotatedsql.processor.sql;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.annotatedsql.AnnotationParsingException;
import com.annotatedsql.annotation.sql.Index;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.annotatedsql.ftl.IndexMeta;
import com.annotatedsql.ftl.SchemaMeta;
import com.annotatedsql.ftl.TableMeta;
import com.annotatedsql.ftl.ViewMeta;
import com.annotatedsql.processor.ProcessorLogger;
import com.annotatedsql.processor.sql.TableProcessor.TableInfo;
import com.annotatedsql.util.TextUtils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SupportedAnnotationTypes({ "com.annotatedsql.annotation.sql.Table", 
							"com.annotatedsql.annotation.sql.SimpleView",
							"com.annotatedsql.annotation.sql.Index", 
							"com.annotatedsql.annotation.sql.PrimaryKey",
							"com.annotatedsql.annotation.sql.Schema"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SQLProcessor extends AbstractProcessor {

	private ProcessorLogger logger;
	private Configuration cfg = new Configuration();
	private Map<String, List<String>> tableColumns = new HashMap<String, List<String>>();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		logger = new ProcessorLogger(processingEnv.getMessager());
		logger.i("SQLProcessor started");
		if(annotations == null || annotations.size() == 0)
			return false;
		cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/res"));
		try{
			return processTable(roundEnv);
		}catch (AnnotationParsingException e) {
			logger.e(e.getMessage(), e.getElement());
			return false;
		}
	}

	private boolean processTable(RoundEnvironment roundEnv) {
		logger.i("SQLProcessor processTable");
		
		String pkgName = null;
		SchemaMeta schema;
		Set<? extends Element> schemaElements = roundEnv.getElementsAnnotatedWith(Schema.class);
		if(schemaElements != null && schemaElements.size() != 0){
			if(schemaElements.size() != 1){
				for(Element e : schemaElements){
					logger.e("Please use one schema file", e);
				}
				return false;
			}else{
				Element e = schemaElements.iterator().next();
				Schema schemaElement = e.getAnnotation(Schema.class);
				if(TextUtils.isEmpty(schemaElement.value())){
					logger.e("Schema name can't be empty", e);
					return false;
				}
				schema = new SchemaMeta(schemaElement.value());
				PackageElement pkg = (PackageElement)e.getEnclosingElement();
				pkgName = pkg.getQualifiedName().toString();
			}
		}else{
			 schema = new SchemaMeta("SQLSchema");
		}
		
		
		
		for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
			
			if(pkgName == null){
				PackageElement pkg = (PackageElement)element.getEnclosingElement();
				pkgName = pkg.getQualifiedName().toString();
				logger.i("SQLProcessor pkgName found: " + pkgName);
			}
			
			logger.i("SQLProcessor table found: " + element.getSimpleName());
			Table table = element.getAnnotation(Table.class);
			TableInfo tableInfo = TableProcessor.create(element);
			tableColumns.put(table.value(), tableInfo.getColumns());
			schema.addTable(new TableMeta(table.value(), tableInfo.sql));
		}
		
		for (Element element : roundEnv.getElementsAnnotatedWith(Index.class)) {
			if(pkgName == null){
				PackageElement pkg = (PackageElement)element.getEnclosingElement();
				pkgName = pkg.getQualifiedName().toString();
				logger.i("SQLProcessor pkgName found: " + pkgName);
			}
			
			logger.i("SQLProcessor index found: " + element.getSimpleName());
			Index index = element.getAnnotation(Index.class);
			String sql = IndexProcessor.create(element);
			schema.addIndex(new IndexMeta(index.name(), sql));
		}
		for(Element element : roundEnv.getElementsAnnotatedWith(SimpleView.class)){
			logger.i("SQLProcessor simple view found: " + element.getSimpleName());
			SimpleView view = element.getAnnotation(SimpleView.class);
			String sql = SimpleViewProcessor.create(element, tableColumns);
			schema.addView(new ViewMeta(view.value(), sql));
		}
		if(pkgName == null || schema.isEmpty()){
			return false;
		}
		schema.setPkgName(pkgName);
		processTemplateForModel(schema);
		return true;
	}

	private void processTemplateForModel(SchemaMeta model) {
		JavaFileObject file;
		try {
			file = processingEnv.getFiler().createSourceFile(model.getPkgName() + "." + model.getClassName());
			logger.i("Creating file:  " + model.getPkgName() + "." + file.getName());
			Writer out = file.openWriter();
			Template t = cfg.getTemplate("schema.ftl");
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
