package com.itangcent.idea.plugin.fields

import java.util.*
import javax.lang.model.element.Modifier


open class BasedFieldGenerator {

    companion object {

        var fieldModifiers: Set<Modifier> = HashSet(Arrays.asList(Modifier.PRIVATE, Modifier.PROTECTED))
        var notFieldModifiers: Set<Modifier> = HashSet(Arrays.asList(Modifier.STATIC, Modifier.FINAL))
    }
}
