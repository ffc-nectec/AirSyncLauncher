package launcher

import javax.swing.JFrame

class FrameUi : Ui {
    override fun show() {
        //splashScreen.panel1.isVisible = true
    }

    override var text: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun dispose() {
        // splashScreen.panel1.isVisible = false
    }

    val splashScreen: SplashScreen = SplashScreen()
    val frame = JFrame("FFC")

    init {
        frame.isUndecorated = true
        frame.defaultCloseOperation = 0
        frame.setSize(800, 480)
        frame.contentPane = splashScreen.panel1
        frame.setLocationRelativeTo(null)
        //frame.pack()
        frame.isVisible = true
    }

}
