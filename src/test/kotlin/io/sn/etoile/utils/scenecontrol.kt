package io.sn.etoile.utils

import io.sn.etoile.utils.scenecontrol.ScenecontrolService
import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.effect.KeyChannel
import io.sn.etoile.utils.scenecontrol.extractScenecontrols
import io.sn.etoile.utils.scenecontrol.loadChart
import io.sn.etoile.utils.scenecontrol.io.SerializedUnit
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Assumptions.assumeTrue
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object ScenecontrolSerializationTest {

    @Test
    fun `serialized unit test`() {
        val unit = SerializedUnit("test", listOf(Union(1), Union(2f), Union(true), Union.nullValue()))
        val unitRead = json.decodeFromString<SerializedUnit>(json.encodeToString(unit))

        assertEquals("[1, 2.0, true, null]", unitRead.properties!!.toTypedArray().contentDeepToString())
    }

    @Test
    fun `scDotJson flatten print`() {
        val chartFile = Path("Z:/Workspace/arc/fragments-category/songs/lfdyrmx/2.aff")
        assumeTrue(chartFile.exists())

        val timingGroups = loadChart(chartFile)
        val service = ScenecontrolService(extractScenecontrols(timingGroups), timingGroups, 3)
        val actualSc = service.serialize()

        val expectedScJson =
            Path("Z:/Workspace/idea/EtoileResurrection/result/lowiro.lfdyrmx/lfdyrmx/2.sc.json")
        assumeTrue(expectedScJson.exists())

        val expectedSc = json.decodeFromString<List<SerializedUnit>>(
            expectedScJson.readText(
                charset = Charsets.UTF_8
            )
        )

        fun flattenChannelPropertyToString(
            fullSerializedScenecontrol: List<SerializedUnit>,
            property: Union,
        ): String {
            return when {
                property.isNull -> "null"
                property.primitive != null -> {
                    val unit = fullSerializedScenecontrol[property.primitive!!.int]

                    if (property.primitive!!.intInited && unit.type.startsWith("channel")) {
                        unit.properties?.map {
                            flattenChannelPropertyToString(
                                fullSerializedScenecontrol,
                                it
                            )
                        }?.toTypedArray()?.contentDeepToString() ?: "null"
                    } else property.toString()
                }

                else -> json.encodeToString(property)
            }

        }

        val expectedScFlattened = expectedSc.find { it.type == "tg.8" }!!.let { (_, properties) ->
            properties!!.map {
                flattenChannelPropertyToString(expectedSc, it)
            }
        }.joinToString(separator = ", ", prefix = "[", postfix = "]")


        val actualScFlattened = actualSc!!.find { it.type == "tg.8" }!!.let { (_, properties) ->
            properties!!.map {
                flattenChannelPropertyToString(actualSc, it)
            }
        }.joinToString(separator = ", ", prefix = "[", postfix = "]")

        println(expectedScFlattened)
        println(actualScFlattened)
    }

}

/**
 * Assets/Tests/Scenecontrol/KeyChannelTest.cs
 */
object KeyChannelTest {

    @Test
    fun `keyChannel one key`() {
        KeyChannel().apply {
            addKey(0, 0f)
        }.let {
            assertEquals(0f, it.valueAt(0))
            assertEquals(0f, it.valueAt(1))
            assertEquals(0f, it.valueAt(-1))
        }
    }

    @Test
    fun `keyChannel multiple key`() {
        KeyChannel().apply {
            addKey(0, 0f)
            addKey(1, 1f)
            addKey(2, 2f)
            addKey(3, 3f)
        }.let {
            assertEquals(0f, it.valueAt(0))
            assertEquals(1f, it.valueAt(1))
            assertEquals(2f, it.valueAt(2))
            assertEquals(3f, it.valueAt(3))
        }
    }

    @Test
    fun `keyChannel multiple keys inbetween`() {
        KeyChannel().apply {
            addKey(0, 0f)
            addKey(2, 1f)
        }.let {
            assertTrue { it.valueAt(1) > 0 }
            assertTrue { it.valueAt(1) < 1 }
        }
    }

    @Test
    fun `keyChannel keys overlapping`() {
        // multiple keys overlapping
        assertEquals(2f, KeyChannel().apply {
            addKey(0, 0f)
            addKey(0, 1f)
            addKey(0, 2f)
            addKey(2, 2f)
        }.valueAt(1))

        assertEquals(3f, KeyChannel().apply {
            addKey(0, 0f)
            addKey(0, 1f)
            addKey(0, 2f)
            addKey(2, 4f)
        }.valueAt(1))
    }

    @Test
    fun `keyChannel extrapolate`() {
        KeyChannel().apply {
            addKey(0, 0f)
            addKey(1, 1f)
        }.let {
            assertTrue { it.valueAt(-1) == 0f }
            assertTrue { it.valueAt(2) == 1f }
        }
    }

    @Test
    fun `keyChannel no key`() {
        assertEquals(0f, KeyChannel().valueAt(0))
    }

}