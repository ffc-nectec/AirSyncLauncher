package version.github

import org.junit.Ignore

class GithubCheckerTest {
    val getTag: GetLastTag = GithubGetLastTag("ffc-nectec/airsync")
    @Ignore("Real test.")
    fun getLastRelease() {

        println(getTag.getLastRelease())
    }

    @Ignore("Real test.")
    fun getLastRelease1() {
        getTag.getLastRelease {
            println(it)
        }

        Thread.sleep(5000)
    }
}
