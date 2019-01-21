package version

interface CheckVersionFromGit {
    fun getLastRelease(): String
    fun getLastRelease(callback: (callback: String) -> Unit)
}
