package com.breakinblocks.justenoughspirits;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(JustEnoughSpirits.MOD_ID)
public class JustEnoughSpirits {
    public static final String MOD_ID = "justenoughspirits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustEnoughSpirits(IEventBus eventBus, ModContainer container, Dist dist) {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
