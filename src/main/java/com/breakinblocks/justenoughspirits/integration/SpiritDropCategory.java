package com.breakinblocks.justenoughspirits.integration;

import com.breakinblocks.justenoughspirits.JustEnoughSpirits;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpiritDropCategory implements IRecipeCategory<SpiritDropRecipe> {
    public static final RecipeType<SpiritDropRecipe> TYPE =
            RecipeType.create(JustEnoughSpirits.MOD_ID, "spirit_drops", SpiritDropRecipe.class);

    private static final int WIDTH = 140;
    private static final int HEIGHT = 64;

    private static final int ENTITY_X0 = 2;
    private static final int ENTITY_Y0 = 2;
    private static final int ENTITY_X1 = 40;
    private static final int ENTITY_Y1 = 62;

    private static final int EGG_X = 44;
    private static final int EGG_Y = 24;

    private static final int OUTPUT_X = 66;
    private static final int OUTPUT_Y = 4;
    private static final int OUTPUT_COLS = 4;
    private static final int SLOT = 18;

    private final IDrawable icon;
    private final Map<EntityType<?>, LivingEntity> entityCache = new HashMap<>();
    private final Set<EntityType<?>> failed = new HashSet<>();

    public SpiritDropCategory(IGuiHelper guiHelper) {
        ItemStack iconStack = BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath("malum", "arcane_spirit"))
                .map(ItemStack::new)
                .orElseGet(() -> new ItemStack(Items.NAME_TAG));
        this.icon = guiHelper.createDrawableItemStack(iconStack);
    }

    @Override
    public RecipeType<SpiritDropRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jes.category.spirit_drops");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpiritDropRecipe recipe, IFocusGroup focuses) {
        ItemStack egg = recipe.soulItem();
        if (!egg.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, EGG_X, EGG_Y)
                    .addItemStack(egg)
                    .setStandardSlotBackground();
        }

        List<ItemStack> spirits = recipe.spirits();
        for (int i = 0; i < spirits.size(); i++) {
            int col = i % OUTPUT_COLS;
            int row = i / OUTPUT_COLS;
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + col * SLOT, OUTPUT_Y + row * SLOT)
                    .addItemStack(spirits.get(i))
                    .setOutputSlotBackground();
        }
    }

    @Override
    public void draw(SpiritDropRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        LivingEntity entity = getRenderEntity(recipe.entityType());
        if (entity == null) {
            return;
        }
        float maxDim = Math.max(entity.getBbWidth(), entity.getBbHeight());
        int scale = (int) Mth.clamp(30.0f / Math.max(maxDim, 0.1f), 6, 30);
        try {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics, ENTITY_X0, ENTITY_Y0, ENTITY_X1, ENTITY_Y1,
                    scale, 0.0f, (float) mouseX, (float) mouseY, entity);
        } catch (Exception e) {
            failed.add(recipe.entityType());
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, SpiritDropRecipe recipe, IRecipeSlotsView slotsView, double mouseX, double mouseY) {
        if (mouseX >= ENTITY_X0 && mouseX <= ENTITY_X1 && mouseY >= ENTITY_Y0 && mouseY <= ENTITY_Y1) {
            EntityType<?> type = recipe.entityType();
            if (type != null) {
                tooltip.add(type.getDescription());
            }
        }
    }

    private LivingEntity getRenderEntity(EntityType<?> type) {
        if (type == null || failed.contains(type)) {
            return null;
        }
        LivingEntity cached = entityCache.get(type);
        if (cached != null) {
            return cached;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        try {
            Entity created = type.create(level);
            if (created instanceof LivingEntity living) {
                entityCache.put(type, living);
                return living;
            }
        } catch (Throwable ignored) {
        }
        failed.add(type);
        return null;
    }
}
