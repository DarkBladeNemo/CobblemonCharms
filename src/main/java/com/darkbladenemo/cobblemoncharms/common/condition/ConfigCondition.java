package com.darkbladenemo.cobblemoncharms.common.condition;

import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A recipe condition that reads a boolean value from the mod config.
 * Evaluated once at recipe load time — config changes require a restart.
 * <p>
 * Serializes as: { "type": "cobblemoncharms:config", "key": "enable_fire_charm" }
 */
public record ConfigCondition(String key) implements ICondition {

    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("key").forGetter(ConfigCondition::key)
            ).apply(instance, ConfigCondition::new)
    );

    private static final Map<String, ModConfigSpec.BooleanValue> CONFIG_MAP = Map.ofEntries(
            Map.entry("enable_shiny_charm",     Config.ENABLE_SHINY_CHARM),
            Map.entry("enable_exp_charm",        Config.ENABLE_EXP_CHARM),
            Map.entry("enable_multi_charm",      Config.ENABLE_MULTI_CHARM),
            Map.entry("enable_all_type_charms",  Config.ENABLE_ALL_TYPE_CHARMS),
            Map.entry("enable_normal_charm",     Config.ENABLE_NORMAL_CHARM),
            Map.entry("enable_fire_charm",       Config.ENABLE_FIRE_CHARM),
            Map.entry("enable_water_charm",      Config.ENABLE_WATER_CHARM),
            Map.entry("enable_electric_charm",   Config.ENABLE_ELECTRIC_CHARM),
            Map.entry("enable_grass_charm",      Config.ENABLE_GRASS_CHARM),
            Map.entry("enable_ice_charm",        Config.ENABLE_ICE_CHARM),
            Map.entry("enable_fighting_charm",   Config.ENABLE_FIGHTING_CHARM),
            Map.entry("enable_poison_charm",     Config.ENABLE_POISON_CHARM),
            Map.entry("enable_ground_charm",     Config.ENABLE_GROUND_CHARM),
            Map.entry("enable_flying_charm",     Config.ENABLE_FLYING_CHARM),
            Map.entry("enable_psychic_charm",    Config.ENABLE_PSYCHIC_CHARM),
            Map.entry("enable_bug_charm",        Config.ENABLE_BUG_CHARM),
            Map.entry("enable_rock_charm",       Config.ENABLE_ROCK_CHARM),
            Map.entry("enable_ghost_charm",      Config.ENABLE_GHOST_CHARM),
            Map.entry("enable_dragon_charm",     Config.ENABLE_DRAGON_CHARM),
            Map.entry("enable_dark_charm",       Config.ENABLE_DARK_CHARM),
            Map.entry("enable_steel_charm",      Config.ENABLE_STEEL_CHARM),
            Map.entry("enable_fairy_charm",      Config.ENABLE_FAIRY_CHARM),
            Map.entry("enable_all_ev_items",     Config.ENABLE_ALL_EV_ITEMS),
            Map.entry("enable_all_iv_items",     Config.ENABLE_ALL_IV_ITEMS)
    );

    @Override
    public boolean test(@NotNull ICondition.IContext context) {
        ModConfigSpec.BooleanValue value = CONFIG_MAP.get(key);
        return value == null || value.get();
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return ModConditions.CONFIG_CONDITION.get();
    }
}