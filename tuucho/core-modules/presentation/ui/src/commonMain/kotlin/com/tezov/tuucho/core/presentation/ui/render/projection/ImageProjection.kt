package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import coil3.PlatformContext
import coil3.compose.asPainter
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.presentation.ui._system.LocalTuuchoKoin
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import coil3.Image as CoilImage

class Image(
    private val coilImage: ImageRepositoryProtocol.Image<CoilImage>
) : TuuchoKoinComponent,
    ImageRepositoryProtocol.Image<CoilImage> by coilImage {
    @Composable
    fun asPainter(): Painter {
        val koin = LocalTuuchoKoin.current
        return remember {
            source.asPainter(context = koin.get<PlatformContext>())
        }
    }
}

typealias ImageTypeAlias = Image

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
                result?.collect {
                    @Suppress("UNCHECKED_CAST")
                    this@ImageProjection.value = Image(coilImage = it as ImageRepositoryProtocol.Image<CoilImage>)
                }
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
