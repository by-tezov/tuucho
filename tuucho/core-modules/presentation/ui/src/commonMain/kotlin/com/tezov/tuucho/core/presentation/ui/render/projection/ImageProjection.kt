package com.tezov.tuucho.core.presentation.ui.render.projection

import coil3.Canvas
import coil3.Image
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

typealias ImageTypeAlias = ImageRepositoryProtocol.Image<Image, Canvas>

private typealias ImageProjectionTypeAlias = ProjectionProtocols<ImageTypeAlias>

interface ImageProjectionProtocol :
    IdProcessorProtocol,
    ImageProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class ImageProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: ImageProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : ImageProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    ImageProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status,
    TuuchoKoinComponent {
    init {
        attach(this as ExtractorProjectionProtocol<ImageTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonObject)
        ?.let { imageObject ->
            val koin = getKoin()
            val coroutineScopes = koin.get<CoroutineScopesProtocol>()
            val useCaseExecutor = koin.get<UseCaseExecutorProtocol>()
            val processImage = koin.get<ProcessImageUseCase>()
            coroutineScopes.action.async(
                throwOnFailure = true
            ) {
                val result = useCaseExecutor.await(
                    useCase = processImage,
                    input = ProcessImageUseCase.Input.ImageObject(
                        imageObject = imageObject
                    )
                )

                @Suppress("UNCHECKED_CAST")
                val image = (when (result) {
                    is ProcessImageUseCase.Output.Element -> {
                        result.image
                    }

                    else -> {
                        null
                    }
                }) as? ImageRepositoryProtocol.Image<Image, Canvas>
                this@ImageProjection.value = image
            }
            null
        }
}

private class MutableImageProjection(
    delegate: ImageProjectionProtocol,
    storage: StorageProjectionProtocol<ImageTypeAlias>
) : ImageProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private val ImageProjectionProtocol.mutable
    get(): ImageProjectionProtocol = MutableImageProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

fun createImageProjection(
    key: String,
): ImageProjectionProtocol = ImageProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
).mutable

fun TypeProjectorProtocols.image(
    key: String,
): ImageProjectionProtocol = createImageProjection(key)
