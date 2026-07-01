package com.breakinblocks.justenoughspirits.integration;

import com.breakinblocks.justenoughspirits.JustEnoughSpirits;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public class JustEnoughSpiritsJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = JustEnoughSpirits.id("main");

    private static IJeiRuntime runtime;
    private static List<SpiritDropRecipe> lastAdded = List.of();

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SpiritDropCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SpiritDropRecipe> recipes = SpiritDropProvider.getRecipes();
        registration.addRecipes(SpiritDropCategory.TYPE, recipes);
        lastAdded = recipes;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        onSpiritDataUpdated();
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static void onSpiritDataUpdated() {
        if (runtime == null) {
            return;
        }
        try {
            if (!lastAdded.isEmpty()) {
                runtime.getRecipeManager().hideRecipes(SpiritDropCategory.TYPE, lastAdded);
            }
            List<SpiritDropRecipe> recipes = SpiritDropProvider.getRecipes();
            runtime.getRecipeManager().addRecipes(SpiritDropCategory.TYPE, recipes);
            lastAdded = recipes;
        } catch (Exception e) {
            JustEnoughSpirits.LOGGER.error("Failed to refresh spirit drop recipes", e);
        }
    }
}
