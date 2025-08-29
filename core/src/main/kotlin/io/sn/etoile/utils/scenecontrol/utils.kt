package io.sn.etoile.utils.scenecontrol

import com.tairitsu.compose.arcaea.Chart
import com.tairitsu.compose.arcaea.Scenecontrol
import com.tairitsu.compose.arcaea.TimingGroup
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * Assets/Scripts/Gameplay/Chart/ChartService.cs#LoadChart()
 */
fun loadChart(chartPath: Path): List<TimingGroup> = Chart.fromAff(
    chartPath.readText(charset = Charsets.UTF_8)
).let { chart ->
    mutableListOf(chart.mainTiming).apply {
        addAll(chart.subTiming.values)
    }
}

fun extractScenecontrols(tgChart: List<TimingGroup>) = tgChart.map {
    it.getScenecontrols()
}.fold(listOf<Scenecontrol>()) { a, b ->
    val reduceResult = a + b
    reduceResult
}.let {
    // ScenecontrolService.cs#RebuildList()
    it.sortedWith { o1, o2 -> o1.time.compareTo(o2.time) }
}