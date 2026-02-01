package com.tezov.tuucho.core.data.repository.image

import app.cash.sqldelight.TransactionWithoutReturn
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.SourceFetchResult
import com.tezov.tuucho.core.data.repository._system.SystemPlatform
import com.tezov.tuucho.core.data.repository.database.ImageDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.ImageEntity
import com.tezov.tuucho.core.data.repository.di.ImageModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.HttpResponseExtension.asSource
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import io.ktor.client.statement.HttpResponse
import okio.FileSystem
import okio.Source
import okio.buffer
import okio.use

interface ImageDiskCacheProtocol {
    suspend fun isAvailable(
        cacheKey: String
    ): Boolean

    suspend fun retrieve(
        cacheKey: String,
    ): SourceFetchResult?

    suspend fun saveAndRetrieve(
        cacheKey: String,
        response: HttpResponse
    ): SourceFetchResult

    fun TransactionWithoutReturn.deleteAll(
        cacheKeyPrefix: String,
    )
}

internal class ImageDiskCache(
    coroutineScopes: CoroutineScopesProtocol,
    config: ImageModule.Config,
    systemPlatform: SystemPlatform,
    private val imageDatabase: ImageDatabaseSource,
) : ImageDiskCacheProtocol {
    private val diskCache = config.diskCacheSizeMo?.let { size ->
        DiskCache
            .Builder()
            .cleanupCoroutineContext(coroutineScopes.default.dispatcher)
            .maxSizeBytes(size * 1024L * 1024L)
            .directory(systemPlatform.pathFromCacheFolder(config.diskCacheDirectory ?: "tuucho.cache-images"))
            .build()
    }

    private val fileSystem: FileSystem = diskCache?.fileSystem ?: throw DataException.Default("Should not be possible")

    override suspend fun isAvailable(
        cacheKey: String
    ) = diskCache?.openSnapshot(cacheKey)?.also { it.close() } != null &&
        imageDatabase.isExist(cacheKey = cacheKey)

    override suspend fun retrieve(
        cacheKey: String,
    ): SourceFetchResult? {
        val snapshot = diskCache?.openSnapshot(cacheKey) ?: return null
        val entity = imageDatabase.getImageEntityOrNull(cacheKey) ?: run {
            snapshot.close()
            return null
        }
        return SourceFetchResult(
            source = ImageSource(
                file = snapshot.data,
                fileSystem = fileSystem,
                diskCacheKey = cacheKey,
                closeable = snapshot
            ),
            mimeType = entity.mimeType,
            dataSource = DataSource.DISK
        )
    }

    override suspend fun saveAndRetrieve(
        cacheKey: String,
        response: HttpResponse
    ): SourceFetchResult {
        val mimeType = response.headers["Content-Type"] ?: throw DataException.Default("missing header 'Content-Type'")
        return save(
            cacheKey = cacheKey,
            mimeType = mimeType,
            source = response.asSource()
        )?.let { snapshot ->
            SourceFetchResult(
                source = ImageSource(
                    file = snapshot.data,
                    fileSystem = fileSystem,
                    diskCacheKey = cacheKey,
                    closeable = snapshot
                ),
                mimeType = mimeType,
                dataSource = DataSource.NETWORK
            )
        } ?: SourceFetchResult(
            source = ImageSource(
                source = response
                    .asSource()
                    .buffer(),
                fileSystem = fileSystem
            ),
            mimeType = mimeType,
            dataSource = DataSource.NETWORK
        )
    }

    private suspend fun save(
        cacheKey: String,
        mimeType: String,
        source: Source
    ) = diskCache?.openEditor(cacheKey)?.let { editor ->
        imageDatabase.insertOrUpdate(
            ImageEntity(
                cacheKey = cacheKey,
                mimeType = mimeType
            )
        )
        runCatching {
            fileSystem.write(editor.data) {
                source.use { writeAll(it) }
            }
            editor.commitAndOpenSnapshot()
        }.onFailure { editor.abort() }
            .getOrNull()
    }

    override fun TransactionWithoutReturn.deleteAll(
        cacheKeyPrefix: String
    ) {
        val diskCache = diskCache ?: return
        val entities = imageDatabase.run { selectAll(cacheKeyPrefix) }
        entities.forEach {
            diskCache.remove(it.cacheKey)
        }
        imageDatabase.run { deleteAll(cacheKeyPrefix) }
    }
}
