package com.breakinblocks.justenoughspirits.net;

import com.breakinblocks.justenoughspirits.JustEnoughSpirits;
import com.breakinblocks.justenoughspirits.client.ClientPayloadHandler;
import com.sammy.malum.core.listeners.SpiritDataReloadListener;
import com.sammy.malum.core.systems.spirit.EntitySpiritDropData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = JustEnoughSpirits.MOD_ID)
public class SpiritNetwork {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(JustEnoughSpirits.MOD_ID)
                .playToClient(SpiritSyncPayload.TYPE, SpiritSyncPayload.STREAM_CODEC,
                        (payload, context) -> ClientPayloadHandler.handleSpiritSync(payload, context));
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        SpiritSyncPayload payload = new SpiritSyncPayload(snapshot());
        if (event.getPlayer() != null) {
            PacketDistributor.sendToPlayer(event.getPlayer(), payload);
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    private static Map<ResourceLocation, List<ItemStack>> snapshot() {
        Map<ResourceLocation, List<ItemStack>> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, EntitySpiritDropData> entry : SpiritDataReloadListener.SPIRIT_DATA.entrySet()) {
            List<ItemStack> stacks = entry.getValue().getSpiritStacks();
            if (stacks != null && !stacks.isEmpty()) {
                map.put(entry.getKey(), new ArrayList<>(stacks));
            }
        }
        return map;
    }
}
