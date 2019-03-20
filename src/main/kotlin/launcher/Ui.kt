package launcher

interface Ui {
    fun show()
    var text: String
    fun updateProgress(progress: Int? = null, max: Int? = null, message: String = "")
    fun dispose()
}
