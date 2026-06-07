package com.darkbladenemo.cobblemoncharms.common.item.charm

import com.darkbladenemo.cobblemoncharms.client.util.ClientTooltipUtils
import com.darkbladenemo.cobblemoncharms.common.config.Config
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import dev.emi.trinkets.api.TrinketItem
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag

class ExpCharm(
    properties: Item.Properties = Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
) : TrinketItem(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val data = stack.get(ModDataComponents.EXP_CHARM_DATA)
        val multiplier = data?.multiplier() ?: Config.EXP_CHARM_MULTIPLIER.get().toFloat()

        tooltipComponents.add(Component.translatable("item.cobblemoncharms.exp_charm.tooltip"))
        tooltipComponents.add(
            Component.translatable(
                "tooltip.cobblemoncharms.exp_boost",
                String.format("§a%.0f%%", (multiplier - 1.0) * 100)
            )
        )

        ClientTooltipUtils.appendStatusTooltip(
            tooltipComponents,
            enabled = Config.ENABLE_EXP_CHARM.get(),
            advancementPath = "utility_charm/exp_charm",
            advancementLabel = "EXP Charm"
        )
    }
}