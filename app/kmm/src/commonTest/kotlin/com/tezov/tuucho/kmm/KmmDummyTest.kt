package com.tezov.tuucho.kmm

import kotlin.test.Test
import kotlin.test.assertEquals


interface MyRepository {
    suspend fun getData(): String
}

class KmmDummyTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

}