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
package com.evilduck.annotatedsql

import org.gradle.api.Project
import org.gradle.api.Plugin

public class AnnotatedSqlPlugin implements Plugin<Project>  {

    private Project project

    private AnnotatedSqlExtension extension

    public void apply(Project project) {
        this.project = project

        project.apply plugin: 'android'

        extension = project.extensions.create("annotatedsql", AnnotatedSqlExtension)
        extension.with {
            aptOutputDir = "aptGenerated"
        }

        setupDefaultAptConfigs()
        modifyJavaCompilerArguments()
    }

    def setupDefaultAptConfigs() {
        project.configurations.create('apt').with {
            visible = false
            transitive = true
            description = 'The apt libraries to be used for annotated sql.'
        }

        project.configurations.create('annotatedsql').with {
			extendsFrom project.configurations.compile
            visible = false
            transitive = true
            description = 'The compile time libraries to be used for annotated sql.'
        }

        project.dependencies {
            apt project.fileTree(dir: "${project.projectDir}/libs-apt", include: '*.jar')
            annotatedsql project.files("${project.projectDir}/libs/sqlannotation-annotations.jar")

            compile project.configurations.annotatedsql
        }
    }

    def modifyJavaCompilerArguments() {
        project.android.applicationVariants.all { variant ->
            def aptOutput = project.file("$project.buildDir/source/$extension.aptOutputDir/$variant.dirName")

            variant.javaCompile.doFirst {
                aptOutput.mkdirs()

                variant.javaCompile.options.compilerArgs += [
                        '-processorpath', project.configurations.apt.getAsPath(), '-processor',
                        'com.annotatedsql.processor.provider.ProviderProcessor,com.annotatedsql.processor.sql.SQLProcessor',
                        '-s', aptOutput
                ]
            }
        }
    }

}
