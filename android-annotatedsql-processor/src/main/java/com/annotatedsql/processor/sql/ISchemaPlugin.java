package com.annotatedsql.processor.sql;

import com.annotatedsql.ftl.SchemaMeta;
import com.annotatedsql.ftl.ViewMeta;
import com.annotatedsql.processor.ProcessorLogger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by hamsterksu on 04.10.2014.
 */
public interface ISchemaPlugin {

    /**
     * execute in Processor.init method
     *
     * @param processingEnv
     * @param logger
     */
    void init(ProcessingEnvironment processingEnv, ProcessorLogger logger);

    /**
     * execute after table parser
     *
     * @param element
     * @param tableInfo
     */
    void processTable(TypeElement element, TableResult tableInfo);

    /**
     * execute after view parser
     *
     * @param element
     * @param meta
     */
    void processView(TypeElement element, ViewMeta meta);

    /**
     * execute after raw query parser
     *
     * @param element
     * @param meta
     */
    void processRawQuery(TypeElement element, ViewMeta meta);

    /**
     * execute in the end of generation, so you can create your files here
     *
     * @param element
     * @param model
     */
    void processSchema(TypeElement element, SchemaMeta model);

}
