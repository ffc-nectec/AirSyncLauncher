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
import kotlin.system.exitProcess

/**
 * ตรวจสอบการ update airsync โดยจะตรวจสอบ version ล่าสุดให้เอง
 * @param dir ไดเรกทอรี่ ที่จะทำการติดตั้ง airsync
 */
class SelfUpdate(dir: File, val preRelease: Boolean) {

    private val replaceFlag = File(dir, "replace.flag")
    private val currentVersionFlag = File(dir, "launcher.version")

    private val newExeFile = File(dir, "tmp-ffc-airsync.exe")
    private val exeFile = File(dir, "ffc-airsync.exe")

    init {
        stampCurrentVersion(BuildConfig.VERSION)
        if (replaceFlag.exists()) {
            newExeFile.delete()
            replaceFlag.delete()
        }
        if (!exeFile.exists()) { // first time start by installer
            val release = GitHubLatestApi("ffc-nectec/airsync-launcher").getLastRelease()
            release.downloadExeFile(exeFile)
            stampCurrentVersion(release.tag_name)
        }
    }

    private fun stampCurrentVersion(version: String) {
        val fw = FileWriter(currentVersionFlag)
        fw.write(version)
        fw.close()
    }

    fun checkForUpdate() {
        if (newExeFile.exists()) {
            // already download newer version
            Thread.sleep(3000) // make sure process was released
            exeFile.replaceWith(newExeFile) {
                replaceFlag.createNewFile()
                exec(exeFile)
                exitProcess(0)
            }
        } else {
            try {
                getNewerVersion()?.let {
                    // restart to newer version tmp
                    exec(it)
                    exitProcess(0)
                }
            } catch (ignore: Throwable) {
                ignore.printStackTrace()
            }
        }
    }

    private fun File.replaceWith(file: File, onFinish: () -> Unit) {
        if (exists())
            delete()
        Files.copy(file.inputStream(), Paths.get(absolutePath))
        onFinish()
    }

    private fun getNewerVersion(): File? {
        val currentVersion = currentVersionFlag.readText()
        val release = if (preRelease)
            GitHubLatestApi("ffc-nectec/airsync-launcher").getLastPreRelease()!!
        else
            GitHubLatestApi("ffc-nectec/airsync-launcher").getLastRelease()
        if (release.tag_name != currentVersion) {
            release.downloadExeFile(newExeFile)
            return newExeFile
        }
        return null
    }

    private fun GithubRelease.downloadExeFile(dest: File) {
        val url = assets.find { it.name == "ffc-airsync.exe" }?.browser_download_url
        URL(url).download(dest)
    }
}
