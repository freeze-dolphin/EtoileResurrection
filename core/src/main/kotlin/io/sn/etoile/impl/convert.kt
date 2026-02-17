package io.sn.etoile.impl

import com.tairitsu.compose.parser.ArcCreateChartSerializer
import com.tairitsu.compose.parser.ArcaeaChartParser
import com.tairitsu.compose.parser.ArcaeaChartSerializer
import com.tairitsu.compose.parser.SimpleArcCreateChartParser
import io.sn.etoile.impl.ChartType.*
import java.io.File

enum class ChartType {
    Arcaea,
    ArcCreate
}

class ChartConvertRequest(
    val sourceChartType: ChartType,
    val sourceFile: File,
    var outputFile: File?,
) {

    fun exec() {
        if (outputFile == null) {
            outputFile = File(sourceFile.absolutePath + ".convert.aff")
        }

        when (sourceChartType) {
            Arcaea -> {
                outputFile?.writeText(ArcaeaChartParser.parse(sourceFile.readText()).let {
                    ArcCreateChartSerializer.serialize(it).joinToString(System.lineSeparator())
                })
            }

            ArcCreate -> {
                outputFile?.writeText(SimpleArcCreateChartParser.parse(sourceFile.readText()).let {
                    ArcaeaChartSerializer.serialize(it).joinToString(System.lineSeparator())
                })
            }
        }
    }

}