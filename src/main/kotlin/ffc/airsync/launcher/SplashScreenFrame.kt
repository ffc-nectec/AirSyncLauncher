/*
 * Copyright 2019 NSTDA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package ffc.airsync.launcher

import java.awt.Image
import javax.imageio.ImageIO
import javax.swing.JFrame

class SplashScreenFrame : Ui {

    val panel: SplashPanel = SplashPanel()
    val header = "FFC AirSync |"
    val frame = JFrame(header)

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
    }

    override var text: String
        get() = ""
        set(value) {
            frame.title = "$header $value"
        }
}
