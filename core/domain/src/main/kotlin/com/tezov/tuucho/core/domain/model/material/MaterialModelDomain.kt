package com.tezov.tuucho.core.domain.model.material

import kotlinx.serialization.Serializable

@Serializable
data class MaterialModelDomain(
    val version: String? = null,
    val root: ComponentModelDomain? = null,
    val components: List<ComponentModelDomain>? = null,
    val options: List<OptionModelDomain>? = null,
    val styles: List<StyleModelDomain>? = null,
    val contents: List<ContentModelDomain>? = null,
    val texts: List<TextModelDomain>? = null,
    val colors: List<ColorModelDomain>? = null,
    val dimensions: List<DimensionModelDomain>? = null,
)