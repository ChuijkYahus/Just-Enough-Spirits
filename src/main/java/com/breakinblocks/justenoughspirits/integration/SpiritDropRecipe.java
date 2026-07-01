package com.breakinblocks.justenoughspirits.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.List;

public record SpiritDropRecipe(ResourceLocation entityId, List<ItemStack> spirits) {

    public EntityType<?> entityType() {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElse(null);
    }

    public ItemStack soulItem() {
        EntityType<?> type = entityType();
        if (type == null) {
            return ItemStack.EMPTY;
        }
        SpawnEggItem egg = SpawnEggItem.byId(type);
        return egg == null ? ItemStack.EMPTY : new ItemStack(egg);
    }
}
