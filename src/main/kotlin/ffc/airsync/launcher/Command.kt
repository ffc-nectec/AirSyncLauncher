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

private const val jarName = "airsync.jar"

fun cmdCheckAirSyncVersion(dir: File): String {
    val jarFile = File(dir, jarName)
    return "java -jar \"${jarFile.absolutePath}\" -v"
}

fun cmdLaunchAirSync(dir: File): String {
    val jarFile = File(dir, jarName)
    val logDir = File(dir, "log")
    val logFile = File(logDir, "airsync.log")
    return "cmd /k start javaw -Xms1G -Xmx3G -jar -Dfile.encoding=UTF-8 " +
        "-jar \"${jarFile.absolutePath}\" > \"${logFile.absolutePath}\""
}

fun cmdLaunchAirSyncX64(dir: File): String {
    val jarFile = File(dir, jarName)
    val logDir = File(dir, "log")
    val logFile = File(logDir, "airsync.log")
    return "cmd /k start javaw -d64 -Xms1G -Xmx5G -jar -Dfile.encoding=UTF-8 " +
        "-jar \"${jarFile.absolutePath}\" > \"${logFile.absolutePath}\""
}
