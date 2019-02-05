package launcher

import launcher.view.PrintView
import launcher.zip.DownloadZipStream
import max.kotlin.checkdupp.CheckDupplicateWithRest
import version.apigithub.ApiGithubRelease
import java.net.URL

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val commandRunAirsync = "cmd /k start java -jar airsync.jar"

internal class Main constructor(val args: Array<String>) {
    private val procName = CheckDupplicateWithRest("airsync")

    fun run() {
        val pv = PrintView()
        pv.isVisible = true

        pv.append = "Check version..."
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        pv.append = "Version local is $airsyncVersion"

        val github = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        pv.append = "Version github is ${github.tag_name}"

        procName.register()

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
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
