package com.darkbladenemo.cobblemoncharms.client.gui

import com.darkbladenemo.cobblemoncharms.CobblemonCharmsFabric
import com.darkbladenemo.cobblemoncharms.client.util.ClientTooltipUtils
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData
import com.darkbladenemo.cobblemoncharms.common.config.Config
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType
import com.darkbladenemo.cobblemoncharms.network.payload.ToggleMultiCharmTypePayload
import dev.emi.trinkets.api.TrinketInventory
import dev.emi.trinkets.api.TrinketsApi
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class MultiCharmButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    private var hasType: Boolean,
    private var isEnabled: Boolean,
    private var isBlocked: Boolean,
    onPress: OnPress
) : Button(x, y, width, height, message, onPress, DEFAULT_NARRATION) {

    companion object {
        private val TEXTURE_DISABLED = ResourceLocation.fromNamespaceAndPath(
            CobblemonCharmsFabric.MOD_ID, "textures/gui/multicharm_button_disabled.png")
        private val TEXTURE_ENABLED = ResourceLocation.fromNamespaceAndPath(
            CobblemonCharmsFabric.MOD_ID, "textures/gui/multicharm_button_effect_enabled.png")
        private val TEXTURE_EFFECT_DISABLED = ResourceLocation.fromNamespaceAndPath(
            CobblemonCharmsFabric.MOD_ID, "textures/gui/multicharm_button_effect_disabled.png")
        private val TEXTURE_LOCKED = ResourceLocation.fromNamespaceAndPath(
            CobblemonCharmsFabric.MOD_ID, "textures/gui/multicharm_button_locked.png")
    }

    fun updateState(hasType: Boolean, isEnabled: Boolean, isBlocked: Boolean) {
        this.hasType = hasType
        this.isEnabled = isEnabled
        this.isBlocked = isBlocked
        this.active = hasType && !isBlocked
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val texture = when {
            !hasType  -> TEXTURE_DISABLED
            isBlocked -> TEXTURE_LOCKED
            isEnabled -> TEXTURE_ENABLED
            else      -> TEXTURE_EFFECT_DISABLED
        }
        val vOffset = if (hasType && !isBlocked && isHovered) 20 else 0
        guiGraphics.blit(texture, x, y, 0f, vOffset.toFloat(), width, height, 72, 40)
        val textColor = when {
            !hasType  -> 0xA0A0A0
            isBlocked -> 0xA0A0A0
            else      -> 0xFFFFFF
        }
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, message,
            x + width / 2, y + (height - 8) / 2, textColor)
    }

    override fun playDownSound(soundManager: net.minecraft.client.sounds.SoundManager) {
        if (hasType && !isBlocked) super.playDownSound(soundManager)
    }
}

class MultiCharmScreen(
    private val player: Player,
    private val curioSlotIndex: Int = -1,
    private val fromCurio: Boolean = false
) : Screen(Component.translatable("gui.cobblemoncharms.multi_charm.title")) {

    private val backgroundTexture = ResourceLocation.fromNamespaceAndPath(
        CobblemonCharmsFabric.MOD_ID, "textures/gui/multi_charm.png")
    private val backgroundWidth  = 176
    private val backgroundHeight = 264

    private var leftPos = 0
    private var topPos  = 0
    private var multiCharmStack: ItemStack = ItemStack.EMPTY
    private var currentData: MultiCharmData = MultiCharmData.empty()
    private val typeButtons = mutableMapOf<CharmType, MultiCharmButton>()

    override fun init() {
        super.init()

        leftPos = (width  - backgroundWidth)  / 2
        topPos  = (height - backgroundHeight) / 2

        multiCharmStack = if (fromCurio && curioSlotIndex >= 0) {
            resolveFromTrinket()
        } else {
            when {
                player.mainHandItem.item is com.darkbladenemo.cobblemoncharms.common.item.charm.MultiCharm
                    -> player.mainHandItem
                player.offhandItem.item is com.darkbladenemo.cobblemoncharms.common.item.charm.MultiCharm
                    -> player.offhandItem
                else -> ItemStack.EMPTY
            }
        }

        if (multiCharmStack.isEmpty) {
            onClose()
            return
        }

        currentData = multiCharmStack.get(ModDataComponents.MULTI_CHARM_DATA) ?: MultiCharmData.empty()
        createButtons()
    }

    /**
     * Resolves the Multi-Charm stack from Trinkets using the slot index.
     * The slot index is a linear index into all stacks in charm/type_charm.
     */
    private fun resolveFromTrinket(): ItemStack {
        var result = ItemStack.EMPTY
        TrinketsApi.getTrinketComponent(player).ifPresent { trinkets ->
            val inv: TrinketInventory? = trinkets.getInventory()["charm"]?.get("type_charm")
            if (inv != null && curioSlotIndex < inv.getContainerSize()) {
                val stack = inv.getItem(curioSlotIndex)
                if (stack.item is com.darkbladenemo.cobblemoncharms.common.item.charm.MultiCharm) {
                    result = stack
                }
            }
        }
        return result
    }

    private fun isTypeBlocked(type: CharmType): Boolean {
        if (!ClientTooltipUtils.isTypeCharmEnabled(type)) return true
        if (Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get() &&
            !ClientTooltipUtils.hasAdvancement("type_charms/${type.translationKey}_charm")) return true
        return false
    }

    private fun createButtons() {
        typeButtons.clear()
        val types = CharmType.entries.sortedBy { it.name }
        var row = 0; var col = 0

        types.forEach { type ->
            val hasType   = currentData.hasType(type)
            val isEnabled = currentData.isTypeEnabled(type)
            val isBlocked = hasType && isTypeBlocked(type)

            val buttonX = leftPos + 13 + (col * 78)
            val buttonY = topPos  + 26 + (row * 24)

            val button = MultiCharmButton(
                x = buttonX, y = buttonY, width = 72, height = 20,
                message  = getButtonLabel(type, hasType),
                hasType  = hasType, isEnabled = isEnabled, isBlocked = isBlocked,
                onPress  = { _ ->
                    if (hasType && !isBlocked) {
                        ClientPlayNetworking.send(
                            ToggleMultiCharmTypePayload(
                                type.translationKey, curioSlotIndex, fromCurio))
                    }
                }
            )
            typeButtons[type] = button
            addRenderableWidget(button)

            col++
            if (col >= 2) { col = 0; row++ }
        }
    }

    fun refreshData(newData: MultiCharmData) {
        currentData = newData
        typeButtons.forEach { (type, button) ->
            val hasType   = currentData.hasType(type)
            val isEnabled = currentData.isTypeEnabled(type)
            val isBlocked = hasType && isTypeBlocked(type)
            button.message = getButtonLabel(type, hasType)
            button.updateState(hasType, isEnabled, isBlocked)
        }
    }

    private fun getButtonLabel(type: CharmType, hasType: Boolean): Component {
        val typeName = Component.translatable("cobblemon.type.${type.translationKey}")
        return if (!hasType) Component.literal("§8").append(typeName) else typeName
    }

    override fun renderBlurredBackground(delta: Float) {}
    override fun renderMenuBackground(guiGraphics: GuiGraphics) {}

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.blit(backgroundTexture, leftPos, topPos, 0f, 0f,
            backgroundWidth, backgroundHeight, 176, 264)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        val titleX = leftPos + (backgroundWidth / 2) - (font.width(title) / 2) + 1
        guiGraphics.drawString(font, title, titleX, topPos + 9, 0x404040, false)
    }

    override fun isPauseScreen(): Boolean = false
}