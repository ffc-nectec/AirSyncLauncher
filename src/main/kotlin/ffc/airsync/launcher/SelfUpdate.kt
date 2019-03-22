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

import max.githubapi.GitHubLatestApi
import max.githubapi.GithubRelease
import java.io.File
import java.io.FileWriter
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

class SelfUpdate(dir: File) {

    private val replaceFlag = File(dir, "replace.flag")
    private val currentVersion = File(dir, "launcher.version")
    private val newVersion = File(dir, "tmp-ffc-airsync.exe")
    private val exeFile = File(dir, "ffc-airsync.exe")

    init {
        stampCurrentVersion(BuildConfig.VERSION)
        if (replaceFlag.exists()) {
            newVersion.delete()
            replaceFlag.delete()
        }
        if (!exeFile.exists()) { // first time start by installer
            val release = GitHubLatestApi("ffc-nectec/airsync-launcher").getLastRelease()
            release.downloadExeFile(exeFile)
            stampCurrentVersion(release.tag_name)
        }
    }

    private fun stampCurrentVersion(version: String) {
        val fw = FileWriter(currentVersion)
        fw.write(version)
        fw.close()
    }

    fun checkForUpdate() {
        if (newVersion.exists()) {
            // already download newer version
            Thread.sleep(3000) // make sure process was released
            replace()
        } else {
            // restart to newer version tmp if available
            downloadLatest()?.let {
                Runtime.getRuntime().exec(it.absolutePath)
                exitProcess(0)
            }
        }
    }

    private fun replace() {
        if (exeFile.exists())
            exeFile.delete()
        Files.copy(newVersion.inputStream(), Paths.get(exeFile.absolutePath))
        replaceFlag.createNewFile()

        Runtime.getRuntime().exec(exeFile.absolutePath)
        exitProcess(0)
    }

    private fun downloadLatest(): File? {
        val currentVersion = currentVersion.readText()
        val release = GitHubLatestApi("ffc-nectec/airsync-launcher").getLastRelease()
        if (release.tag_name != currentVersion) {
            release.downloadExeFile(newVersion)
            return newVersion
        }
        return null
    }

    private fun GithubRelease.downloadExeFile(dest: File) {
        val url = assets.find { it.name == "ffc-airsync.exe" }?.browser_download_url
        val input = URL(url).openStream()
        Files.copy(input, Paths.get(dest.absolutePath), StandardCopyOption.REPLACE_EXISTING)
    }
}
