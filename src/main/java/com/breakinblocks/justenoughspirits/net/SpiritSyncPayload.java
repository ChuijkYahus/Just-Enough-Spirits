package com.breakinblocks.justenoughspirits.net;

import com.breakinblocks.justenoughspirits.JustEnoughSpirits;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SpiritSyncPayload(Map<ResourceLocation, List<ItemStack>> drops) implements CustomPacketPayload {
    public static final Type<SpiritSyncPayload> TYPE = new Type<>(JustEnoughSpirits.id("spirit_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<ItemStack>>> MAP_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpiritSyncPayload> STREAM_CODEC =
            MAP_CODEC.map(SpiritSyncPayload::new, SpiritSyncPayload::drops);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
