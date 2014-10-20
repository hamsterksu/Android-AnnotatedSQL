package com.annotatedsql.processor;

import com.annotatedsql.processor.logger.ILogger;
import com.annotatedsql.util.TextUtils;

import java.util.Collection;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class ProcessorLogger implements ILogger {

    public static final String ARG_LOG_LEVEL = "logLevel";
    private static final String LOG_TAG = "aSQL: ";
    private static final LogLevel DEFAULT_LEVEL_VALUE = LogLevel.INFO;

    private enum LogLevel {
        INFO, WARN, ERROR;

        public static LogLevel parse(String name) {
            if (TextUtils.isEmpty(name))
                return DEFAULT_LEVEL_VALUE;
            for (LogLevel logLevel : LogLevel.values()) {
                if (logLevel.name().equalsIgnoreCase(name)) {
                    return logLevel;
                }
            }
            return DEFAULT_LEVEL_VALUE;
        }
    }

    private Messager messager;

    private LogLevel logLevel = DEFAULT_LEVEL_VALUE;

    public ProcessorLogger(Messager messager, Map<String, String> options) {
        this.messager = messager;
        if (options != null && options.containsKey(ARG_LOG_LEVEL)) {
            String level = options.get(ARG_LOG_LEVEL);
            logLevel = LogLevel.parse(level);
        }
    }

    @Override
    public void w(String msg) {
        w(msg, null);
    }

    @Override
    public void w(String msg, Element element) {
        if (logLevel.ordinal() <= LogLevel.WARN.ordinal()) {
            messager.printMessage(Kind.WARNING, LOG_TAG + msg, element);
        }
    }

    @Override
    public void i(String msg) {
        i(msg, null);
    }

    @Override
    public void i(String msg, Element element) {
        if (logLevel.ordinal() == LogLevel.INFO.ordinal()) {
            messager.printMessage(Kind.NOTE, LOG_TAG + msg, element);
        }
    }

    @Override
    public void e(String msg, Throwable e, Element element) {
        if (e != null) {
            messager.printMessage(Kind.ERROR, LOG_TAG + msg + ": " + e.getMessage(), element);
        } else {
            messager.printMessage(Kind.ERROR, LOG_TAG + msg, element);
        }
    }

    @Override
    public void e(String msg, Throwable e) {
        e(msg, e, null);
    }

    @Override
    public void e(String msg, Element e) {
        e(msg, null, e);
    }

    @Override
    public void e(String msg, Element... elms) {
        if (elms != null && elms.length != 0) {
            for (Element e : elms) {
                e(msg, null, e);
            }
        } else {
            e(msg, null, null);
        }
    }

    @Override
    public void e(String msg, Collection<? extends Element> elms) {
        if (elms != null && !elms.isEmpty()) {
            for (Element e : elms) {
                e(msg, null, e);
            }
        } else {
            e(msg, null, null);
        }
    }
}
