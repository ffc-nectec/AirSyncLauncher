package ffc.airsync.launcher

val is64BitCpu: Boolean
    get() {
        val property = System.getProperty("os.arch")
        getLogger(Launcher::class).info { "OS Arch is $property" }
        return property.endsWith("64")
    }
