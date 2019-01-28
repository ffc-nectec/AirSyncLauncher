package launcher

import launcher.zip.DownloadZipStream
import version.apigithub.ApiGithubRelease
import java.net.URL

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"
private const val commandRunAirsync = "cmd /k start java -jar airsync.jar"

internal class Main constructor(val args: Array<String>) {
    fun run() {
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        val github = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        if (airsyncVersion != github.tag_name) {
            val assertInstall = github.assets.find { it.name == "install.zip" }!!
            val urlZip = URL(assertInstall.browser_download_url)
            val zip = DownloadZipStream(urlZip, assertInstall.size)

            zip.download()
        }

        println(airsyncVersion)
        Runtime.getRuntime().exec(commandRunAirsync)
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
