package io.sn.etoile

import com.charleskorn.kaml.Yaml
import io.sn.etoile.impl.ArcpkgPackRequest.Companion.getDifficultyString
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals

object Utils {


    @Test
    fun `test import info serialization`() {
        val yaml = Yaml.default

        val importInfo = ImportInformationEntry(
            directory = "test",
            identifier = "etoile.test",
            settingsFile = "project.arcproj",
            version = 0,
            type = ArcpkgEntryType.LEVEL
        )

        val encoded =
            yaml.encodeToString(
                ImportInformationEntrySerializer(),
                importInfo
            )

        assertEquals(importInfo, yaml.decodeFromString(encoded))
        assertEquals(
            importInfo, yaml.decodeFromString(
                """
            identifier: "etoile.test"
            directory: "test"
            type: "level"
            settingsFile: "project.arcproj"
            """.trimIndent()
            )
        )
    }

    @Test
    fun `test get difficulty string`() {
        assertEquals("Future 10+", getDifficultyString(2, 10.7F))
        assertEquals("Present 9", getDifficultyString(1, 9.2F))
    }

}