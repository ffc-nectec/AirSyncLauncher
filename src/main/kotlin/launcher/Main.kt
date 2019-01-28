package launcher

import launcher.view.PrintView
import launcher.zip.DownloadZipStream
import version.apigithub.ApiGithubRelease
import java.net.URL

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val commandRunAirsync = "cmd /k start java -jar airsync.jar"

internal class Main constructor(val args: Array<String>) {
    fun run() {
        val pv = PrintView()
        pv.isVisible = true

        pv.append = "Check version..."
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        pv.append = "Version is $airsyncVersion"
        val github = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        if (airsyncVersion != github.tag_name) {
            val assertInstall = github.assets.find { it.name == "install.zip" }!!

            val callback = { size: Double ->
                val percen = (size / assertInstall.size) * 100
                pv.append = "Load complete $percen %"
            }

            val urlZip = URL(assertInstall.browser_download_url)
            val zip = DownloadZipStream(urlZip, assertInstall.size, callSizeLoad = callback)

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
