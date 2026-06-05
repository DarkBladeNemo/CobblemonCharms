package com.darkbladenemo.cobblemoncharms.common.event

import com.darkbladenemo.cobblemoncharms.common.component.CatchCharmData
import com.darkbladenemo.cobblemoncharms.common.component.EVItemData
import com.darkbladenemo.cobblemoncharms.common.component.ExpCharmData
import com.darkbladenemo.cobblemoncharms.common.component.IVItemData
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData
import com.darkbladenemo.cobblemoncharms.common.component.ShinyCharmData
import com.darkbladenemo.cobblemoncharms.common.component.TypeCharmData
import com.darkbladenemo.cobblemoncharms.common.config.Config
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import com.darkbladenemo.cobblemoncharms.init.ModItems
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent

object ModEvents {

    // Safe config readers — return hardcoded defaults during datagen when
    // the config file hasn't been loaded yet (Config.SPEC.isLoaded() == false).
    private fun expMultiplier()         = if (Config.SPEC.isLoaded) Config.EXP_CHARM_MULTIPLIER.get().toFloat()         else 1.5f
    private fun catchMultiplier()       = if (Config.SPEC.isLoaded) Config.CATCH_CHARM_MULTIPLIER.get().toFloat()       else 2.0f
    private fun shinyMultiplier()       = if (Config.SPEC.isLoaded) Config.SHINY_CHARM_MULTIPLIER.get().toFloat()       else 3.0f
    private fun typeMatchMultiplier()   = if (Config.SPEC.isLoaded) Config.TYPE_CHARM_MATCH_MULTIPLIER.get().toFloat()  else 5.0f
    private fun superEvAmount()         = if (Config.SPEC.isLoaded) Config.SUPER_EV_INCREASE_AMOUNT.get()               else 100
    private fun superIvAmount()         = if (Config.SPEC.isLoaded) Config.SUPER_IV_INCREASE_AMOUNT.get()               else 10
    private fun goldBottleCapAmount()   = if (Config.SPEC.isLoaded) Config.GOLD_BOTTLE_CAP_IV_AMOUNT.get()              else 31

    @JvmStatic
    @SubscribeEvent
    fun onModifyDefaultComponents(event: ModifyDefaultComponentsEvent) {
        event.modify(ModItems.EXP_CHARM.get()) { builder ->
            builder.set(ModDataComponents.EXP_CHARM_DATA.get(), ExpCharmData(expMultiplier()))
        }

        event.modify(ModItems.CATCH_CHARM.get()) { builder ->
            builder.set(ModDataComponents.CATCH_CHARM_DATA.get(), CatchCharmData(catchMultiplier()))
        }

        event.modify(ModItems.SHINY_CHARM.get()) { builder ->
            builder.set(ModDataComponents.SHINY_CHARM_DATA.get(), ShinyCharmData(shinyMultiplier()))
        }

        val matchMultiplier = typeMatchMultiplier()
        ModItems.TYPE_CHARMS.forEach { (type, deferredCharm) ->
            event.modify(deferredCharm.get()) { builder ->
                builder.set(ModDataComponents.TYPE_CHARM_DATA.get(), TypeCharmData(type, matchMultiplier))
            }
        }

        event.modify(ModItems.MULTI_CHARM.get()) { builder ->
            builder.set(ModDataComponents.MULTI_CHARM_DATA.get(), MultiCharmData.empty())
        }

        val evAmount = superEvAmount()
        event.modify(ModItems.SUPER_CARBOS.get())   { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("speed",            evAmount)) }
        event.modify(ModItems.SUPER_PROTEIN.get())  { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("attack",           evAmount)) }
        event.modify(ModItems.SUPER_HP_UP.get())    { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("hp",               evAmount)) }
        event.modify(ModItems.SUPER_IRON.get())     { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("defence",          evAmount)) }
        event.modify(ModItems.SUPER_CALCIUM.get())  { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("special_attack",   evAmount)) }
        event.modify(ModItems.SUPER_ZINC.get())     { it.set(ModDataComponents.EV_ITEM_DATA.get(), EVItemData("special_defence",  evAmount)) }

        val ivAmount = superIvAmount()
        event.modify(ModItems.SUPER_HEALTH_CANDY.get())  { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("hp"),               ivAmount)) }
        event.modify(ModItems.SUPER_MIGHTY_CANDY.get())  { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("attack"),           ivAmount)) }
        event.modify(ModItems.SUPER_TOUGH_CANDY.get())   { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("defence"),          ivAmount)) }
        event.modify(ModItems.SUPER_SMART_CANDY.get())   { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("special_attack"),   ivAmount)) }
        event.modify(ModItems.SUPER_COURAGE_CANDY.get()) { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("special_defence"),  ivAmount)) }
        event.modify(ModItems.SUPER_QUICK_CANDY.get())   { it.set(ModDataComponents.IV_ITEM_DATA.get(), IVItemData(listOf("speed"),            ivAmount)) }

        event.modify(ModItems.GOLD_BOTTLE_CAP.get()) { builder ->
            builder.set(ModDataComponents.IV_ITEM_DATA.get(),
                IVItemData(listOf("hp", "attack", "defence", "special_attack", "special_defence", "speed"),
                    goldBottleCapAmount()))
        }
    }
}
