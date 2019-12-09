package ffc.airsync.launcher

import java.io.File

class SetupFFC_HOME {
    fun setup() {
        if (FFC_HOME == null) {
            val roamingUserPath = System.getProperty("user.home") + "\\AppData\\Roaming"
            val ffcHome = File(roamingUserPath, "FFC")
            runCatching { ffcHome.mkdirs() }
            exec("setx FFC_HOME \"${ffcHome.absolutePath}\"")
            System.setProperty("FFC_HOME", ffcHome.absolutePath)
        } else {
            System.setProperty("FFC_HOME", FFC_HOME!!)
        }
    }
}
