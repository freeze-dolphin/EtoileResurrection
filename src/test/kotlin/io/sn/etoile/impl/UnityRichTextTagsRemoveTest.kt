package io.sn.etoile.impl

import io.sn.etoile.impl.ArcpkgConvertRequest.Companion.removeUnityRichTextTags
import kotlin.test.Test
import kotlin.test.assertEquals

class UnityRichTextTagsRemoveTest {

    @Test
    fun `test unity rich text tags remover`() {
        val before =
            "<u><color=#ee7cc5>S<color=#a783fb>U<color=#e687f1>P<color=#ffffff>E<color=#ee7cc5>R<color=#a783fb>N<color=#e687f1>O<color=#ffffff>V<color=#ee7cc5>A</color>"
        val expected = "SUPERNOVA"

        assertEquals(removeUnityRichTextTags(before), expected)
    }

}