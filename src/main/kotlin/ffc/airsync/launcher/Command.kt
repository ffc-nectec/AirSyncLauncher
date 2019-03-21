package ffc.airsync.launcher

import java.io.File

private const val jarName = "airsync.jar"

fun cmdCheckAirSyncVersion(dir: File): String {
    val jarFile = File(dir, jarName)
    return "java -jar ${jarFile.absolutePath} -v"
}

fun cmdLaunchAirSync(dir: File): String {
    val jarFile = File(dir, jarName)
    return "cmd /k start javaw -Xms1G -Xmx3G -jar -Dfile.encoding=UTF-8 " +
        "-jar ${jarFile.absolutePath} -runnow > airsync.log"
}

fun cmdLaunchAirSyncX64(dir: File): String {
    val jarFile = File(dir, jarName)
    return "cmd /k start javaw -d64 -Xms1G -Xmx5G -jar -Dfile.encoding=UTF-8 " +
        "-jar ${jarFile.absolutePath} -runnow > airsync.log"
}
