package com.darkbladenemo.cobblemoncharms.common.item

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.item.PokemonSelectingItem
import com.cobblemon.mod.common.api.pokemon.stats.ItemEvSource
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.pokemon.EVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.darkbladenemo.cobblemoncharms.common.util.StatDisplayUtils
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class EVBoostItem(
    private val targetStat: Stat,
    private val enabledCheck: () -> Boolean,
    properties: Properties = Properties().stacksTo(64)
) : CobblemonItem(properties), PokemonSelectingItem {

    override val bagItem = null
    val sound: SoundEvent = CobblemonSounds.MEDICINE_PILLS_USE

    private fun getEVAmount(stack: ItemStack): Int {
        val data = stack.get(ModDataComponents.EV_ITEM_DATA)
        return data?.evAmount() ?: 100
    }

    override fun canUseOnPokemon(stack: ItemStack, pokemon: Pokemon): Boolean {
        if (!enabledCheck()) return false
        return pokemon.evs.getOrDefault(targetStat) < EVs.MAX_STAT_VALUE
    }

    override fun applyToPokemon(
        player: ServerPlayer,
        stack: ItemStack,
        pokemon: Pokemon
    ): InteractionResultHolder<ItemStack> {
        if (!enabledCheck()) return InteractionResultHolder.fail(stack)

        val gained = pokemon.evs.add(targetStat, getEVAmount(stack), ItemEvSource(player, stack, pokemon))

        if (gained > 0) {
            stack.consume(1, player)
            pokemon.entity?.playSound(sound, 1F, 1F)
            return InteractionResultHolder.success(stack)
        }

        return InteractionResultHolder.fail(stack)
    }

    override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (user is ServerPlayer) {
            return use(user, user.getItemInHand(hand))
        }
        return InteractionResultHolder.success(user.getItemInHand(hand))
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val evAmount = getEVAmount(stack)
        val statName = StatDisplayUtils.tooltipName(targetStat)

        tooltipComponents.add(
            Component.translatable("tooltip.cobblemoncharms.ev_item", statName, evAmount)
        )

        if (!enabledCheck()) {
            tooltipComponents.add(
                Component.translatable("item.cobblemoncharms.status.disabled_in_config")
                    .withStyle(ChatFormatting.RED)
            )
        }
    }
}