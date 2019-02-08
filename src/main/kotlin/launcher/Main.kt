package launcher

import hii.log.print.easy.EasyPrintLogGUI
import max.download.zip.ZIpDownload
import max.java.c64support.CheckJava64BitSupportWithCommand
import max.kotlin.checkdupp.CheckDupplicateWithRest
import max.kotlin.checkdupp.DupplicateProcessException
import version.apigithub.ApiGithubRelease
import java.net.URL

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val defaultRunAirsync = "cmd /k start java -Xms1G -Xmx3G -jar -Dfile.encoding=UTF-8 -jar airsync.jar"
private const val x64RunAirsync = "cmd /k start java -d64 -Xms1G -Xmx4G -jar -Dfile.encoding=UTF-8 -jar airsync.jar"

internal class Main constructor(val args: Array<String>) {
    private val procName = CheckDupplicateWithRest("airsync")

    fun run() {
        val pv = EasyPrintLogGUI("Patch Load...")
        pv.isVisible = true
        run {
            val mb = 1024L * 1024L
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val maxMemory = runtime.maxMemory()

            pv.text = ("Total mem = ${totalMemory / mb}")
            pv.text = ("Free mem = ${freeMemory / mb}")
            pv.text = ("User mem = ${(totalMemory - freeMemory) / mb}")
            pv.text = ("Max mem = ${maxMemory / mb}")
        }


        pv.text = "Check duplicate process."
        try {
            procName.register()
        } catch (ex: DupplicateProcessException) {
            pv.dispose()
            System.exit(1)
        }

        pv.text = "Check version..."
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        pv.text = "Version local is $airsyncVersion"

        val github = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        pv.text = "Version github is ${github.tag_name}"

        if (airsyncVersion != github.tag_name) {
            val assertInstall = github.assets.find { it.name == "install.zip" }!!
            pv.text = "Download new version..."

            val urlZip = URL(assertInstall.browser_download_url)
            val zip = ZIpDownload(urlZip) { size: Double ->
                val percen = (size / assertInstall.size) * 100
                pv.text = "Load complete $percen %"
            }
            zip.download()
        }
        pv.text = "Complete patch..."
        println(airsyncVersion)
        if (CheckJava64BitSupportWithCommand().is64Support())
            Runtime.getRuntime().exec(x64RunAirsync)
        else
            Runtime.getRuntime().exec(defaultRunAirsync)
        pv.dispose()
        System.exit(0)
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
