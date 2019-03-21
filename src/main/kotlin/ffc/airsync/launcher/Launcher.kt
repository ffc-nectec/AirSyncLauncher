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
import max.java.c64support.CheckJava64BitSupportWithCommand
import max.kotlin.checkdupp.CheckDupplicateWithRest
import max.kotlin.checkdupp.DupplicateProcessException
import max.windows.createlink.CreateLink
import java.io.File
import java.io.FileWriter
import java.net.URL
import javax.swing.JOptionPane

internal class Launcher constructor(val args: Array<String>) {

    private val ui: Ui = SplashScreenFrame()

    fun run() {
        try {
            ui.show()
            ui.text = "กำหนดค่าตั้งต้น"
            val appFolder = prepareAppFolder()
            createStartupLink(appFolder)
            stampLauncherVersion(appFolder)
            checkDuplicateProcess()
            ui.text = "ตรวจสอบเวอร์ชั่น"
            checkAirSyncVersion(appFolder)
            ui.text = ""
            launchAirSync(appFolder)
            ui.dispose()
            System.exit(0)
        } catch (exception: Throwable) {
            ui.dispose()
            JOptionPane.showMessageDialog(
                null,
                exception.message,
                "FFC AirSync Launcher",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun prepareAppFolder(): File {
        val root = System.getenv("APPDATA") ?: System.getProperty("user.home") + "\\AppData\\Roaming"
        val appFolder = File(root, "FFC")
        appFolder.mkdirs()
        return appFolder
    }

    private fun createStartupLink(appFolder: File) {
        ui.updateProgress(1, 10, "สร้างลิงค์เชื่อมต่อโปรแกรม")
        val exeFile = File(appFolder, "ffc-airsync.exe")
        val link = CreateLink(exeFile)
        val startupFile = FileWriter(File(link.userWindowsStartupPath + "ffc-airsync.bat"), false)

        startupFile.write("\"${exeFile.absolutePath}\"")
        startupFile.flush()
        startupFile.close()
    }

    private fun stampLauncherVersion(file: File) {
        ui.updateProgress(2, 10, "บันทึกเวอร์ชั่นปัจจุบัน")
        val fw = FileWriter(File(file, "launcher.version"))
        fw.write(BuildConfig.VERSION)
        fw.close()
    }

    private fun checkDuplicateProcess() {
        ui.updateProgress(3, 10, "ตรวจสอบสถานะโปรเสจ")
        try {
            CheckDupplicateWithRest("airsync").register()
        } catch (ex: DupplicateProcessException) {
            ui.dispose()
            System.exit(1)
        }
    }

    private fun checkAirSyncVersion(appFolder: File) {
        ui.updateProgress(message = "")
        val proc = Runtime.getRuntime().exec(cmdCheckAirSyncVersion(appFolder))
        val localVersion = proc.inputStream.reader().readText()
        val release = GitHubLatestApi("ffc-nectec/airsync").getLastRelease()

        print("airsync $localVersion, github ${release.tag_name}")
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
            zip.download(appFolder)
        }
        ui.updateProgress(100, 100, "เวอร์ชั่นใหม่ล่าสุดแล้ว")
    }

    private fun launchAirSync(appFolder: File) {
        if (CheckJava64BitSupportWithCommand().is64Support())
            Runtime.getRuntime().exec(cmdLaunchAirSyncX64(appFolder))
        else
            Runtime.getRuntime().exec(cmdLaunchAirSync(appFolder))
    }
}
