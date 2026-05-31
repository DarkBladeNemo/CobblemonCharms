package com.darkbladenemo.cobblemoncharms.common.component;

import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TypeCharmData(
        CharmType type,
        float matchMultiplier
) {
    public static final Codec<TypeCharmData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.flatXmap(
                            s -> {
                                CharmType type = CharmType.fromString(s);
                                return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown CharmType: " + s);
                            },
                            type -> DataResult.success(type.getTranslationKey())
                    ).fieldOf("type").forGetter(TypeCharmData::type),
                    Codec.FLOAT.fieldOf("match_multiplier").forGetter(TypeCharmData::matchMultiplier)
            ).apply(instance, TypeCharmData::new)
    );
}