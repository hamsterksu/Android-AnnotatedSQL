package com.annotatedsql.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class ProcessorLogger{

	private Messager messager; 
	
	public ProcessorLogger(Messager messager) {
		this.messager = messager;
	}
	
	public void w(String msg){
		w(msg, null);
	}
	
	public void w(String msg, Element element){
		messager.printMessage(Kind.WARNING, msg, element);
	}
	
	public void i(String msg){
		i(msg, null);
	}
	
	public void i(String msg, Element element){
		//messager.printMessage(Kind.NOTE, msg, element);
	}
	
	public void e(String msg, Throwable e, Element element){
		if(e != null){
			messager.printMessage(Kind.ERROR, msg + ": " + e.getMessage(), element);
		}else{
			messager.printMessage(Kind.ERROR, msg, element);
		}
	}
	
	public void e(String msg, Throwable e){
		e(msg, e, null);
	}
	
	public void e(String msg, Element e){
		e(msg, null, e);
	}
	
	public void e(String msg, Element...elms){
		if(elms != null){
			for(Element e : elms){
				e(msg, null, e);
			}
		}else{
			e(msg, null, null);
		}
	}
	
}
