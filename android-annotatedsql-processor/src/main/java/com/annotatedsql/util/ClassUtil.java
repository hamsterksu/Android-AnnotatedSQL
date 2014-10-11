package com.annotatedsql.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

/**
 * Created by hamsterksu on 04.10.2014.
 */
public final class ClassUtil {

    private ClassUtil() {
    }

    /**
     * with inheritance
     *
     * @param c
     * @return
     */
    public static List<Element> getAllClassFields(TypeElement c) {
        List<Element> fields = new ArrayList<Element>();
        collectParentFields(fields, Arrays.asList(c.asType()));
        return fields;
    }

    public static void collectParentFields(List<Element> fields, List<? extends TypeMirror> typeMirrors) {
        if (typeMirrors == null || typeMirrors.isEmpty())
            return;
        for (TypeMirror p : typeMirrors) {
            if (p instanceof NoType) {
                continue;
            }
            Element superClass = ((DeclaredType) p).asElement();
            List<? extends Element> inner = superClass.getEnclosedElements();
            if (inner != null) {
                fields.addAll(inner);
            }
            if (superClass instanceof TypeElement) {
                TypeElement typeElement = ((TypeElement) superClass);
                TypeMirror superclass = typeElement.getSuperclass();
                if (superclass != null) {
                    collectParentFields(fields, Arrays.asList(superclass));
                }
                collectParentFields(fields, typeElement.getInterfaces());
            }
        }
    }
}
