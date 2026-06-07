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

class TypeCharm @JvmOverloads constructor(
    private val type: CharmType,
    properties: Item.Properties = Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
) : TrinketItem(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val data = stack.get(ModDataComponents.TYPE_CHARM_DATA)
        val matchMultiplier = data?.matchMultiplier() ?: Config.TYPE_CHARM_MATCH_MULTIPLIER.get().toFloat()

        tooltipComponents.add(
            Component.translatable(
                "item.cobblemoncharms.type_charm.tooltip",
                Component.translatable("cobblemon.type.${type.translationKey}"),
                String.format("§e%.1f", matchMultiplier)
            )
        )

        ClientTooltipUtils.appendStatusTooltip(
            tooltipComponents,
            enabled = ClientTooltipUtils.isTypeCharmEnabled(type),
            advancementPath = "type_charms/${type.translationKey}_charm",
            advancementLabel = Component.translatable(
                "advancement.cobblemoncharms.type_charm.title",
                Component.translatable("cobblemon.type.${type.translationKey}")
            )
        )
    }

    override fun getName(stack: ItemStack): Component =
        Component.translatable(
            "item.cobblemoncharms.type_charm",
            Component.translatable("cobblemon.type.${type.translationKey}")
        )
}