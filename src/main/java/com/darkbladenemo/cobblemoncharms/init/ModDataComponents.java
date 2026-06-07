package com.darkbladenemo.cobblemoncharms.init;

import com.darkbladenemo.cobblemoncharms.common.component.*;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModDataComponents {

    public static DataComponentType<ExpCharmData>   EXP_CHARM_DATA;
    public static DataComponentType<CatchCharmData> CATCH_CHARM_DATA;
    public static DataComponentType<ShinyCharmData> SHINY_CHARM_DATA;
    public static DataComponentType<TypeCharmData>  TYPE_CHARM_DATA;
    public static DataComponentType<MultiCharmData> MULTI_CHARM_DATA;
    public static DataComponentType<EVItemData>     EV_ITEM_DATA;
    public static DataComponentType<IVItemData>     IV_ITEM_DATA;

    public static void register() {
        EXP_CHARM_DATA = register("exp_charm_data",
                DataComponentType.<ExpCharmData>builder()
                        .persistent(ExpCharmData.CODEC)
                        .build());

        CATCH_CHARM_DATA = register("catch_charm_data",
                DataComponentType.<CatchCharmData>builder()
                        .persistent(CatchCharmData.CODEC)
                        .build());

        SHINY_CHARM_DATA = register("shiny_charm_data",
                DataComponentType.<ShinyCharmData>builder()
                        .persistent(ShinyCharmData.CODEC)
                        .build());

        TYPE_CHARM_DATA = register("type_charm_data",
                DataComponentType.<TypeCharmData>builder()
                        .persistent(TypeCharmData.CODEC)
                        .build());

        MULTI_CHARM_DATA = register("multi_charm_data",
                DataComponentType.<MultiCharmData>builder()
                        .persistent(MultiCharmData.CODEC)
                        .build());

        EV_ITEM_DATA = register("ev_item_data",
                DataComponentType.<EVItemData>builder()
                        .persistent(EVItemData.CODEC)
                        .build());

        IV_ITEM_DATA = register("iv_item_data",
                DataComponentType.<IVItemData>builder()
                        .persistent(IVItemData.CODEC)
                        .build());
    }

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath("cobblemoncharms", name),
                type
        );
    }
}