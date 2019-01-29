package launcher.zip

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DownloadZipStream(val url: URL, val fileSize: Long = 0, val callSizeLoad: (Double) -> Unit = {}) {
    fun download(destDir: File = File("")) {

        println("File size ${fileSize.toDouble() / (1024 * 1024)}M")

        var fileSizeIndex = 0L

        val webInputStream = MyBufferInputStream(url.openStream())

        // Print percen load per 1 sec.
        Thread {
            while (true) {
                callSizeLoad(webInputStream.sizeLoad.toDouble())
                Thread.sleep(1000)
            }
        }.start()

        val zis = ZipInputStream(webInputStream)

        val buffer = ByteArray(1024)

        var zipEntry = zis.nextEntry
        while (zipEntry != null) {
            val newFile = newFile(destDir, zipEntry)
            val fos = FileOutputStream(newFile)
            var len: Int = zis.read(buffer)
            while (len > 0) {
                fileSizeIndex += len
                fos.write(buffer, 0, len)
                if (fileSizeIndex % 10000 == 0L) {
                    println("${fileSizeIndex.toDouble() / (1024 * 1024)}M")
                }
                len = zis.read(buffer)
            }
            fos.close()
            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
        zis.close()
    }

    private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir.absolutePath, zipEntry.name)

        val destDirPath = destinationDir.canonicalPath
        val destFilePath = destFile.canonicalPath

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }

        return destFile
    }
}
