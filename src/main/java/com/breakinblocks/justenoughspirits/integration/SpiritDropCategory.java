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
import net.minecraft.client.gui.Font;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpiritDropCategory implements IRecipeCategory<SpiritDropRecipe> {
    public static final RecipeType<SpiritDropRecipe> TYPE =
            RecipeType.create(JustEnoughSpirits.MOD_ID, "spirit_drops", SpiritDropRecipe.class);

    private static final int WIDTH = 132;
    private static final int HEIGHT = 54;

    private static final int ENTITY_X0 = 3;
    private static final int ENTITY_Y0 = 2;
    private static final int ENTITY_X1 = 43;
    private static final int ENTITY_Y1 = 52;

    private static final int GRID_REGION_X0 = 48;
    private static final int GRID_REGION_X1 = WIDTH - 4;
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
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(egg);
        }

        List<ItemStack> spirits = recipe.spirits();
        int count = spirits.size();
        int cols = Math.min(count, OUTPUT_COLS);
        int rows = Math.max(1, Mth.ceil(count / (float) OUTPUT_COLS));
        int startX = (GRID_REGION_X0 + GRID_REGION_X1) / 2 - cols * SLOT / 2;
        int startY = (HEIGHT - rows * SLOT) / 2;
        for (int i = 0; i < count; i++) {
            int col = i % OUTPUT_COLS;
            int row = i / OUTPUT_COLS;
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + col * SLOT, startY + row * SLOT)
                    .addItemStack(spirits.get(i))
                    .setStandardSlotBackground();
        }
    }

    @Override
    public void draw(SpiritDropRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        LivingEntity entity = getRenderEntity(recipe.entityType());
        if (entity != null) {
            try {
                renderEntity(graphics, entity, mouseX, mouseY);
                return;
            } catch (Throwable t) {
                failed.add(recipe.entityType());
                JustEnoughSpirits.LOGGER.debug("Failed to render entity {} in spirit drops", recipe.entityId(), t);
            }
        }
        drawNameFallback(graphics, recipe.entityType());
    }

    private void renderEntity(GuiGraphics graphics, LivingEntity entity, double mouseX, double mouseY) {
        float centerX = (ENTITY_X0 + ENTITY_X1) / 2.0F;
        float centerY = (ENTITY_Y0 + ENTITY_Y1) / 2.0F;
        float maxDim = Math.max(entity.getBbWidth(), entity.getBbHeight());
        float scale = Mth.clamp(44.0F / Math.max(maxDim, 0.1F), 6.0F, 45.0F);

        float lookX = (float) Math.atan((centerX - mouseX) / 40.0F);
        float lookY = (float) Math.atan((centerY - mouseY) / 40.0F);
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX(lookY * 20.0F * ((float) Math.PI / 180.0F));
        pose.mul(camera);

        float bodyRot = entity.yBodyRot;
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        float headRotO = entity.yHeadRotO;
        float headRot = entity.yHeadRot;
        entity.yBodyRot = 180.0F + lookX * 20.0F;
        entity.setYRot(180.0F + lookX * 40.0F);
        entity.setXRot(-lookY * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        Vector3f translate = new Vector3f(0.0F, entity.getBbHeight() / 2.0F, 0.0F);
        InventoryScreen.renderEntityInInventory(graphics, centerX, centerY, scale / entity.getScale(), translate, pose, camera, entity);

        entity.yBodyRot = bodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRotO = headRotO;
        entity.yHeadRot = headRot;
    }

    private void drawNameFallback(GuiGraphics graphics, EntityType<?> type) {
        if (type == null) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        Component name = type.getDescription();
        int centerX = (ENTITY_X0 + ENTITY_X1) / 2;
        int centerY = (ENTITY_Y0 + ENTITY_Y1) / 2;
        int textWidth = font.width(name);
        int boxWidth = ENTITY_X1 - ENTITY_X0 - 2;
        if (textWidth <= boxWidth) {
            graphics.drawString(font, name, centerX - textWidth / 2, centerY - font.lineHeight / 2, 0xFFFFFF);
            return;
        }
        graphics.pose().pushPose();
        float s = boxWidth / (float) textWidth;
        graphics.pose().translate(centerX, centerY, 0);
        graphics.pose().scale(s, s, 1.0f);
        graphics.drawString(font, name, -textWidth / 2, -font.lineHeight / 2, 0xFFFFFF);
        graphics.pose().popPose();
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
        } catch (Throwable t) {
            JustEnoughSpirits.LOGGER.debug("Failed to create entity {} for spirit drops", type, t);
        }
        failed.add(type);
        return null;
    }
}
