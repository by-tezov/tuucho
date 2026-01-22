package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals

class StoreActionTest {
    @Test
    fun `keyValue command matches store command`() {
        assertEquals("store", StoreActionDefinition.KeyValue.command)
    }

    @Test
    fun `keyValue authority is correct`() {
        assertEquals("key-value", StoreActionDefinition.KeyValue.authority)
    }

    @Test
    fun `keyValue lockable is empty`() {
        assertEquals(InteractionLockable.Empty, StoreActionDefinition.KeyValue.lockable)
    }

    @Test
    fun `keyValue target constants are correct`() {
        assertEquals("save", StoreActionDefinition.KeyValue.Target.save)
        assertEquals("remove", StoreActionDefinition.KeyValue.Target.remove)
    }
}
