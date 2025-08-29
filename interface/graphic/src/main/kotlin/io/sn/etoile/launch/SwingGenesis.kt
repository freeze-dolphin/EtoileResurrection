package io.sn.etoile.launch

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import io.sn.etoile.launch.ui.MainFrame
import io.sn.etoile.utils.json
import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess


private fun isWindowsDarkMode(): Boolean {
    try {
        val process = Runtime.getRuntime().exec(
            "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v AppsUseLightTheme"
        )
        val scanner = Scanner(process.inputStream).useDelimiter("\\A")
        val output = if (scanner.hasNext()) scanner.next() else ""
        scanner.close()
        return output.contains("AppsUseLightTheme") && output.trim { it <= ' ' }.endsWith("0x0")
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

private fun isMacDarkMode(): Boolean {
    try {
        val process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle")
        val scanner = Scanner(process.inputStream).useDelimiter("\\A")
        val result = if (scanner.hasNext()) scanner.next() else ""
        scanner.close()
        return result.trim { it <= ' ' } == "Dark"
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

val os = System.getProperty("os.name").lowercase(Locale.getDefault())
val userHome: String? = System.getProperty("user.home")
val userDir: String = System.getProperty("user.dir")!!

fun setupLaf(config: Config) {
    when {
        os.contains("mac") -> {
            when (config.theme) {
                "light" -> FlatMacLightLaf.setup()
                "dark" -> FlatMacDarkLaf.setup()
                else -> {
                    if (isMacDarkMode()) {
                        FlatMacDarkLaf.setup()
                    } else {
                        FlatMacLightLaf.setup()
                    }
                }
            }
        }

        os.contains("win") -> {
            when (config.theme) {
                "light" -> FlatIntelliJLaf.setup()
                "dark" -> FlatDarculaLaf.setup()
                else -> {
                    if (isWindowsDarkMode()) {
                        FlatDarculaLaf.setup()
                    } else {
                        FlatIntelliJLaf.setup()
                    }
                }
            }
        }

        else -> {
            when (config.theme) {
                "light" -> FlatIntelliJLaf.setup()
                "dark" -> FlatDarculaLaf.setup()
                else -> FlatLightLaf.setup()
            }
        }
    }
}

const val CONFIG_FILENAME = "EtoileResurrection.Swing.json"

fun getConfigPath(): Path {
    val configDir = when {
        userHome == null -> {
            userDir
        }

        os.contains("linux") || os.contains("unix") -> {
            Paths.get(userHome, ".config").toString()
        }

        os.contains("win") -> {
            val appData = System.getenv("APPDATA")
            appData ?: Paths.get(userHome, "AppData", "Roaming").toString()
        }

        os.contains("mac") -> {
            Paths.get(userHome, "Library", "Preferences").toString()
        }

        else -> {
            userDir
        }
    }

    return Paths.get(configDir, CONFIG_FILENAME)
}

@Serializable
data class Config(
    val version: Int,
    var lastPackedSonglistPath: String,
    var lastPackOutputDirectoryPath: String,
    var lastSelectedArcpkgPath: String,
    var lastExportPackname: String,
    var lastExportVersion: String,
    var lastExportOutputDirectoryPath: String,
    var localization: Boolean,
    var theme: String,
) {
    companion object {
        val configFile: File = getConfigPath().toFile()
    }

    fun saveConfig() {
        configFile.writeText(json.encodeToString(this))
    }
}

val defaultConfig by lazy {
    Config(
        1,
        userDir,
        userDir,
        userDir,
        "single",
        "1.0",
        userDir,
        true,
        "default"
    )
}

fun setupConfig(): Config {
    try {
        val config = json.decodeFromString<Config>(Config.configFile.readText(Charsets.UTF_8))
        return config
    } catch (e: Exception) {
        if (Config.configFile.exists()) {
            e.printStackTrace()
            println("Updating configurations...")

            // TODO updatable configuration
        }

        try {
            Config.configFile.createNewFile()
            Config.configFile.writeText(json.encodeToString(defaultConfig))
        } catch (e1: Exception) {
            e1.printStackTrace()
            JOptionPane.showMessageDialog(null, "Unable to create config file: ${e1.toString()}", "Error:", JOptionPane.ERROR_MESSAGE)
            exitProcess(-1)
        }
        return defaultConfig
    }
}

fun main() {
    val config = setupConfig()
    setupLaf(config)

    SwingUtilities.invokeLater {
        val mainFrame = MainFrame()
        UIHandler.initializeComponents(mainFrame, config)
    }
}
