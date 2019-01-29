package launcher.zip

import java.io.BufferedInputStream
import java.io.InputStream

private const val mega = 1000

class MyBufferInputStream(`in`: InputStream?) : BufferedInputStream(`in`) {
    private var countByte: Long = 0

    val sizeLoad: Long get() = countByte

    override fun read(b: ByteArray?): Int {
        return super.read(b)
    }

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        countByte += len
        return super.read(b, off, len)
    }
}
