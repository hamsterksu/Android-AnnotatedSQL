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
package com.evilduck.aptlibs.libs

class AnnotatedSqlLibrary extends AptLibrary  {

    public AnnotatedSqlLibrary() {
        name = "Annotated SQL"
        processors = ["com.annotatedsql.processor.provider.ProviderProcessor", "com.annotatedsql.processor.sql.SQLProcessor"];

        groupId = 'com.github.hamsterksu'
        artifactIdApt = 'android-annotatedsql-processor'
        artifactIdLibrary = 'android-annotatedsql-api'
    }

    void groupId(String groupId) {
        throw new UnsupportedOperationException("groupId cannot be changed")
    }

    void artifactIdApt(String artifactIdApt) {
        throw new UnsupportedOperationException("artifactIdApt cannot be changed")
    }

    void artifactIdLibrary(String artifactIdLibrary) {
        throw new UnsupportedOperationException("artifactIdLibrary cannot be changed")
    }

    public void customArgs(def args) {
        throw new UnsupportedOperationException("custom args cannot be changed")
    }

}