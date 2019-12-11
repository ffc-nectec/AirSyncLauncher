/*
 * Copyright 2019 NSTDA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package ffc.airsync.launcher

import java.io.File

private val airsyncFile by lazy { File(FFC_HOME, "airsync.jar") }
private val javaFile by lazy { File(FFC_HOME, "jre/bin/java.exe") }
private val javawFile by lazy { File(FFC_HOME, "jre/bin/javaw.exe") }

fun cmdCheckAirSyncVersion(): String {
    return "\"${javaFile.absolutePath}\" -jar \"${airsyncFile.absolutePath}\" -v"
}

fun cmdLaunchAirSync(): String {
    getLogger(javaFile).debug { "Java file ${javaFile.absolutePath}" }
    getLogger(javawFile).debug { "Javaw file ${javawFile.absolutePath}" }
    getLogger(airsyncFile).debug { "Airsync file ${javaFile.absolutePath}" }
    val command = "cmd /k start \"${javawFile.absolutePath} -Xms1G -Xmx4G -jar -Dfile.encoding=UTF-8 " +
        "-jar ${airsyncFile.absolutePath}\""
    getLogger(command).info { "Run with command: $command" }
    return command
}
