package version.github

interface GetLastTag {
    fun getLastRelease(): String
    fun getLastRelease(callback: (callback: String) -> Unit)
}
