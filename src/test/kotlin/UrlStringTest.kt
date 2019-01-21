import org.junit.Test

class UrlStringTest {
    val baseData = """
<html><body>You are being <a href="https://github.com/ffc-nectec/airsync/releases/tag/0.0.1">redirected</a>.</body></html>
    """.trimIndent()

    // https://github.com/ffc-nectec/airsync/releases/download/0.0.1/install.zip
    @Test
    fun getUrl() {
        val urlPattern = Regex("""^.*a href="(.*)">redirected.*$""")
        val tagPattern = Regex("""^.*tag/(.*)$""")
        val url = urlPattern.matchEntire(baseData)!!.groupValues.last()
        val tag = tagPattern.matchEntire(url)!!.groupValues.last()

        println(url)
        println(tag)
    }
}
