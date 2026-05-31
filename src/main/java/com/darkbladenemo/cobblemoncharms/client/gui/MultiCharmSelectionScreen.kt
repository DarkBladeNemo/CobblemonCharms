package com.darkbladenemo.cobblemoncharms.client.gui

import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents
import com.darkbladenemo.cobblemoncharms.init.ModItems
import com.darkbladenemo.cobblemoncharms.network.payload.OpenMultiCharmFromCurioPayload
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor
import top.theillusivec4.curios.api.CuriosApi

class MultiCharmSelectionScreen(
    private val player: Player,
    private val slotIndices: List<Int>
) : Screen(Component.translatable("gui.cobblemoncharms.multi_charm_selection.title")) {

    companion object {
        private const val GUI_WIDTH = 220
        private const val PADDING = 13
        private const val BUTTON_PADDING = 15
        private const val TITLE_HEIGHT = 20
        private const val BUTTON_HEIGHT = 20
        private const val BUTTON_SPACING = 25
        private const val SECTION_SPACING = 12
        private const val CHARM_BLOCK_SPACING = 12
        private const val COLOR_TITLE = 0xFFAA00
        private const val COLOR_CHARM_LABEL = 0x55FFFF
        private const val COLOR_TYPE_LIST = 0x55FF55
        private const val COLOR_NO_ACTIVE = 0x808080

        private val BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "cobblemoncharms",
            "textures/gui/multi_charm_selection.png"
        )
        private const val TEXTURE_HEIGHT = 230
    }

    private var leftPos = 0
    private var topPos = 0

    private data class CharmEntry(
        val slotIndex: Int,
        val index: Int,
        val data: MultiCharmData,
        val typeLines: List<String>
    )

    private val charmEntries = mutableListOf<CharmEntry>()
    private var guiHeight = 0

    override fun init() {
        super.init()

        charmEntries.clear()

        CuriosApi.getCuriosInventory(player).ifPresent { inventory ->
            val slots = inventory.findCurios("type_charm_slot")
            var entryIndex = 0

            slotIndices.forEach { slotIndex ->
                if (slotIndex < slots.size) {
                    val stack = slots[slotIndex].stack()
                    if (stack.`is`(ModItems.MULTI_CHARM.get())) {
                        val data = stack.get(ModDataComponents.MULTI_CHARM_DATA.get())
                            ?: MultiCharmData.empty()
                        val typeLines = buildTypeLines(data)
                        charmEntries.add(CharmEntry(slotIndex, entryIndex + 1, data, typeLines))
                        entryIndex++
                    }
                }
            }
        }

        val buttonsHeight = TITLE_HEIGHT + (charmEntries.size * BUTTON_SPACING)
        val activeSectionHeight = SECTION_SPACING + font.lineHeight + SECTION_SPACING +
                charmEntries.sumOf { entry ->
                    font.lineHeight + 2 + (entry.typeLines.size * font.lineHeight) + CHARM_BLOCK_SPACING
                }
        guiHeight = buttonsHeight + activeSectionHeight + PADDING

        leftPos = (width - GUI_WIDTH) / 2
        topPos = (height - guiHeight) / 2

        charmEntries.forEach { entry ->
            val buttonY = topPos + TITLE_HEIGHT + (entry.index - 1) * BUTTON_SPACING
            addRenderableWidget(
                Button.builder(
                    Component.translatable(
                        "gui.cobblemoncharms.multi_charm_selection.charm_label",
                        entry.index,
                        entry.data.getEnabledEffects().size
                    )
                ) { _ ->
                    PacketDistributor.sendToServer(OpenMultiCharmFromCurioPayload(entry.slotIndex))
                    onClose()
                }
                    .bounds(leftPos + BUTTON_PADDING, buttonY + 4, GUI_WIDTH - BUTTON_PADDING * 2, BUTTON_HEIGHT)
                    .build()
            )
        }
    }

    private fun buildTypeLines(data: MultiCharmData): List<String> {
        val enabledTypes = data.getEnabledEffects()
        if (enabledTypes.isEmpty()) return emptyList()

        val maxWidth = GUI_WIDTH - PADDING * 2 - 4
        val typeNames = enabledTypes.keys.sortedBy { it.name }.map { type ->
            type.translationKey.replaceFirstChar { it.uppercase() }
        }

        val lines = mutableListOf<String>()
        var currentLine = ""

        typeNames.forEach { name ->
            val test = if (currentLine.isEmpty()) name else "$currentLine, $name"
            if (font.width(test) > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = name
            } else {
                currentLine = test
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)

        return lines
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.blit(
            BACKGROUND_TEXTURE,
            leftPos, topPos,
            0f, 0f,
            GUI_WIDTH, TEXTURE_HEIGHT,
            GUI_WIDTH, TEXTURE_HEIGHT
        )

        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val titleComponent = title.copy().withStyle(ChatFormatting.BOLD)
        val titleX = leftPos + (GUI_WIDTH / 2) - (font.width(titleComponent) / 2)
        guiGraphics.drawString(font, titleComponent, titleX, topPos + 9, COLOR_TITLE, false)

        val boostsHeaderY = topPos + TITLE_HEIGHT + (charmEntries.size * BUTTON_SPACING) + SECTION_SPACING + 2
        guiGraphics.drawString(
            font,
            Component.translatable("gui.cobblemoncharms.multi_charm_selection.active_boosts")
                .withStyle(ChatFormatting.BOLD),
            leftPos + PADDING, boostsHeaderY, COLOR_TITLE, false
        )

        var yOffset = boostsHeaderY + font.lineHeight + SECTION_SPACING / 2

        charmEntries.forEach { entry ->
            guiGraphics.drawString(
                font,
                Component.translatable(
                    "gui.cobblemoncharms.multi_charm_selection.charm_label_header",
                    entry.index
                ).withStyle(ChatFormatting.AQUA),
                leftPos + PADDING, yOffset, COLOR_CHARM_LABEL, false
            )
            yOffset += font.lineHeight + 2

            if (entry.typeLines.isEmpty()) {
                guiGraphics.drawString(
                    font,
                    Component.translatable("gui.cobblemoncharms.multi_charm_selection.no_active")
                        .withStyle(ChatFormatting.ITALIC),
                    leftPos + PADDING, yOffset, COLOR_NO_ACTIVE, false
                )
                yOffset += font.lineHeight
            } else {
                entry.typeLines.forEach { line ->
                    guiGraphics.drawString(
                        font,
                        Component.literal(line),
                        leftPos + PADDING, yOffset, COLOR_TYPE_LIST, false
                    )
                    yOffset += font.lineHeight
                }
            }

            yOffset += CHARM_BLOCK_SPACING
        }
    }

    override fun renderBlurredBackground(delta: Float) {}
    override fun renderMenuBackground(guiGraphics: GuiGraphics) {}

    override fun isPauseScreen(): Boolean = false
}