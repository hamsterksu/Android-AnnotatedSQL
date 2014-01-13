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

import com.evilduck.aptlibs.ArgConfig

class AptLibrary {

    String name
    boolean enabled = true
    List<String> processors = new ArrayList<String>()

    String groupId
    String artifactIdApt
    String artifactIdLibrary
    String version

    def args;

    public AptLibrary() {
    }

    public AptLibrary(String name) {
        this.name = name
    }

    public void customArgs(def args) {
        this.args = args
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled
    }

    public void setProcessors(List<String> processors) {
        this.processors = processors
    }

    void groupId(String groupId) {
        this.groupId = groupId
    }

    void artifactIdApt(String artifactIdApt) {
        this.artifactIdApt = artifactIdApt
    }

    void artifactIdLibrary(String artifactIdLibrary) {
        this.artifactIdLibrary = artifactIdLibrary
    }

    void version(String version) {
        this.version = version
    }

    String aptDependency() {
        return "${groupId}:${artifactIdApt}:${version}"
    }

    String compileDependency() {
        return "${groupId}:${artifactIdLibrary}:${version}"
    }

    void appendAptArgs(Collection<String> args, variant) {
        if (this.args != null) {
            ArgConfig config = new ArgConfig()
            this.args.delegate = config
            this.args(variant)
            args.addAll(config.args)
        }
    }

}