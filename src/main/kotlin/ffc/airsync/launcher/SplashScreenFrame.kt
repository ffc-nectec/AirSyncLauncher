package ffc.airsync.launcher

import java.awt.Image
import javax.imageio.ImageIO
import javax.swing.JFrame

class SplashScreenFrame : Ui {

    val panel: SplashPanel = SplashPanel()
    val frame = JFrame("FFC AirSync | v${BuildConfig.VERSION}")

    init {
        frame.iconImages = listOf<Image>(
            ImageIO.read(javaClass.classLoader.getResource("icon/192x192.png")),
            ImageIO.read(javaClass.classLoader.getResource("icon/144x144.png")),
            ImageIO.read(javaClass.classLoader.getResource("icon/96x96.png"))
        )
        frame.isUndecorated = true
        frame.setSize(1440, 900)
        frame.contentPane = panel
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        panel.versionText.text = "v${BuildConfig.VERSION}"
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
            panel.progressBar.maximum = max
            panel.progressBar.value = progress
        }
        panel.progressText.text = message
        panel.revalidate()
    }

    override var text: String
        get() = "" //panel.progressTitle.text
        set(value) {
            panel.progressText.text = value
        }
}
