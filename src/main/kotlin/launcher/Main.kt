package launcher

import launcher.view.PrintView
import launcher.zip.DownloadZipStream
import max.kotlin.checkdupp.CheckDupplicateWithRest
import max.kotlin.checkdupp.DupplicateProcessException
import version.apigithub.ApiGithubRelease
import java.net.URL

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val commandRunAirsync = "cmd /k start java -Xms1G -Xmx4G -jar -Dfile.encoding=UTF-8 -jar airsync.jar"

internal class Main constructor(val args: Array<String>) {
    private val procName = CheckDupplicateWithRest("airsync")

    fun run() {
        val pv = PrintView()
        pv.isVisible = true
        run {
            val mb = 1024L * 1024L
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val maxMemory = runtime.maxMemory()

            pv.append = ("Total mem = ${totalMemory / mb}")
            pv.append = ("Free mem = ${freeMemory / mb}")
            pv.append = ("User mem = ${(totalMemory - freeMemory) / mb}")
            pv.append = ("Max mem = ${maxMemory / mb}")
        }


        pv.append = "Check duplicate process."
        try {
            procName.register()
        } catch (ex: DupplicateProcessException) {
            pv.dispose()
            System.exit(1)
        }

        pv.append = "Check version..."
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        pv.append = "Version local is $airsyncVersion"

        val github = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        pv.append = "Version github is ${github.tag_name}"

        if (airsyncVersion != github.tag_name) {
            val assertInstall = github.assets.find { it.name == "install.zip" }!!
            pv.append = "Download new version..."

            val urlZip = URL(assertInstall.browser_download_url)
            val zip = DownloadZipStream(urlZip, assertInstall.size) { size: Double ->
                val percen = (size / assertInstall.size) * 100
                pv.append = "Load complete $percen %"
            }
            zip.download()
        }
        pv.append = "Complete patch..."
        println(airsyncVersion)
        Runtime.getRuntime().exec(commandRunAirsync)
        pv.dispose()
        System.exit(0)
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
