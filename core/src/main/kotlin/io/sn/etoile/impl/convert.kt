package io.sn.etoile.impl

import com.tairitsu.compose.Chart
import com.tairitsu.compose.EventFilter
import com.tairitsu.compose.TimingGroupSpecialEffectFilter
import com.tairitsu.compose.filter.ShimFilter
import com.tairitsu.compose.parser.ArcCreateChartSerializer
import com.tairitsu.compose.parser.ArcaeaChartParser
import com.tairitsu.compose.parser.ArcaeaChartSerializer
import com.tairitsu.compose.parser.SimpleArcCreateChartParser
import io.sn.etoile.impl.ChartType.*
import java.io.File
import kotlin.io.writeText

enum class ChartType {
    Arcaea,
    ArcCreate
}

class A2CConverter : ArcaeaChartParser() {
    override val globalEffectFilter: TimingGroupSpecialEffectFilter = ShimFilter.A2C
    override val globalEventFilter: EventFilter = ShimFilter.A2C

    companion object {
        val Instance by lazy { A2CConverter() }
        fun parse(content: String): Chart = Instance.parse(content)
    }
}

class C2AConverter : SimpleArcCreateChartParser() {
    override val globalEffectFilter: TimingGroupSpecialEffectFilter = ShimFilter.C2A
    override val globalEventFilter: EventFilter = ShimFilter.C2A

    companion object {
        val Instance by lazy { C2AConverter() }
        fun parse(content: String): Chart = Instance.parse(content)
    }
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
                outputFile!!.writeText(A2CConverter.parse(sourceFile.readText()).let {
                    ArcCreateChartSerializer.serialize(it).joinToString(System.lineSeparator())
                })
            }

            ArcCreate -> {
                outputFile!!.writeText(C2AConverter.parse(sourceFile.readText()).let {
                    ArcaeaChartSerializer.serialize(it).joinToString(System.lineSeparator())
                })
            }
        }
    }

}