package com.darkbladenemo.cobblemoncharms.common.item.charm

import com.darkbladenemo.cobblemoncharms.client.util.ClientTooltipUtils
import com.darkbladenemo.cobblemoncharms.common.config.Config
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import top.theillusivec4.curios.api.type.capability.ICurioItem

class CatchCharm : Item(
    Properties()
        .stacksTo(1)
        .rarity(Rarity.UNCOMMON)
), ICurioItem {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val data = stack.get(ModDataComponents.CATCH_CHARM_DATA.get())
        val multiplier = data?.multiplier() ?: Config.CATCH_CHARM_MULTIPLIER.get().toFloat()

        tooltipComponents.add(Component.translatable("item.cobblemoncharms.catch_charm.tooltip"))
        tooltipComponents.add(
            Component.translatable(
                "tooltip.cobblemoncharms.catch_boost",
                String.format("§a%.1f", multiplier)
            )
        )

        ClientTooltipUtils.appendStatusTooltip(
            tooltipComponents,
            enabled = Config.ENABLE_CATCH_CHARM.get(),
            advancementPath = "utility_charm/catch_charm",
            advancementLabel = "Catch Charm"
        )
    }
}