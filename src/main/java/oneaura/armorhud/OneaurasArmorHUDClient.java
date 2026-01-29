package oneaura.armorhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import oneaura.armorhud.config.ArmorHudConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OneaurasArmorHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArmorHudConfig.load();
        HudRenderCallback.EVENT.register(OneaurasArmorHUDClient::renderArmorHud);
    }

    // Public Static so HudMoveScreen can call it
    public static void renderArmorHud(DrawContext context, RenderTickCounter tickCounter) {
        renderInternal(context);
    }

    // Helper to accept just context from Screen
    public static void renderArmorHud(DrawContext context, float tickDelta) {
        renderInternal(context);
    }

    private static void renderInternal(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null)
            return;

        // Master toggle
        if (!ArmorHudConfig.enabled)
            return;

        if (client.options.hudHidden && client.currentScreen == null)
            return;

        // Hide in creative mode
        if (ArmorHudConfig.hideInCreative && client.player.isCreative())
            return;

        // Scale Logic REMOVED FOR COMPATIBILITY
        // float scale = ArmorHudConfig.scale;
        // context.getMatrices().push();
        // context.getMatrices().scale(scale, scale, 1.0f);

        // Collect items to show
        List<ItemStack> itemsToCheck = new ArrayList<>();

        // Armor (Helmet -> Boots) - respect individual toggles
        if (ArmorHudConfig.showHelmet) {
            itemsToCheck.add(client.player.getEquippedStack(EquipmentSlot.HEAD));
        }
        if (ArmorHudConfig.showChestplate) {
            itemsToCheck.add(client.player.getEquippedStack(EquipmentSlot.CHEST));
        }
        if (ArmorHudConfig.showLeggings) {
            itemsToCheck.add(client.player.getEquippedStack(EquipmentSlot.LEGS));
        }
        if (ArmorHudConfig.showBoots) {
            itemsToCheck.add(client.player.getEquippedStack(EquipmentSlot.FEET));
        }

        // Main Hand
        if (ArmorHudConfig.showMainHand) {
            itemsToCheck.add(client.player.getMainHandStack());
        }

        // Off Hand
        if (ArmorHudConfig.showOffHand) {
            itemsToCheck.add(client.player.getOffHandStack());
        }

        // Filter items
        List<ItemStack> itemsToDraw = new ArrayList<>();
        for (ItemStack stack : itemsToCheck) {
            if (stack.isEmpty())
                continue;

            // "Require Damageable (Hand)" check
            boolean isHandItem = (stack == client.player.getMainHandStack())
                    || (stack == client.player.getOffHandStack());
            if (isHandItem && ArmorHudConfig.requireDamageable && !stack.isDamageable()) {
                continue;
            }

            // Hide when full durability
            if (ArmorHudConfig.hideWhenFull && stack.isDamageable() && stack.getDamage() == 0) {
                continue;
            }

            itemsToDraw.add(stack);
        }

        // Reverse order if enabled
        if (ArmorHudConfig.reverseOrder) {
            Collections.reverse(itemsToDraw);
        }

        if (itemsToDraw.isEmpty()) {
            // context.getMatrices().pop();
            return;
        }

        TextRenderer textRenderer = client.textRenderer;

        // Calculate position based on anchor
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = ArmorHudConfig.x;
        int y = ArmorHudConfig.y;

        // Adjust position based on anchor
        switch (ArmorHudConfig.anchor) {
            case TOP_LEFT:
                // x and y are offsets from top-left (default)
                break;
            case TOP_RIGHT:
                x = screenWidth - ArmorHudConfig.x - 16; // 16 = item size
                break;
            case BOTTOM_LEFT:
                y = screenHeight - ArmorHudConfig.y - 16;
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - ArmorHudConfig.x - 16;
                y = screenHeight - ArmorHudConfig.y - 16;
                break;
        }

        // Calculate Direction steps
        int stepX = 0;
        int stepY = 0;

        if (ArmorHudConfig.listMode == ArmorHudConfig.ListMode.HORIZONTAL) {
            stepX = 16 + ArmorHudConfig.itemSpace;
            // Reverse direction for right-anchored
            if (ArmorHudConfig.anchor == ArmorHudConfig.Anchor.TOP_RIGHT
                    || ArmorHudConfig.anchor == ArmorHudConfig.Anchor.BOTTOM_RIGHT) {
                stepX = -stepX;
            }
        } else {
            // Vertical Stack
            stepY = 16 + ArmorHudConfig.itemSpace;
            // Reverse direction for bottom-anchored
            if (ArmorHudConfig.anchor == ArmorHudConfig.Anchor.BOTTOM_LEFT
                    || ArmorHudConfig.anchor == ArmorHudConfig.Anchor.BOTTOM_RIGHT) {
                stepY = -stepY;
            }
        }

        for (ItemStack stack : itemsToDraw) {

            int extraStep = 0;

            renderItemEntry(context, textRenderer, stack, x, y);

            if (ArmorHudConfig.listMode == ArmorHudConfig.ListMode.HORIZONTAL) {
                if (ArmorHudConfig.showDurability && stack.isDamageable() &&
                        (ArmorHudConfig.textAlignment == ArmorHudConfig.Align.RIGHT)) {
                    String text = getDurabilityText(stack);
                    extraStep = textRenderer.getWidth(text) + ArmorHudConfig.textSpace;
                }
                x += stepX + extraStep;
            } else {
                y += stepY;
            }
        }

        // context.getMatrices().pop();
    }

    private static void renderItemEntry(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        // Draw Background
        if (ArmorHudConfig.showBackground) {
            context.fill(x - 1, y - 1, x + 17, y + 17, ArmorHudConfig.getBackgroundColor());
        }

        // Draw Item
        context.drawItem(stack, x, y);

        // Draw Damage Overlay (Bar)
        if (ArmorHudConfig.showDamageBar) {
            context.drawStackOverlay(textRenderer, stack, x, y);
        }

        // Draw Text
        if (ArmorHudConfig.showDurability && stack.isDamageable()) {
            String text = getDurabilityText(stack);
            int color = getDurabilityColor(stack);

            int tx = x;
            int ty = y;

            switch (ArmorHudConfig.textAlignment) {
                case RIGHT:
                    tx = x + 16 + ArmorHudConfig.textSpace;
                    ty = y + 4; // Center vertically (approx)
                    break;
                case LEFT:
                    int w = textRenderer.getWidth(text);
                    tx = x - w - ArmorHudConfig.textSpace;
                    ty = y + 4;
                    break;
                case TOP:
                    tx = x + (16 - textRenderer.getWidth(text)) / 2;
                    ty = y - 8 - ArmorHudConfig.textSpace; // Above item
                    break;
                case BOTTOM:
                    tx = x + (16 - textRenderer.getWidth(text)) / 2;
                    ty = y + 16 + ArmorHudConfig.textSpace; // Below item
                    break;
            }

            if (ArmorHudConfig.textShadow) {
                context.drawTextWithShadow(textRenderer, text, tx, ty, color);
            } else {
                context.drawText(textRenderer, text, tx, ty, color, false);
            }
        }
    }

    private static String getDurabilityText(ItemStack stack) {
        int max = stack.getMaxDamage();
        int cur = stack.getDamage();
        int rem = max - cur;

        if (ArmorHudConfig.damageDisplayType == ArmorHudConfig.DisplayType.PERCENT) {
            int percent = (int) Math.round(((double) rem / max) * 100);
            return percent + "%";
        }

        return String.valueOf(rem);
    }

    private static int getDurabilityColor(ItemStack stack) {
        int max = stack.getMaxDamage();
        int remaining = max - stack.getDamage();
        float percent = (float) remaining / max * 100f;

        // Warning color if below threshold
        if (percent <= ArmorHudConfig.warningThreshold) {
            return ArmorHudConfig.getWarningColor();
        }

        if (!ArmorHudConfig.dynamicColor) {
            return ArmorHudConfig.getTextColor();
        }

        // Dynamic: Green -> Red
        float f = Math.max(0.0F, (float) remaining / (float) max);
        return net.minecraft.util.math.MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }
}