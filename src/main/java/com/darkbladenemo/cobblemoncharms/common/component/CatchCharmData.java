package com.darkbladenemo.cobblemoncharms.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CatchCharmData(float multiplier) {
    public static final Codec<CatchCharmData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(CatchCharmData::multiplier)
            ).apply(instance, CatchCharmData::new)
    );
}