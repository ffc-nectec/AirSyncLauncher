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

import max.download.zip.ZIpDownload
import max.githubapi.GitHubLatestApi
import max.kotlin.checkdupp.CheckDupplicateWithRest
import max.kotlin.checkdupp.DupplicateProcessException
import java.io.File
import java.io.FileWriter
import java.net.URL
import javax.swing.JOptionPane
import kotlin.system.exitProcess

internal class Launcher constructor(val args: Array<String>) {

    private val ui: Ui = SplashScreenFrame()

    fun run() {
        try {
            ui.show()
            ui.text = "ตรวจสอบสถานะ"
            val appFolder = prepareAppFolder()
            checkDuplicateProcess()
            SelfUpdate(appFolder).checkForUpdate()
            createStartupLink(appFolder)
            checkAirSyncVersion(appFolder)
            ui.text = "เข้าสู่โปรแกรม FFC AirSync"
            launchAirSync(appFolder)
            ui.dispose()
            exitProcess(0)
        } catch (exception: Throwable) {
            exception.printStackTrace()
            ui.dispose()
            JOptionPane.showMessageDialog(
                null,
                exception.message,
                "FFC AirSync Launcher",
                JOptionPane.ERROR_MESSAGE
            )
            exitProcess(500)
        }
    }

    private fun prepareAppFolder(): File {
        val root = System.getenv("APPDATA") ?: HOME_USER + "\\AppData\\Roaming"
        val appFolder = File(root, "FFC")
        appFolder.mkdirs()
        return appFolder
    }

    private fun checkDuplicateProcess() {
        ui.updateProgress(1, 100, "ตรวจสอบสถานะโปรเสจ")
        try {
            CheckDupplicateWithRest("airsync").register()
        } catch (ex: DupplicateProcessException) {
            throw DupplicateProcessException("พบ FFC AirSync ทำงานอยู่แล้ว")
        }
    }

    private fun createStartupLink(appDir: File) {
        ui.updateProgress(3, 100, "สร้างลิงค์เชื่อมต่อโปรแกรม")
        val startupDir = File(
            System.getProperty("user.home"),
            "AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup"
        )
        val startupFile = FileWriter(File(startupDir, "ffc-airsync.bat"), false)

        val exeFile = File(appDir, "ffc-airsync.exe")
        startupFile.write("\"${exeFile.absolutePath}\"")
        startupFile.flush()
        startupFile.close()
    }

    private fun checkAirSyncVersion(appDir: File) {
        ui.updateProgress(message = "ตรวจสอบเวอร์ชั่น")
        try {
            val localVersion = currentAirSyncVersion(appDir)
            val release = GitHubLatestApi("ffc-nectec/airsync").getLastRelease()

            if (localVersion != release.tag_name) {
                ui.text = "ปรับปรุงเวอร์ชั่น"
                val assertInstall = release.assets.find { it.name == "airsync.zip" }!!
                val loadingMessage = "ดาวน์โหลดเวอร์ชั่น ${release.tag_name}"
                ui.updateProgress(0, 100, loadingMessage)
                val urlZip = URL(assertInstall.browser_download_url)
                val zip = ZIpDownload(urlZip) { size: Double ->
                    val progress = ((size / assertInstall.size) * 100).toInt()
                    ui.updateProgress(progress.takeIf { it <= 100 } ?: 100, 100, loadingMessage)
                }
                zip.download(appDir)
            }
            ui.updateProgress(100, 100, "เวอร์ชั่นใหม่ล่าสุดแล้ว")
        } catch (exception: Throwable) {
            val jarFile = File(appDir, "airsync.jar")
            if (!jarFile.exists()) {
                throw IllegalStateException("ไม่สามารถติดตั้ง FFC Airsync ได้\nกรุณาตรวจสอบอินเตอร์เน็ต", exception)
            } else {
                exception.printStackTrace()
                ui.updateProgress(100, 100, "ไม่สามารถตรวจสอบเวอร์ชั่นใหม่ได้")
            }
        }
    }

    private fun currentAirSyncVersion(appDir: File): String {
        try {
            val proc = exec(cmdCheckAirSyncVersion(appDir))
            val localVersion = proc.inputStream.reader().readText()
            return localVersion
        } catch (notFound: Throwable) {
            return "unspecified"
        }
    }

    private fun launchAirSync(appDir: File) {
        if (runtime.is64bit)
            exec(cmdLaunchAirSyncX64(appDir))
        else
            exec(cmdLaunchAirSync(appDir))
    }
}
