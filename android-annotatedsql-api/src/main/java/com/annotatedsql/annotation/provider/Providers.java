package com.annotatedsql.annotation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by hamsterksu on 25.09.2014.
 */
@Target(ElementType.TYPE)
public @interface Providers {
    Provider[] value();
}
