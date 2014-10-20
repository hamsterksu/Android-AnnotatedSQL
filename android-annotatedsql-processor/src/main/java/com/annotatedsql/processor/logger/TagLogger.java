package com.annotatedsql.processor.logger;

import com.annotatedsql.processor.ProcessorLogger;

import java.util.Collection;

import javax.lang.model.element.Element;

/**
 * Created by hamsterksu on 20.10.2014.
 */
public class TagLogger implements ILogger{

    private final ProcessorLogger logger;
    private final String tag;

    public TagLogger(String tag, ProcessorLogger logger) {
        this.tag = tag;
        this.logger = logger;
    }

    public ProcessorLogger getLogger() {
        return logger;
    }

    @Override
    public void w(String msg) {
        logger.w("[" + tag + "] " + msg);
    }

    @Override
    public void w(String msg, Element element) {
        logger.w("[" + tag + "] " + msg, element);
    }

    @Override
    public void i(String msg) {
        logger.i("[" + tag + "] " + msg);
    }

    @Override
    public void i(String msg, Element element) {
        logger.i("[" + tag + "] " + msg, element);
    }

    @Override
    public void e(String msg, Throwable e, Element element) {
        logger.e("[" + tag + "] " + msg, e, element);
    }

    @Override
    public void e(String msg, Throwable e) {
        logger.e("[" + tag + "] " + msg, e);
    }

    @Override
    public void e(String msg, Element e) {
        logger.e("[" + tag + "] " + msg, e);
    }

    @Override
    public void e(String msg, Element... elms) {
        logger.e("[" + tag + "] " + msg, elms);
    }

    @Override
    public void e(String msg, Collection<? extends Element> elms) {
        logger.e("[" + tag + "] " + msg, elms);
    }
}
