package com.breakinblocks.justenoughspirits.integration;

import com.breakinblocks.justenoughspirits.client.ClientSpiritData;
import com.sammy.malum.core.listeners.SpiritDataReloadListener;
import com.sammy.malum.core.systems.spirit.EntitySpiritDropData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class SpiritDropProvider {

    private SpiritDropProvider() {
    }

    public static List<SpiritDropRecipe> getRecipes() {
        List<SpiritDropRecipe> recipes = new ArrayList<>();

        Map<ResourceLocation, EntitySpiritDropData> serverData = SpiritDataReloadListener.SPIRIT_DATA;
        if (!serverData.isEmpty()) {
            for (Map.Entry<ResourceLocation, EntitySpiritDropData> entry : serverData.entrySet()) {
                add(recipes, entry.getKey(), entry.getValue().getSpiritStacks());
            }
        } else {
            for (Map.Entry<ResourceLocation, List<ItemStack>> entry : ClientSpiritData.getDrops().entrySet()) {
                add(recipes, entry.getKey(), entry.getValue());
            }
        }

        recipes.sort(Comparator.comparing(SpiritDropProvider::sortKey));
        return recipes;
    }

    private static void add(List<SpiritDropRecipe> recipes, ResourceLocation entityId, List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return;
        }
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
            return;
        }
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                copy.add(stack.copy());
            }
        }
        if (!copy.isEmpty()) {
            recipes.add(new SpiritDropRecipe(entityId, copy));
        }
    }

    private static String sortKey(SpiritDropRecipe recipe) {
        EntityType<?> type = recipe.entityType();
        Component name = type != null ? type.getDescription() : Component.literal(recipe.entityId().toString());
        return name.getString().toLowerCase();
    }
}
