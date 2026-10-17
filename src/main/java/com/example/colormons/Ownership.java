package com.example.colormons;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Whether the player may colour a Pokemon. Only consulted when config has
 * "onlyMine": true. Defaults to MINE when it cannot prove otherwise.
 */
public class Ownership {
    public static final int MINE = 0;
    public static final int WILD = 1;
    public static final int OTHER = 2;

    public static int check(Minecraft mc, PokemonEntity entity) {
        if (mc.player == null || entity == null) return MINE;
        UUID me = mc.player.getUUID();

        if (inMyParty(mc, entity)) return MINE;

        UUID owner = monOwner(entity);
        if (owner == null) return WILD;
        return owner.equals(me) ? MINE : OTHER;
    }

    private static boolean inMyParty(Minecraft mc, PokemonEntity entity) {
        try {
            UUID target = entity.getPokemon().getUuid();
            if (target == null) return false;
            for (String accessor : new String[]{"getParty", "party"}) {
                Object party = invoke(mc.player, accessor);
                if (party instanceof Iterable<?> it) {
                    for (Object mon : it) {
                        Object u = invoke(mon, "getUuid");
                        if (u instanceof UUID id && id.equals(target)) return true;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return false;
    }

    private static UUID monOwner(PokemonEntity entity) {
        Pokemon mon = entity.getPokemon();
        if (mon == null) return null;
        try {
            UUID u = mon.getOwnerUUID();
            if (u != null) return u;
        } catch (Throwable ignored) {}
        for (String name : new String[]{"getOriginalTrainerUUID", "getOwnerId"}) {
            Object v = invoke(mon, name);
            if (v instanceof UUID u) return u;
            if (v instanceof String s) {
                try { return UUID.fromString(s); } catch (Exception ignored) {}
            }
        }
        return null;
    }

    private static Object invoke(Object target, String method) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(method);
            return m.invoke(target);
        } catch (Throwable t) {
            return null;
        }
    }
}
