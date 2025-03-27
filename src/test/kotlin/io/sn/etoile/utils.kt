package io.sn.etoile

import com.charleskorn.kaml.Yaml
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

}