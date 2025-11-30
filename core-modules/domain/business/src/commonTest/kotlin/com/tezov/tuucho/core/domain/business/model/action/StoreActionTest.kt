package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals

class StoreActionTest {

    @Test
    fun `keyValue command matches store command`() {
        assertEquals("store", StoreAction.KeyValue.command)
    }

    @Test
    fun `keyValue authority is correct`() {
        assertEquals("key-value", StoreAction.KeyValue.authority)
    }

    @Test
    fun `keyValue lockable is empty`() {
        assertEquals(InteractionLockable.Empty, StoreAction.KeyValue.lockable)
    }

    @Test
    fun `keyValue target constants are correct`() {
        assertEquals("save", StoreAction.KeyValue.Target.save)
        assertEquals("remove", StoreAction.KeyValue.Target.remove)
    }
}
