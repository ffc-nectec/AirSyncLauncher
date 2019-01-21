package version.apigithub

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubReleaseApiURL {

    @GET("repos/{repoName}/releases/latest")
    fun latestRelease(
        @Path("repoName", encoded = true) repoName: String
    ): Call<GithubRelease>
}
