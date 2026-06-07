package com.darkbladenemo.cobblemoncharms.client.keybind;

import com.darkbladenemo.cobblemoncharms.CobblemonCharmsFabric;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    public static final String CATEGORY = "key.categories." + CobblemonCharmsFabric.MOD_ID;

    public static final KeyMapping OPEN_MULTI_CHARM_GUI = new KeyMapping(
            "key." + CobblemonCharmsFabric.MOD_ID + ".open_multi_charm",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );
}