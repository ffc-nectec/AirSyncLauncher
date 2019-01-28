package launcher.view

import java.awt.BorderLayout
import java.awt.Font
import java.awt.TextArea
import javax.swing.JFrame
import javax.swing.JPanel

class PrintView : JFrame() {

    val text: TextArea

    var append
        get() = text.text
        set(value) {
            text.append("\n $value")
        }

    init {
        setSize(800, 600)
        setLocationRelativeTo(null)
        title = "Patch Load..."
        text = TextArea("Load ...")
        text.isEditable = false

        text.font = Font("", 1, 24)

        val panel = JPanel()
        panel.layout = BorderLayout()
        panel.add(text)
        contentPane.add(panel)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }
}
