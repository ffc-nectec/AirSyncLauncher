package version.apigithub

interface GithubLastRelease {

    fun getLastRelease(): GithubRelease
    fun getLastRelease(callback: (callback: GithubRelease) -> Unit)
}
