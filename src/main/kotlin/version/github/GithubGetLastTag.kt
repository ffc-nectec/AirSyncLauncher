package version.github

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class GithubGetLastTag(val repoName: String) : GetLastTag {
    private val urlPattern = Regex("""^.*a href="(.*)">redirected.*$""")
    private val tagPattern = Regex("""^.*tag/(.*)$""")
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("""https://github.com""")
        .build()

    override fun getLastRelease(): String {
        val response = retrofit.create(GithubURL::class.java).latestRelease(repoName).execute()
        response.raw().networkResponse()!!.request()
        check(response.code() == 200) { "http code return ${response.code()}" }

        return getLastTag(response)
    }

    private fun getLastTag(response: Response<String>): String {
        // val url = urlPattern.matchEntire(response.body()!!)!!.groupValues.last()
        val url = response.raw().networkResponse()!!.request().url().encodedPath()
        return tagPattern.matchEntire(url)!!.groupValues.last()
    }

    override fun getLastRelease(callback: (callback: String) -> Unit) {
        retrofit.create(GithubURL::class.java).latestRelease(repoName).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                throw t
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                callback(getLastTag(response))
            }
        })
    }
}
