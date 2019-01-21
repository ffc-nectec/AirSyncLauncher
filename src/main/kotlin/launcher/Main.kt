package launcher

import version.apigithub.ApiGithubRelease

private const val commandGetAirsyncVersion = "java -jar airsync.jar -v"

internal class Main constructor(val args: Array<String>) {
    fun run() {
        val proc = Runtime.getRuntime().exec(commandGetAirsyncVersion)
        val airsyncVersion = proc.inputStream.reader().readText()
        val githubRelease = ApiGithubRelease("ffc-nectec/airsync").getLastRelease()
        println(airsyncVersion)
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
