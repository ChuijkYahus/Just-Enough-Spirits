package com.breakinblocks.justenoughspirits.client;

import com.breakinblocks.justenoughspirits.net.SpiritSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandler {

    private ClientPayloadHandler() {
    }

    public static void handleSpiritSync(SpiritSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientSpiritData.accept(payload.drops()));
    }
}
