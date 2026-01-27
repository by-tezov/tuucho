package com.tezov.tuucho.core.data.repository.image

import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.SourceFetchResult
import com.tezov.tuucho.core.data.repository._system.Platform
import com.tezov.tuucho.core.data.repository.di.ImageModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import okio.FileSystem
import okio.buffer
import okio.source

interface ImageDiskCacheProtocol {

    fun isAvailable(diskCacheKey: String): Boolean

    suspend fun retrieve(
        diskCacheKey: String,
    ): SourceFetchResult?

    suspend fun saveAndRetrieve(
        diskCacheKey: String,
        response: HttpResponse
    ): SourceFetchResult


}

class ImageDiskCache(
    coroutineScopes: CoroutineScopesProtocol,
    config: ImageModule.Config,
    platform: Platform,
    private val fileSystem: FileSystem
) : ImageDiskCacheProtocol {

    private val diskCache = config.diskCacheSizeMo?.let { size ->
        DiskCache.Builder()
            .cleanupCoroutineContext(coroutineScopes.image.context)
            .maxSizeBytes(size * 1024L * 1024L)
            .directory(platform.pathFromCacheFolder(config.diskCacheDirectory ?: "tuucho.cache-images"))
            .build()
    }

    override fun isAvailable(diskCacheKey: String): Boolean {
        val snapshot = diskCache?.openSnapshot(diskCacheKey) ?: return false
        snapshot.close()
        return true
    }

    override suspend fun retrieve(
        diskCacheKey: String,
    ): SourceFetchResult? {
        val snapshot = diskCache?.openSnapshot(diskCacheKey) ?: return null
        var contentType: String? = null
        runCatching {
            fileSystem.read(snapshot.metadata) {
                contentType = readUtf8LineStrict()
            }
        }
        contentType ?: return null
        return SourceFetchResult(
            source = ImageSource(
                file = snapshot.data,
                fileSystem = diskCache.fileSystem,
                diskCacheKey = diskCacheKey,
                closeable = snapshot
            ),
            mimeType = contentType,
            dataSource = DataSource.DISK
        )
    }

    override suspend fun saveAndRetrieve(
        diskCacheKey: String,
        response: HttpResponse
    ): SourceFetchResult {
        val contentType = response.headers["Content-Type"] ?: throw DataException.Default("missing header 'Content-Type'")
        diskCache?.openEditor(diskCacheKey)?.let { editor ->
            runCatching {
                fileSystem.write(editor.data) {
                    response.bodyAsChannel()
                        .toInputStream()
                        .source()
                        .buffer()
                        .use { writeAll(it) }
                }
                fileSystem.write(editor.metadata) {
                    writeUtf8("$contentType\n")
                }
                editor.commitAndOpenSnapshot()?.let { snapshot ->
                    return SourceFetchResult(
                        source = ImageSource(
                            file = snapshot.data,
                            fileSystem = fileSystem,
                            diskCacheKey = diskCacheKey,
                            closeable = snapshot
                        ),
                        mimeType = contentType,
                        dataSource = DataSource.NETWORK
                    )
                }
            }
        }
        return SourceFetchResult(
            source = ImageSource(
                source = response.bodyAsChannel()
                    .toInputStream().source().buffer(),
                fileSystem = fileSystem
            ),
            mimeType = contentType,
            dataSource = DataSource.NETWORK
        )
    }

}
