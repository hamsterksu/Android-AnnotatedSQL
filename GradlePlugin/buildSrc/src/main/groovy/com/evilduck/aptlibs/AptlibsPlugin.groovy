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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal

public class AptlibsPlugin implements Plugin<Project> {

    private Project project

    private AptlibsExtension aptlibsExt

    public void apply(Project project) {
        this.project = project

        project.apply plugin: 'android'
        aptlibsExt = project.extensions.create("aptlibs", AptlibsExtension, (ProjectInternal) project)

        project.afterEvaluate {
            setupDefaultAptConfigs()
        }
        modifyJavaCompilerArguments()
    }

    def setupDefaultAptConfigs() {
        println 'setupDefaultAptConfigs'
        project.configurations.create('aptlibs').with {
            visible = false
            transitive = true
            description = 'The apt annotation processor libraries.'
        }

        project.configurations.create('aptcompile').with {
            visible = false
            transitive = true
            description = 'The API libraries to be included in java compilation.'
        }

        aptlibsExt.includedLibraries.each {
            def lib = it
            project.dependencies {
                aptlibs lib.aptDependency()
                aptcompile lib.compileDependency()
            }
        }
    }

    def modifyJavaCompilerArguments() {
        project.android.applicationVariants.all { variant ->
            def aptOutput = project.file("$project.buildDir/generated/source/$aptlibsExt.aptDir/$variant.dirName")
            variant.addJavaSourceFoldersToModel(aptOutput)

            variant.javaCompile.doFirst {
                aptOutput.mkdirs()

                String processors = ""
                aptlibsExt.includedLibraries.each {
                    it.processors.each {
                        processors += it + ','
                    }
                }

                variant.javaCompile.classpath += project.configurations.aptcompile
                if (processors.length() > 0) {
                    processors = processors.substring(0, processors.length() - 1)
                    variant.javaCompile.options.compilerArgs += [
                            '-processorpath', project.configurations.aptlibs.getAsPath(),
                            '-processor', processors
                    ]

                    aptlibsExt.includedLibraries.each {
                        it.appendAptArgs(variant.javaCompile.options.compilerArgs, variant);
                    }

                    variant.javaCompile.options.compilerArgs += ['-s', aptOutput]
                }
            }
        }
    }

}
