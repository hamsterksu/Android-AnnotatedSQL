package com.annotatedsql.processor.logger;

import java.util.Collection;

import javax.lang.model.element.Element;

/**
 * Created by hamsterksu on 20.10.2014.
 */
public interface ILogger {
    void w(String msg);

    void w(String msg, Element element);

    void i(String msg);

    void i(String msg, Element element);

    void e(String msg, Throwable e, Element element);

    void e(String msg, Throwable e);

    void e(String msg, Element e);

    void e(String msg, Element... elms);

    void e(String msg, Collection<? extends Element> elms);
}
