package com.darkbladenemo.cobblemoncharms.client.util

import net.minecraft.resources.ResourceLocation

/**
 * Client-side cache of earned advancement IDs.
 * Updated via SyncAdvancementsPayload on login or after grants.
 */
object ClientAdvancementCache {
    private val earned = mutableSetOf<ResourceLocation>()

    fun update(ids: Collection<ResourceLocation>) {
        earned.clear()
        earned.addAll(ids)
    }

    fun add(id: ResourceLocation) {
        earned.add(id)
    }

    fun has(id: ResourceLocation): Boolean = earned.contains(id)

    fun clear() {
        earned.clear()
    }
}
