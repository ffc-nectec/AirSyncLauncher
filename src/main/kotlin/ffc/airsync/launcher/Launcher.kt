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

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val defaultRunAirsync =
    "cmd /k start javaw -Xms1G -Xmx3G -jar -Dfile.encoding=UTF-8 -jar airsync.jar -runnow > airsync.log"
private const val x64RunAirsync =
    "cmd /k start javaw -d64 -Xms1G -Xmx5G -jar -Dfile.encoding=UTF-8 -jar airsync.jar -runnow > airsync.log"

internal class Launcher constructor(val args: Array<String>) {
    private val procName = CheckDupplicateWithRest("airsync")
    private val ui: Ui = SplashScreenFrame()

    fun run() {
        try {
            ui.show()
            ui.text = "กำหนดค่าตั้งต้น"
            createStartupLink()
            stampLauncherVersion()
            checkDuplicateProcess()
            ui.text = "ตรวจสอบเวอร์ชั่น"
            checkAirSyncVersion()
            ui.text = ""
            launchAirSync()
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

    private fun createStartupLink() {
        ui.updateProgress(1, 10, "สร้างลิงค์เชื่อมต่อโปรแกรม")
        val runParth = System.getProperty("user.dir")!! + "\\ffc-airsync.exe"
        val link = CreateLink(File(runParth))
        val startupFile = FileWriter(File(link.userWindowsStartupPath + "ffc-airsync.bat"), false)

        startupFile.write("\"$runParth\"")
        startupFile.flush()
        startupFile.close()
    }

    private fun stampLauncherVersion() {
        ui.updateProgress(2, 10, "บันทึกเวอร์ชั่นปัจจุบัน")
        val fw = FileWriter("launcher.version")
        fw.write(BuildConfig.VERSION)
        fw.close()
    }

    private fun checkDuplicateProcess() {
        ui.updateProgress(3, 10, "ตรวจสอบสถานะโปรเสจ")
        try {
            procName.register()
        } catch (ex: DupplicateProcessException) {
            ui.dispose()
            System.exit(1)
        }
    }

    private fun checkAirSyncVersion() {
        ui.updateProgress(message = "")
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        val github = GitHubLatestApi("ffc-nectec/airsync").getLastRelease()

        if (airsyncVersion != github.tag_name) {
            ui.text = "ปรับปรุงเวอร์ชั่น"
            val assertInstall = github.assets.find { it.name == "airsync.zip" }!!
            val loadingMessage = "ดาวน์โหลดเวอร์ชั่น ${github.tag_name}"
            ui.updateProgress(0, 100, loadingMessage)

            val urlZip = URL(assertInstall.browser_download_url)
            val zip = ZIpDownload(urlZip) { size: Double ->
                val progress = ((size / assertInstall.size) * 100).toInt()

                ui.updateProgress(progress.takeIf { it <= 100 } ?: 100, 100, loadingMessage)
            }
            zip.download(File(""))
        }
        ui.updateProgress(100, 100, "เวอร์ชั่นใหม่ล่าสุดแล้ว")
    }

    private fun launchAirSync() {
        if (CheckJava64BitSupportWithCommand().is64Support())
            Runtime.getRuntime().exec(x64RunAirsync)
        else
            Runtime.getRuntime().exec(defaultRunAirsync)
    }
}
