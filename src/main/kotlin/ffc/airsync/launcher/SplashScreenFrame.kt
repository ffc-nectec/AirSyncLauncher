package ffc.airsync.launcher

import javax.swing.JFrame

class SplashScreenFrame : Ui {

    val splashScreen: SplashScreen = SplashScreen()
    val frame = JFrame("FFC")

    init {
        frame.isUndecorated = true
        frame.setSize(800, 480)
        frame.contentPane = splashScreen.panel
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        splashScreen.versionLabel.text = "v${BuildConfig.VERSION}"
    }

    override fun show() {
        frame.isVisible = true
    }

    override fun dispose() {
        frame.isVisible = false
    }

    override fun updateProgress(progress: Int?, max: Int?, message: String) {
        if (progress != null && max != null) {
            require(progress <= max) { "progress [$progress] must lower or equal max [$max]" }
            splashScreen.progressBar.maximum = max
            splashScreen.progressBar.value = progress
        }
        splashScreen.progressText.text = message
    }

    override var text: String
        get() = splashScreen.progressTitle.text
        set(value) {
            splashScreen.progressTitle.text = value
        }
}
