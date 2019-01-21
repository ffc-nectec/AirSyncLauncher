package version.github

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubReleaseApi {
    @GET("{repoName}/releases/latest")
    fun latestRelease(
        @Path("repoName", encoded = true) repoName: String
    ): Call<String>
}
