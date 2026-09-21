package com.darkbladenemo.cobblemoncharms.common.util

import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats

/** Centralizes the stat-name label variants used across EV/IV item tooltips. */
object StatDisplayUtils {

    /** Full name, e.g. "Special Attack". Used by IVBoostItem's single-stat tooltip. */
    fun fullName(stat: Stat): String = when (stat) {
        Stats.HP -> "HP"
        Stats.ATTACK -> "Attack"
        Stats.DEFENCE -> "Defence"
        Stats.SPECIAL_ATTACK -> "Special Attack"
        Stats.SPECIAL_DEFENCE -> "Special Defence"
        Stats.SPEED -> "Speed"
        else -> "IV"
    }

    /** Three-letter abbreviation, e.g. "SpA". Used by IVBoostItem's multi-stat tooltip. */
    fun shortName(stat: Stat): String = when (stat) {
        Stats.HP -> "HP"
        Stats.ATTACK -> "Atk"
        Stats.DEFENCE -> "Def"
        Stats.SPECIAL_ATTACK -> "SpA"
        Stats.SPECIAL_DEFENCE -> "SpD"
        Stats.SPEED -> "Spe"
        else -> "?"
    }

    /** Dotted abbreviation, e.g. "Sp. Atk". Used by EVBoostItem's tooltip. */
    fun tooltipName(stat: Stat): String = when (stat) {
        Stats.HP -> "HP"
        Stats.ATTACK -> "Attack"
        Stats.DEFENCE -> "Defence"
        Stats.SPECIAL_ATTACK -> "Sp. Atk"
        Stats.SPECIAL_DEFENCE -> "Sp. Def"
        Stats.SPEED -> "Speed"
        else -> "EV"
    }
}