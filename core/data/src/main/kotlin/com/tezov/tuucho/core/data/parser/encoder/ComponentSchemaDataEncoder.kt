package com.tezov.tuucho.core.data.parser.encoder

import com.tezov.tuucho.core.data.di.MaterialEncoderModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataEncoder
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ComponentSchemaDataEncoder: SchemaDataEncoder(), KoinComponent {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<SchemaDataEncoder> by inject(
        Name.Processor.COMPONENT
    )

}