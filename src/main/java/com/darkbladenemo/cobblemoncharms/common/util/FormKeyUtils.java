package com.darkbladenemo.cobblemoncharms.common.util;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class FormKeyUtils {
    private FormKeyUtils() {}

    public static String normalize(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    /** Stable cache key for a species+form pair. */
    public static String key(String speciesPath, FormData form) {
        return speciesPath + ":" + normalize(form.getName());
    }

    /** Case-insensitive form lookup. Returns null instead of falling back to the standard form. */
    @Nullable
    public static FormData findForm(Species species, String formName) {
        String target = normalize(formName);
        FormData standard = species.getStandardForm();
        if (normalize(standard.getName()).equals(target)) return standard;
        for (FormData form : species.getForms()) {
            if (normalize(form.getName()).equals(target)) return form;
        }
        return null;
    }
}