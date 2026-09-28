package com.darkbladenemo.cobblemoncharms.common.util;

public final class CharmStackingUtils {
    private CharmStackingUtils() {}

    /** The additive "bonus" a multiplier contributes when stacked. */
    public static float bonus(float multiplier) {
        return multiplier - 1.0f;
    }

    /** Combines two multipliers using additive stacking: 1 + Σ(m-1). */
    public static float combine(float existing, float next) {
        return 1.0f + bonus(existing) + bonus(next);
    }
}
