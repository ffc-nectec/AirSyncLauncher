package version.github

import org.junit.Ignore
import version.CheckVersionFromGit

class GithubCheckerTest {
    val getTag: CheckVersionFromGit = GithubChecker("ffc-nectec/airsync")
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
