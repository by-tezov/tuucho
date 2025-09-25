plugins {
    alias(libs.plugins.convention.application.ios)
}

// Helper functions
fun logSuccess(text: String) {
    println("\u001B[32m$text\u001B[0m")
}

fun logWarning(text: String) {
    println("\u001B[33m$text\u001B[0m")
}

fun logInfo(text: String) {
    println("\u001B[34m$text\u001B[0m")
}

fun getAvailableDevices(): Map<String, List<String>> {
    val command = ProcessBuilder("xcrun", "simctl", "list", "devices")
        .start()
        .inputStream
        .bufferedReader()

    val deviceSections = mutableMapOf<String, MutableList<String>>()
    var currentSectionName = ""
    var currentSectionDevices = mutableListOf<String>()
    var includeSection = true

    command.forEachLine { line ->
        if (line.startsWith("--")) {
            if (includeSection && currentSectionName.isNotEmpty()) {
                deviceSections[currentSectionName] = currentSectionDevices
            }
            currentSectionName = line.replace("--", "")
                .trim()
                .lowercase()
                .replace(" ", "-")
            currentSectionDevices = mutableListOf()
            includeSection = !line.contains("Unavailable")
        } else if (includeSection && line.isNotBlank()) {
            currentSectionDevices.add(line.trim())
        }
    }

    if (includeSection && currentSectionName.isNotEmpty()) {
        deviceSections[currentSectionName] = currentSectionDevices
    }

    return deviceSections
}


fun getSimulatorId(device: String, devices: Map<String, List<String>>? = null): String? {
    val _devices = devices ?: getAvailableDevices()
    val deviceId = _devices.values.flatten()
        .find { it.contains(device) }
        ?.replace(Regex(".*\\(([0-9A-Fa-f-]+)\\).*"), "$1")
    return deviceId
}

fun createSimulator(deviceName: String, devices: Map<String, List<String>>? = null): String {
    var deviceId = getSimulatorId(deviceName, devices)
    if (deviceId == null) {
        val model: String
        val core: String
        when (deviceName) {
            "iphone_16-18.5-simulator" -> {
                model = "iPhone 16"
                core = "com.apple.CoreSimulator.SimRuntime.iOS-18-5"
            }

            else -> throw GradleException("invalid device name $deviceName")
        }

        deviceId = ProcessBuilder("xcrun", "simctl", "create", deviceName, model, core)
            .start().inputStream.bufferedReader().readText().trim()
    }
    return deviceId
}

// Generic tasks
tasks.register<Exec>("bundleInstall") {
    group = "build"
    description = "Install fastlane"

    doFirst {
        commandLine("bundle", "install")
    }
}

tasks.register<Exec>("createSimulatorAppMock") {
    group = "build"
    description = "Create simulator application"

    doFirst {
        val xcodeproj = project.findProperty("xcodeproj") as String
        val scheme = project.findProperty("scheme") as String
        val device = project.findProperty("device") as String

        logInfo("createSimulatorAppMock:: xcodeproj: $xcodeproj, scheme: $scheme, device: $device")

        val deviceId = createSimulator(device)
        commandLine(
            "bundle", "exec", "fastlane", "buildMock",
            "xcodeproj:${xcodeproj}",
            "scheme:${scheme}",
            "deviceId:${deviceId}"
        )
    }
}

// Helper tasks
tasks.register<Exec>("assembleMock") {
    group = "build"
    description = "assemble scheme debug for e2e test"

    doFirst {
        val device = project.findProperty("device") as String

        logInfo("assembleMock:: device: $device")

        workingDir = project.rootDir
        commandLine(
            "./gradlew",
            "app:ios:createSimulatorAppMock",
            "-Pxcodeproj=ios/ios.xcodeproj",
            "-Pscheme=ios",
            "-Pdevice=$device",
            "--stacktrace"
        )
    }
}
