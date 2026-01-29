package oneaura.armorhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import oneaura.armorhud.config.ArmorHudConfig;
import org.lwjgl.glfw.GLFW;

public class HudMoveScreen extends Screen {

    private final Screen parent;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    // Polling state
    private boolean wasMouseDown = false;
    private long lastKeyTime = 0;

    public HudMoveScreen(Screen parent) {
        super(Text.literal("Move HUD"));
        this.parent = parent;
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Call super.render FIRST to handle background (blur can only happen once)
        super.render(context, mouseX, mouseY, delta);

        // Render simple instructions
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Drag the HUD to move. Press ESC to save."),
                this.width / 2, 40, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Arrows to move. Shift for 10px."),
                this.width / 2, 55, 0xAAAAAA);

        // Render the HUD
        OneaurasArmorHUDClient.renderArmorHud(context, 0f);

        int x = ArmorHudConfig.x;
        int y = ArmorHudConfig.y;

        // Outline Anchor (Manual border using fill)
        // Top
        context.fill(x - 2, y - 2, x + 18, y - 1, 0xFFFF0000); // Top
        context.fill(x - 2, y + 17, x + 18, y + 18, 0xFFFF0000); // Bottom
        context.fill(x - 2, y - 1, x - 1, y + 17, 0xFFFF0000); // Left
        context.fill(x + 17, y - 1, x + 18, y + 17, 0xFFFF0000); // Right

        handleInput(mouseX, mouseY);
    }

    private void handleInput(int mouseX, int mouseY) {
        var window = MinecraftClient.getInstance().getWindow();
        long handle = window.getHandle();

        // Mouse Polling
        boolean isMouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;

        if (isMouseDown && !wasMouseDown) {
            // Mouse Clicked just now
            // Simple bound check or global grab
            isDragging = true;
            dragOffsetX = mouseX - ArmorHudConfig.x;
            dragOffsetY = mouseY - ArmorHudConfig.y;
        } else if (!isMouseDown && wasMouseDown) {
            // Mouse Released
            isDragging = false;
        }

        if (isDragging && isMouseDown) {
            ArmorHudConfig.x = mouseX - dragOffsetX;
            ArmorHudConfig.y = mouseY - dragOffsetY;
        }

        wasMouseDown = isMouseDown;

        // Key Polling (Debounced forarrows)
        long now = System.currentTimeMillis();
        if (now - lastKeyTime > 100) { // 100ms repeat delay
            int moveAmount = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                    || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT) ? 10 : 1;

            boolean moved = false;
            // GLFW key codes are ints, InputUtil expects them.
            // But wait, InputUtil.isKeyPressed(window, code) -> Does it take GLFW code?
            // Yes.
            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_UP)) {
                ArmorHudConfig.y -= moveAmount;
                moved = true;
            }
            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_DOWN)) {
                ArmorHudConfig.y += moveAmount;
                moved = true;
            }
            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT)) {
                ArmorHudConfig.x -= moveAmount;
                moved = true;
            }
            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT)) {
                ArmorHudConfig.x += moveAmount;
                moved = true;
            }

            if (moved) {
                lastKeyTime = now;
            }
        }
    }
}
