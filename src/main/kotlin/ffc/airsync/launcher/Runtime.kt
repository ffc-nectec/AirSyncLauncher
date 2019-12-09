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

import max.java.c64support.CheckJava64BitSupportWithCommand
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.logger
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

val runtime: Runtime
    get() = Runtime.getRuntime()

fun exec(command: String) = runtime.exec(command)

fun exec(file: File) = runtime.exec("\"${file.absolutePath}\"")

@Suppress("unused")
val Runtime.is64bit
    get() = CheckJava64BitSupportWithCommand().is64Support()

val HOME_USER: String get() = System.getProperty("user.home")
val FFC_HOME: String?
    get() {
        val property = System.getProperty("FFC_HOME")
        return if (property != null)
            return property
        else
            System.getenv("FFC_HOME")
    }

fun URL.download(dest: File) {
    Files.copy(
        openStream(),
        Paths.get(dest.absolutePath),
        StandardCopyOption.REPLACE_EXISTING
    )
}

inline fun <reified T> getLogger(clazz: T): KotlinLogger {
    return logger(T::class.java.simpleName)
}
