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

import ffc.airsync.launcher.jredownload.JreSetup
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

    private val isPreRelease = args.contains("-pre")
    private val ui: Ui = SplashScreenFrame()
    private val logger = getLogger(this)

    fun run() {
        try {
            ui.show()
            ui.text = "ตรวจสอบสถานะ..."
            logger.info { "Setup FFC_HOME" }
            if (FFC_HOME.isNullOrBlank())
                SetupFFC_HOME().setup()
            val appFolder = File(FFC_HOME!!)
            val jreSetup = JreSetup(appFolder)
            logger.info { "Check jre install." }
            ui.updateProgress(5, 100)
            if (!jreSetup.isJavaInstall()) { // ถ้ายังไม่ได้ติดตั้ง jre ให้ติดตั้ง jre ตัวพื้นฐานก่อน
                ui.updateProgress(15, 100, "กำลังติดตั้งตัวเรียกใช้พื้นฐาน")
                logger.info { "Install jre base." }
                jreSetup.InstallJre(
                    "https://github.com/" +
                        "AdoptOpenJDK/openjdk8-binaries/" +
                        "releases/download/jdk8u232-b09/OpenJDK8U-jre_x64_windows_hotspot_8u232b09.zip"
                )
            }
            checkDuplicateProcess()
            logger.info { "Check launcher update" }
            ui.updateProgress(20, 100, "ตรวจสอบไฟล์ติดตั้งเวอร์ชั่นล่าสุด")
            SelfUpdate(appFolder, isPreRelease).checkForUpdate()
            logger.info { "Create startup link." }
            createStartupLink(appFolder)
            logger.info { "Check airsync update." }
            ui.updateProgress(40, 100, "ตรวจสอบ Airsync เวอร์ชั่นล่าสุด")
            checkAirSyncVersion(appFolder) // progress 40 - 85
            logger.info { "Check jre compat version" }
            ui.updateProgress(85, 100, "ตรวจสอบ java runtime x64")
            jreSetup.setupCompatVersion()
            ui.updateProgress(100, 100)
            ui.text = "เข้าสู่โปรแกรม FFC AirSync"
            logger.info { "Lunch airsync" }
            exec(cmdLaunchAirSync())
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
            val localVersion = currentAirSyncVersion()
            val release = GitHubLatestApi("ffc-nectec/airsync").getLastRelease()

            if (localVersion != release.tag_name) {
                ui.text = "ปรับปรุงเวอร์ชั่น"
                val assertInstall = release.assets.find { it.name == "airsync.zip" }!!
                val loadingMessage = "ดาวน์โหลดเวอร์ชั่น ${release.tag_name}"
                ui.updateProgress(40, 100, loadingMessage)
                val urlZip = URL(assertInstall.browser_download_url)
                val zip = ZIpDownload(urlZip) { size: Double ->
                    val progress = 40 + ((size / assertInstall.size) * 45).toInt()
                    ui.updateProgress(progress.takeIf { it <= 85 } ?: 85, 100, loadingMessage)
                }
                zip.download(appDir)
            }
            ui.updateProgress(85, 100, "เวอร์ชั่นใหม่ล่าสุดแล้ว")
        } catch (exception: Throwable) {
            val jarFile = File(appDir, "airsync.jar")
            if (!jarFile.exists()) {
                throw IllegalStateException("ไม่สามารถติดตั้ง FFC Airsync ได้\nกรุณาตรวจสอบอินเตอร์เน็ต", exception)
            } else {
                exception.printStackTrace()
                ui.updateProgress(85, 100, "ไม่สามารถตรวจสอบเวอร์ชั่นใหม่ได้")
            }
        }
    }

    private fun currentAirSyncVersion(): String {
        return try {
            val proc = exec(cmdCheckAirSyncVersion())
            val localVersion = proc.inputStream.reader().readText()
            localVersion
        } catch (notFound: Throwable) {
            "unspecified"
        }
    }
}
