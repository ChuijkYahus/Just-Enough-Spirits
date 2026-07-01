package com.breakinblocks.justenoughspirits.client;

import com.breakinblocks.justenoughspirits.integration.JustEnoughSpiritsJeiPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientSpiritData {
    private static final Map<ResourceLocation, List<ItemStack>> DROPS = new ConcurrentHashMap<>();

    private ClientSpiritData() {
    }

    public static void accept(Map<ResourceLocation, List<ItemStack>> drops) {
        DROPS.clear();
        DROPS.putAll(drops);
        JustEnoughSpiritsJeiPlugin.onSpiritDataUpdated();
    }

    public static Map<ResourceLocation, List<ItemStack>> getDrops() {
        return DROPS;
    }
}
