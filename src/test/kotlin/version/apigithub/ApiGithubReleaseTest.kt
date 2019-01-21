package version.apigithub

import org.junit.Ignore

class ApiGithubReleaseTest {

    val getLastRelease: GithubLastRelease = ApiGithubRelease("ffc-nectec/airsync")
    @Ignore("Real test.")
    fun getLastRelease() {
        val data = getLastRelease.getLastRelease()
        println(data)
    }

    @Ignore("Real test.")
    fun getLastRelease1() {
        getLastRelease.getLastRelease {
            println(it)
        }

        Thread.sleep(5000)
    }
}
