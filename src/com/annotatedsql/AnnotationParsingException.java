package com.annotatedsql;

import javax.lang.model.element.Element;

public class AnnotationParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Element[] e;
	
	public AnnotationParsingException(String msg, Element...e){
		super(msg);
		this.e = e;
	}

	public Element[] getElements(){
		return e;
	}
}
