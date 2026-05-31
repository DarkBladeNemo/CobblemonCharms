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

class TypeCharm @JvmOverloads constructor(
    private val type: CharmType,
    properties: Properties = Properties()
        .stacksTo(1)
        .rarity(Rarity.UNCOMMON)
) : Item(properties), ICurioItem {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val data = stack.get(ModDataComponents.TYPE_CHARM_DATA.get())
        val matchMultiplier = data?.matchMultiplier() ?: Config.TYPE_CHARM_MATCH_MULTIPLIER.get().toFloat()

        tooltipComponents.add(
            Component.translatable(
                "item.cobblemoncharms.type_charm.tooltip",
                Component.translatable("cobblemon.type.${type.translationKey}"),
                String.format("§e%.1f", matchMultiplier)
            )
        )
        // The current Non_matching type tooltip is confusing, and knowing the radius is pointless.
        // Will sleep on whether I keep them or not.
//        val globalPenalty = Config.TYPE_CHARM_NON_MATCH_MULTIPLIER.get().toFloat()
//        if (globalPenalty < 1.0f) {
//            val penaltyPercent = (1.0f - globalPenalty) * 100
//            tooltipComponents.add(
//                Component.literal("§7Other types: §c-${String.format("%.0f", penaltyPercent)}%")
//            )
//        }
//
//        val globalRadius = Config.TYPE_CHARM_RADIUS.get().toInt()
//        tooltipComponents.add(Component.literal("§7Radius: §a$globalRadius blocks"))

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
