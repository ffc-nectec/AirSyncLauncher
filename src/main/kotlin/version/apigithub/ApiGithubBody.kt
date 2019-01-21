package version.apigithub

import com.fatboyindustrial.gsonjodatime.DateTimeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

val githubGson: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeConverter())
        .create()

}

data class GithubRelease(
    val tag_name: String,
    val name: String,
    val prerelease: Boolean,
    val created_at: DateTime,
    val published_at: DateTime,
    val assets: List<Assets>
)

data class Assets(
    val name: String,
    val url: String,
    val content_type: String?,
    val size: Long,
    val created_at: DateTime,
    val updated_at: DateTime,
    val browser_download_url: String
)
