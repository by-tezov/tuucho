package com.tezov.tuucho.core.data.repository.network

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.runBlocking
import kotlinx.io.readByteArray
import okio.Buffer
import okio.Source
import okio.Timeout

object HttpResponseExtension {
    private const val defaultBufferSize = 8192L

    suspend fun HttpResponse.asSource(
        bufferSize: Long = defaultBufferSize
    ): Source {
        val channel = bodyAsChannel()
        return object : Source {
            override fun read(
                sink: Buffer,
                byteCount: Long
            ) = runBlocking {
                if (channel.isClosedForRead || channel.exhausted()) return@runBlocking -1L
                val sizeToRead = minOf(bufferSize, byteCount)
                val packet = channel.readRemaining(sizeToRead)
                sink.write(packet.readByteArray())
                sizeToRead
            }

            override fun timeout() = Timeout.NONE

            override fun close() {
                channel.cancel()
            }
        }
    }
}
