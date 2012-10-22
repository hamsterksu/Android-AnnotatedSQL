package com.annotatedsql.processor.provider;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.processor.ProcessorLogger;

@SupportedAnnotationTypes({ "com.annotatedsql.annotation.sql.Table", 
							"com.annotatedsql.annotation.sql.Schema",
							"com.annotatedsql.annotation.sql.URI"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ProviderProcessor extends AbstractProcessor{

	private ProcessorLogger logger;
	
	@Override
	public boolean process(Set<? extends TypeElement> arg0, RoundEnvironment roundEnv) {
		logger = new ProcessorLogger(processingEnv.getMessager());
		return processSchema(roundEnv);
	}
	
	private boolean processSchema(RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(URI.class)) {
			logger.i("el = " + element.getSimpleName());
		}
		return true;
	}

}
