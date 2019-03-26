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
import java.io.File

val runtime: Runtime
    get() = Runtime.getRuntime()

fun exec(command: String) = runtime.exec(command)

fun exec(file: File) = runtime.exec("\"${file.absolutePath}\"")

@Suppress("unused")
val Runtime.is64bit
    get() = CheckJava64BitSupportWithCommand().is64Support()
