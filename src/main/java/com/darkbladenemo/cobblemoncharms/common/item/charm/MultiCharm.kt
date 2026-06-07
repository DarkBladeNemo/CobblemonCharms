package com.darkbladenemo.cobblemoncharms.common.item.charm

import com.darkbladenemo.cobblemoncharms.client.util.ClientTooltipUtils
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData
import com.darkbladenemo.cobblemoncharms.common.config.Config
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import com.darkbladenemo.cobblemoncharms.network.payload.OpenMultiCharmScreenPayload
import dev.emi.trinkets.api.TrinketItem
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class MultiCharm(
    properties: Item.Properties = Item.Properties().stacksTo(1).rarity(Rarity.RARE)
) : TrinketItem(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (player.isShiftKeyDown && !level.isClientSide) {
            if (player is ServerPlayer) {
                ServerPlayNetworking.send(player, OpenMultiCharmScreenPayload())
            }
            return InteractionResultHolder.success(stack)
        }

        return InteractionResultHolder.pass(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val data = stack.get(ModDataComponents.MULTI_CHARM_DATA) ?: MultiCharmData.empty()

        tooltipComponents.add(
            Component.translatable("item.cobblemoncharms.multi_charm.tooltip")
                .withStyle(ChatFormatting.GRAY)
        )

        if (!Config.ENABLE_MULTI_CHARM.get()) {
            tooltipComponents.add(
                Component.translatable("item.cobblemoncharms.status.disabled_in_config")
                    .withStyle(ChatFormatting.RED)
            )
        } else if (data.typeEffects().isEmpty()) {
            tooltipComponents.add(
                Component.translatable("item.cobblemoncharms.multi_charm.empty")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            )
        } else {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(
                    Component.translatable("item.cobblemoncharms.multi_charm.types")
                        .withStyle(ChatFormatting.GOLD)
                )

                data.typeEffects().entries.sortedBy { it.key.name }.forEach { (type, effect) ->
                    val typeEnabled = ClientTooltipUtils.isTypeCharmEnabled(type)
                    val advPath = "type_charms/${type.translationKey}_charm"
                    val hasAdv = !Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get() ||
                            ClientTooltipUtils.hasAdvancement(advPath)

                    val statusComponent = when {
                        !typeEnabled -> Component.translatable("item.cobblemoncharms.status.disabled")
                            .withStyle(ChatFormatting.RED)
                        !hasAdv -> Component.translatable("item.cobblemoncharms.status.needs_advancement")
                            .withStyle(ChatFormatting.YELLOW)
                        !effect.enabled() -> Component.translatable("item.cobblemoncharms.status.disabled")
                            .withStyle(ChatFormatting.RED)
                        else -> Component.translatable("item.cobblemoncharms.status.enabled")
                            .withStyle(ChatFormatting.GREEN)
                    }

                    tooltipComponents.add(
                        Component.literal("  ")
                            .append(Component.translatable("cobblemon.type.${type.translationKey}"))
                            .append(Component.literal(" - "))
                            .append(statusComponent)
                    )
                }
            } else {
                val activeCount = data.typeEffects().entries.count { (type, effect) ->
                    effect.enabled() &&
                            ClientTooltipUtils.isTypeCharmEnabled(type) &&
                            (!Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get() ||
                                    ClientTooltipUtils.hasAdvancement("${type.translationKey}_charm"))
                }
                val totalCount = data.typeEffects().size

                tooltipComponents.add(
                    Component.translatable(
                        "item.cobblemoncharms.multi_charm.summary",
                        activeCount, totalCount
                    ).withStyle(ChatFormatting.AQUA)
                )

                tooltipComponents.add(
                    Component.translatable("item.cobblemoncharms.multi_charm.shift_details")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                )
            }
        }

        tooltipComponents.add(Component.empty())
        tooltipComponents.add(
            Component.translatable("item.cobblemoncharms.multi_charm.hint")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
        )
    }

    override fun getName(stack: ItemStack): Component =
        Component.translatable("item.cobblemoncharms.multi_charm")
}