package ffc.airsync.launcher.jredownload

import ffc.airsync.launcher.getLogger
import max.download.zip.ZIpDownload
import java.io.File
import java.net.URL

class JreSetup(airsyncPath: File) {
    private val logger = getLogger(this)
    private val destJre = File(airsyncPath, "jre")
    private val destTempDir = File(airsyncPath, "jretmp")
    private val localVersionFile = File(airsyncPath, "jre/jreVersion.txt")
    private val compatVersionFile = File(airsyncPath, "jreVersion.txt")

    fun setupCompatVersion() {
        val localVersion = readJreLocalVersion()
        val compatVersion = readJreCompatVersion() ?: return
        if (localVersion == compatVersion) return

        InstallJre(compatVersion)
        stampVersion()

        logger.debug { "Jre local version $localVersion" }
        logger.debug { "Jre compat version $compatVersion" }
    }

    fun InstallJre(compatVersion: String) {
        downloadZipAndExtract(compatVersion)
        moveTempToAirsyncJre()
    }

    fun isJavaInstall(): Boolean = File(destJre, "bin/javaw.exe").isFile

    private fun readJreCompatVersion(): String? {
        return if (compatVersionFile.isFile)
            compatVersionFile.readLines().firstOrNull()
        else
            null
    }

    private fun readJreLocalVersion(): String? {
        return if (localVersionFile.isFile)
            localVersionFile.readLines().firstOrNull()
        else
            null
    }

    private fun downloadZipAndExtract(urlKey: String) {
        val zd = ZIpDownload(URL(urlKey)) {
            logger.debug { "${it / (1024L * 1024L)}" }
        }
        destTempDir.deleteRecursively()
        zd.download(destTempDir)
    }

    private fun moveTempToAirsyncJre() {
        val listTemp = destTempDir.list()
        logger.debug { "List jre temp file $listTemp" }
        check(listTemp != null) { "${destTempDir.absolutePath} is Empty" }
        check(listTemp.isNotEmpty()) { "${destTempDir.absolutePath} is Empty" }
        val subJre = File(destTempDir, listTemp.first())
        logger.debug { "Sub Jre ${subJre.listFiles()?.map { it.name }}}" }
        destJre.deleteRecursively()
        destJre.mkdirs()
        subJre.copyRecursively(destJre)
        destTempDir.deleteRecursively()
    }

    private fun stampVersion() {
        compatVersionFile.copyRecursively(localVersionFile)
    }
}
