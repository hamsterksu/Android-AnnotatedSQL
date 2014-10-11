package com.annotatedsql.annotation.sql;

import com.annotatedsql.annotation.sql.Join.Type;

public @interface RawJoin {
    String joinTable();

    String onCondition();

    Type type() default Type.INNER;
}
