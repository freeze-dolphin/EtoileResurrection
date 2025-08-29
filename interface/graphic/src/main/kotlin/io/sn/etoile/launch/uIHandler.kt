@file:Suppress("unused")

package io.sn.etoile.launch

import io.sn.etoile.impl.ArcpkgConvertRequest
import io.sn.etoile.impl.ArcpkgPackRequest
import io.sn.etoile.impl.ExportBgMode
import io.sn.etoile.impl.ExportConfiguration
import io.sn.etoile.launch.UIHandler.Pack.stepAfterSonglistSelection
import io.sn.etoile.launch.UIHandler.config
import io.sn.etoile.launch.ui.MainFrame
import io.sn.etoile.utils.Songlist
import io.sn.etoile.utils.json
import kotlinx.serialization.SerializationException
import java.awt.Desktop
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import java.io.File
import java.net.URI
import java.nio.file.Path
import javax.swing.DefaultComboBoxModel
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.Path
import kotlin.system.exitProcess


class SonglistFileFilter(val desc: String) : FileFilter() {
    override fun accept(f: File?): Boolean {
        if (f == null) return false
        if (f.isDirectory) return true

        try {
            val slst = json.decodeFromString<Songlist>(f.readText())
            return slst.songs.any { it.deleted != true }
        } catch (_: Exception) {
            return false
        }
    }

    override fun getDescription(): String? = desc
}

class SonglistDropTargetListener(val sub: MainFrame) : DropTargetListener {
    override fun dragEnter(dtde: DropTargetDragEvent) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY)
        } else {
            dtde.rejectDrag()
        }
    }

    override fun dragOver(dtde: DropTargetDragEvent?) {
    }

    override fun dropActionChanged(dtde: DropTargetDragEvent?) {
    }

    override fun dragExit(dte: DropTargetEvent?) {
    }

    @Suppress("UNCHECKED_CAST")
    override fun drop(dtde: DropTargetDropEvent) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY)
            val files = dtde.transferable
                .getTransferData(DataFlavor.javaFileListFlavor) as MutableList<File>

            var success = false
            if (!files.isEmpty()) {
                val file = files[0]

                try {
                    json.decodeFromString<Songlist>(file.readText())
                    sub.txtSlst.text = file.absolutePath
                    config.lastPackedSonglistPath = file.parent
                    config.saveConfig()
                    success = true

                    stepAfterSonglistSelection(sub, file)
                } catch (es: SerializationException) {
                    es.printStackTrace()
                    JOptionPane.showMessageDialog(sub, "Invalid songlist.", "Error", JOptionPane.ERROR_MESSAGE)
                    return
                }
            }

            dtde.dropComplete(success)
        } catch (e: Exception) {
            e.printStackTrace()
            dtde.rejectDrop()
        }
    }

}

val VERSION_REGEX =
    "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$".toRegex()

fun validateSemver(s: String): Boolean = s.isEmpty() || s.matches(VERSION_REGEX)

object UIHandler {

    lateinit var config: Config

    fun resetPackPage(sub: MainFrame) {
        sub.txtSlst.text = null
        sub.comboSongId.model = DefaultComboBoxModel()
        sub.txtSongIdRegex.text = null
        sub.toggleSongIdRegex.isSelected = false
        sub.txtPrefixPack.text = null
        sub.txtOutputDir.text = null
        sub.progPack.isVisible = false

        listOf(
            sub.comboSongId,
            sub.txtSongIdRegex,
            sub.toggleSongIdRegex,
            sub.txtPrefixPack,
            sub.txtOutputDir,
            sub.btnOutputDir,
            sub.txtPackOutput,
            sub.btnPack,
            sub.btnPackAndReset,
            sub.btnResetPack,
        ).forEach { it.isEnabled = false }
    }

    fun resetExportPage(sub: MainFrame) {
        sub.txtPackExport.text = config.lastExportPackname
        sub.txtVersionExport.text = config.lastExportVersion

        listOf(
            sub.txtPackExport,
            sub.txtVersionExport,
            sub.txtPrefixExport,
            sub.txtOutputDirExport,
            sub.btnOutputDirExport,
            sub.txtExportOutput,
            sub.btnExport,
            sub.btnExportAndReset,
            sub.btnResetExport,
        ).forEach { it.isEnabled = false }

        sub.txtOutputDirExport.text = null
        sub.comboArcpkg.model = DefaultComboBoxModel()
    }

    fun initializeComponents(sub: MainFrame, config: Config) {
        this.config = config

        if (config.localization) sub.initComponentsI18n()

        // set drop target
        sub.txtSlst.dropTarget = DropTarget(sub.txtSlst, SonglistDropTargetListener(sub))

        // reset components
        resetPackPage(sub)
        resetExportPage(sub)

        sub.isVisible = true
    }

    object Pack {

        internal fun stepAfterSonglistSelection(sub: MainFrame, selectedFile: File) {
            val slst = json.decodeFromString<Songlist>(selectedFile.readText())
            sub.comboSongId.model = getComboSongIdModel(slst, null)
            listOf(
                sub.comboSongId,
                sub.txtSongIdRegex,
                sub.toggleSongIdRegex,
                sub.txtPrefixPack,
                sub.txtOutputDir,
                sub.btnOutputDir,
            ).forEach { it.isEnabled = true }

            if (config.lastPackOutputDirectoryPath.isNotEmpty() && File(config.lastPackOutputDirectoryPath).isDirectory) {
                sub.txtOutputDir.text = config.lastPackOutputDirectoryPath
                stepPreparePack(sub)
            }
        }

        fun buttonSonglistSelection(sub: MainFrame, e: ActionEvent) {
            val chooser = JFileChooser().apply {
                currentDirectory = File(config.lastPackedSonglistPath)
                fileFilter = SonglistFileFilter("songlist Files")
            }
            val chooseResult = chooser.showOpenDialog(sub)

            if (chooseResult == JFileChooser.APPROVE_OPTION) {
                val selectedFile = chooser.selectedFile
                sub.txtSlst.text = selectedFile.absolutePath
                config.lastPackedSonglistPath = selectedFile.parent
                config.saveConfig()

                stepAfterSonglistSelection(sub, selectedFile)
            }
        }

        private fun getComboSongIdModel(slst: Songlist, regex: String?): DefaultComboBoxModel<Any> {
            val flt = if (regex != null) {
                val re = regex.toRegex()
                slst.songs.filter { it.id.matches(re) && it.deleted != true }.sortedBy { it.id }
            } else {
                slst.songs.sortedBy { it.id }
            }

            val rst = if (regex != null) {
                listOf("<Managed by Regular Expression>") + flt.map { it.id }
            } else flt.map { it.id }

            return DefaultComboBoxModel(rst.toTypedArray())
        }

        private fun getCurrentSonglist(sub: MainFrame): Songlist {
            return json.decodeFromString<Songlist>(File(sub.txtSlst.text).readText())
        }

        fun toggleSongIdRegex(sub: MainFrame, e: ActionEvent) {
            sub.txtSongIdRegex.isEnabled = sub.toggleSongIdRegex.isSelected
            sub.progPack.isVisible = sub.toggleSongIdRegex.isSelected

            sub.comboSongId.model =
                getComboSongIdModel(getCurrentSonglist(sub), if (sub.toggleSongIdRegex.isSelected) sub.txtSongIdRegex.text else null)
        }

        fun txtSongIdRegex(sub: MainFrame, e: ActionEvent) {
            if (!sub.toggleSongIdRegex.isSelected) return

            sub.comboSongId.model = getComboSongIdModel(getCurrentSonglist(sub), sub.txtSongIdRegex.text)
        }

        fun comboSongId(sub: MainFrame, e: ActionEvent) {
            if (sub.toggleSongIdRegex.isSelected) sub.comboSongId.selectedIndex = 0
        }

        fun btnPack(sub: MainFrame, e: ActionEvent, shouldReset: Boolean) {
            val slst = getCurrentSonglist(sub)

            sub.txtPackOutput.text = null
            val outputStream = TextAreaOutputStream(sub.txtPackOutput, sub.sclPackOutput)

            val tmpDisable = listOf(
                sub.btnPack,
                sub.btnPackAndReset,
                sub.btnResetPack,
                sub.txtSlst,
                sub.btnSlst,
                sub.comboSongId,
                sub.txtSongIdRegex,
                sub.toggleSongIdRegex,
                sub.txtPrefixPack,
                sub.btnOutputDir,
                sub.txtOutputDir,
            )

            var success = true
            val opThread = Thread {
                tmpDisable.forEach { it.isEnabled = false }

                if (sub.toggleSongIdRegex.isSelected) {
                    // regex mode
                    val songs =
                        slst.songs.filter { it.id.matches(sub.txtSongIdRegex.text.toRegex()) && it.deleted != true }.sortedBy { it.id }

                    sub.progPack.value = 0
                    sub.progPack.maximum = songs.size

                    if (songs.isEmpty()) {
                        JOptionPane.showMessageDialog(sub, "No matching song.", "Error:", JOptionPane.ERROR_MESSAGE)
                        success = false
                        return@Thread
                    }

                    songs.forEach { song ->
                        ArcpkgPackRequest(
                            songlistPath = Path(sub.txtSlst.text),
                            song = song,
                            prefix = sub.txtPrefixPack.text,
                            packOutputPath = Path(sub.txtOutputDir.text)
                        ).exec(outputStream)
                        sub.progPack.value += 1
                    }
                } else {
                    // single mode
                    val songId = sub.comboSongId.model.selectedItem as String

                    try {
                        val song = slst.songs.first { it.id == songId && it.deleted != true }
                        ArcpkgPackRequest(
                            songlistPath = Path(sub.txtSlst.text),
                            song = song,
                            prefix = sub.txtPrefixPack.text,
                            packOutputPath = Path(sub.txtOutputDir.text)
                        ).exec(outputStream)
                    } catch (e: NoSuchElementException) {
                        JOptionPane.showMessageDialog(sub, "No matching song.", "Error:", JOptionPane.ERROR_MESSAGE)
                        success = false
                        return@Thread
                    }
                }
            }
            opThread.start()

            Thread {
                opThread.join()

                SwingUtilities.invokeLater {
                    tmpDisable.forEach { it.isEnabled = true }
                    sub.progPack.value = 0

                    if (success && shouldReset) resetPackPage(sub)
                    sub.txtPackOutput.append("${if (success) "Done" else "Failed"}!\n")
                }
            }.start()
        }

        fun btnOutputDir(sub: MainFrame, e: ActionEvent) {
            val chooser = JFileChooser().apply {
                currentDirectory = File(config.lastPackOutputDirectoryPath)
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            }
            val chooseResult = chooser.showOpenDialog(sub)

            if (chooseResult == JFileChooser.APPROVE_OPTION) {
                val selectedFile = chooser.selectedFile
                sub.txtOutputDir.text = selectedFile.absolutePath
                config.lastPackOutputDirectoryPath = selectedFile.absolutePath
                config.saveConfig()
            }
        }

        private fun stepPreparePack(sub: MainFrame) {
            listOf(
                sub.btnPack,
                sub.btnPackAndReset,
                sub.btnResetPack,
                sub.sclPackOutput,
                sub.txtPackOutput,
            ).forEach { it.isEnabled = true }
        }

    }

    object Export {
        var currentSelectedArcpkgs: Set<Path> = emptySet()

        fun btnArcpkg(sub: MainFrame, e: ActionEvent) {
            val chooser = JFileChooser().apply {
                currentDirectory = File(config.lastSelectedArcpkgPath)
                fileFilter = FileNameExtensionFilter("ArcCreate Package", "arcpkg", "zip")
                isMultiSelectionEnabled = true
            }
            val chooseResult = chooser.showOpenDialog(sub)

            if (chooseResult == JFileChooser.APPROVE_OPTION) {
                val selectedFiles = chooser.selectedFiles
                sub.comboArcpkg.model =
                    DefaultComboBoxModel((listOf("<List of selected arcpkgs>") + selectedFiles.map { it.name }).toTypedArray())
                config.lastSelectedArcpkgPath = selectedFiles[0].parent
                config.saveConfig()

                currentSelectedArcpkgs = selectedFiles.map { it.toPath() }.toSet()

                stepAfterArcpkgSelection(sub)
            }
        }

        private fun stepAfterArcpkgSelection(sub: MainFrame) {
            listOf(
                sub.txtPackExport,
                sub.txtVersionExport,
                sub.txtPrefixExport,
                sub.btnOutputDirExport,
                sub.txtOutputDirExport,
            ).forEach { it.isEnabled = true }

            if (config.lastExportOutputDirectoryPath.isNotEmpty() && File(config.lastExportOutputDirectoryPath).isDirectory) {
                sub.txtOutputDirExport.text = config.lastExportOutputDirectoryPath
                stepPrepareExport(sub)
            }
        }

        private fun stepPrepareExport(sub: MainFrame) {
            listOf(
                sub.btnExport,
                sub.btnExportAndReset,
                sub.btnResetExport,
                sub.sclExportOutput,
                sub.txtExportOutput,
            ).forEach { it.isEnabled = true }
        }

        fun comboArcpkg(sub: MainFrame, e: ActionEvent) {
            sub.comboArcpkg.selectedIndex = 0
        }

        fun btnOutputDirExport(sub: MainFrame, e: ActionEvent) {
            val chooser = JFileChooser().apply {
                currentDirectory = File(config.lastExportOutputDirectoryPath)
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            }
            val chooseResult = chooser.showOpenDialog(sub)

            if (chooseResult == JFileChooser.APPROVE_OPTION) {
                val selectedFile = chooser.selectedFile
                sub.txtOutputDirExport.text = selectedFile.absolutePath
                config.lastExportOutputDirectoryPath = selectedFile.absolutePath
                config.saveConfig()
            }
        }

        fun btnExport(sub: MainFrame, e: ActionEvent, shouldReset: Boolean) {
            if (!txtVersionExport(sub, e)) return

            config.lastExportVersion = sub.txtVersionExport.text
            config.lastExportPackname = sub.txtPackExport.text
            config.saveConfig()

            sub.txtExportOutput.text = null
            val outputStream = TextAreaOutputStream(sub.txtExportOutput, sub.sclExportOutput)

            val tmpDisable = listOf(
                sub.btnExport,
                sub.btnExportAndReset,
                sub.btnResetExport,
                sub.btnArcpkg,
                sub.comboArcpkg,
                sub.txtPackExport,
                sub.txtVersionExport,
                sub.txtPrefixExport,
                sub.txtOutputDirExport,
                sub.btnOutputDirExport
            )

            var success = true
            val opThread = Thread {
                tmpDisable.forEach { it.isEnabled = false }

                sub.progExport.value = 0
                var maximumSetState = true
                try {
                    ArcpkgConvertRequest(
                        arcpkgs = currentSelectedArcpkgs,
                        identifierPrefix = sub.txtPrefixExport.text,
                        exportConfiguration = ExportConfiguration(
                            exportSet = sub.txtPackExport.text,
                            exportVersion = sub.txtVersionExport.text,
                            exportBgMode = ExportBgMode.AUTO_RENAME,
                            exportDirectory = File(sub.txtOutputDirExport.text),
                        )
                    ).exec(outputStream) {
                        SwingUtilities.invokeLater {
                            if (maximumSetState) {
                                sub.progExport.maximum = it.size
                                maximumSetState = false
                            }
                            sub.progExport.value++
                        }
                    }
                } catch (rte: RuntimeException) {
                    rte.printStackTrace()
                    JOptionPane.showMessageDialog(sub, rte.message, "Runtime Error:", JOptionPane.ERROR_MESSAGE)
                    success = false
                    return@Thread
                } catch (e: Exception) {
                    e.printStackTrace()
                    JOptionPane.showMessageDialog(
                        sub,
                        "You should check the runtime console and report this to the developer.\n" + e.message,
                        "Critical Error:",
                        JOptionPane.ERROR_MESSAGE
                    )
                    success = false
                    return@Thread
                }
            }
            opThread.start()

            Thread {
                opThread.join()

                SwingUtilities.invokeLater {
                    tmpDisable.forEach { it.isEnabled = true }
                    sub.progExport.value = 0

                    if (success && shouldReset) resetExportPage(sub)
                    sub.txtExportOutput.append("${if (success) "Done" else "Failed"}!\n")
                }
            }.start()
        }

        fun txtVersionExport(sub: MainFrame, e: ActionEvent): Boolean {
            val validation = validateSemver(sub.txtVersionExport.text)
            if (!validation) {
                JOptionPane.showMessageDialog(
                    sub,
                    "Invalid version: '${sub.txtVersionExport.text}'.",
                    "Error:",
                    JOptionPane.ERROR_MESSAGE
                )
            }
            return validation
        }

    }

    fun thisWindowClosed(e: WindowEvent) {
        config.saveConfig()
        exitProcess(1)
    }

    fun openBrowser(sub: MainFrame, s: String) {
        val desktop = Desktop.getDesktop()
        try {
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                val uri = URI(s)
                desktop.browse(uri)
            } else throw RuntimeException("No browser support.")
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(sub, "Unable to open url.", "Error:", JOptionPane.ERROR_MESSAGE)
        }
    }
}