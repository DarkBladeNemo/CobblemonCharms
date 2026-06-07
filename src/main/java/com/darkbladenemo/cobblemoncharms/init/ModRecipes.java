package com.darkbladenemo.cobblemoncharms.init;

import com.darkbladenemo.cobblemoncharms.common.recipe.MultiCharmRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {

    public static RecipeSerializer<MultiCharmRecipe> MULTI_CHARM_SERIALIZER;

    public static void register() {
        MULTI_CHARM_SERIALIZER = Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                ResourceLocation.fromNamespaceAndPath("cobblemoncharms", "multi_charm"),
                new MultiCharmRecipe.Serializer()
        );
    }
}