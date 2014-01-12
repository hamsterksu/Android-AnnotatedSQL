/*
 * Copyright 2013 hamsterksu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *        You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evilduck.aptlibs

import org.gradle.api.Action
import com.evilduck.aptlibs.libs.AptLibrary
import com.evilduck.aptlibs.libs.AndroidAnnotationsLibrary
import com.evilduck.aptlibs.libs.AnnotatedSqlLibrary
import com.evilduck.aptlibs.libs.GroundyLibrary
import com.evilduck.aptlibs.libs.AptLibraryFactory
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.project.ProjectInternal

class AptlibsExtension  {

    String aptDir = "apt"
    AnnotatedSqlLibrary annotatedSql
    AndroidAnnotationsLibrary androidAnnotations
    GroundyLibrary groundy

    NamedDomainObjectContainer<AptLibrary> custom

    public AptlibsExtension(ProjectInternal project) {
        custom = project.container(AptLibrary, new AptLibraryFactory())
    }

    public void annotatedSql(Action<AnnotatedSqlLibrary> action) {
        annotatedSql = new AnnotatedSqlLibrary();
        action.execute(annotatedSql);
    }

    public void androidAnnotations(Action<AndroidAnnotationsLibrary> action) {
        androidAnnotations = new AndroidAnnotationsLibrary();
        action.execute(androidAnnotations);
    }

    public void groundy(Action<AndroidAnnotationsLibrary> action) {
        groundy = new GroundyLibrary();
        action.execute(groundy);
    }

    void custom(Action<NamedDomainObjectContainer<AptLibrary>> action) {
        action.execute(custom)
    }

    public List<AptLibrary> getIncludedLibraries() {
        List<AptLibrary> libs = new ArrayList<AptLibrary>();

        if (annotatedSql != null) {
            libs.add(annotatedSql);
        }
        if (androidAnnotations != null) {
            libs.add(androidAnnotations);
        }
        if (groundy != null) {
            libs.add(groundy);
        }

        libs.addAll(custom)

        return libs
    }

}