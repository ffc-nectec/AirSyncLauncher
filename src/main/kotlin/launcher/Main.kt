package launcher

import com.nstda.hii.airsynclauncher.BuildConfig
import hii.log.print.easy.EasyPrintLogGUI
import max.download.zip.ZIpDownload
import max.githubapi.GitHubLatestApi
import max.java.c64support.CheckJava64BitSupportWithCommand
import max.kotlin.checkdupp.CheckDupplicateWithRest
import max.kotlin.checkdupp.DupplicateProcessException
import max.windows.createlink.CreateLink
import java.io.File
import java.io.FileWriter
import java.net.URL

private const val VERSION = BuildConfig.VERSION
private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val defaultRunAirsync =
    "cmd /k start javaw -Xms1G -Xmx3G -jar -Dfile.encoding=UTF-8 -jar airsync.jar -runnow >> airsync.log"
private const val x64RunAirsync =
    "cmd /k start javaw -d64 -Xms1G -Xmx5G -jar -Dfile.encoding=UTF-8 -jar airsync.jar -runnow >> airsync.log"

internal class Main constructor(val args: Array<String>) {
    private val procName = CheckDupplicateWithRest("airsync")
    private val pv = EasyPrintLogGUI("Patch Load...", width = 900, lineLimit = 100)

    fun run() {
        pv.isVisible = true
        pv.text = "สร้างตัวช่วยในการใช้งานอัตโนมัติ"
        createStartupLink()
        stampLauncherVersion()
        checkDuplicateProcess()
        checkAirSyncVersion()

        if (CheckJava64BitSupportWithCommand().is64Support())
            Runtime.getRuntime().exec(x64RunAirsync)
        else
            Runtime.getRuntime().exec(defaultRunAirsync)
        pv.dispose()
        System.exit(0)
    }

    private fun stampLauncherVersion() {
        val fw = FileWriter("launcher.version")
        fw.write(VERSION)
        fw.close()
    }

    private fun checkAirSyncVersion() {
        pv.text = "Check Air Sync version..."
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        pv.text = "Version local is $airsyncVersion"

        val github = GitHubLatestApi("ffc-nectec/airsync").getLastRelease()
        pv.text = "Version github is ${github.tag_name}"

        if (airsyncVersion != github.tag_name) {
            val assertInstall = github.assets.find { it.name == "airsync.zip" }!!
            pv.text = "Download new version..."

            val urlZip = URL(assertInstall.browser_download_url)
            val zip = ZIpDownload(urlZip) { size: Double ->
                val percen = (size / assertInstall.size) * 100
                pv.text = "Load complete $percen %"
            }
            zip.download(File(""))
        }
        pv.text = "Complete patch..."
        println(airsyncVersion)
    }

    private fun checkDuplicateProcess() {
        pv.text = "Check duplicate process."
        try {
            procName.register()
        } catch (ex: DupplicateProcessException) {
            pv.dispose()
            System.exit(1)
        }
    }

    private fun createStartupLink() {
        val runParth = System.getProperty("user.dir")!! + "\\ffc-airsync.exe"
        val link = CreateLink(File(runParth))
        // link.build(File(link.userWindowsStartupPath + "AirSyncLauncher"), override = true)

        val startupFile = FileWriter(File(link.userWindowsStartupPath + "ffc-airsync.bat"), false)

        startupFile.write("\"$runParth\"")
        startupFile.flush()
        startupFile.close()
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
