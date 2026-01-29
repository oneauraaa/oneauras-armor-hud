package oneaura.armorhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import oneaura.armorhud.HudMoveScreen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ArmorHudConfig {

        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
                        .resolve("oneauras-armor-hud.json");

        public enum Decoration {
                ARROW, NONE
        }

        public enum DisplayType {
                VALUE, PERCENT
        }

        public enum Align {
                LEFT, RIGHT, TOP, BOTTOM
        }

        public enum ListMode {
                HORIZONTAL, VERTICAL
        }

        public enum Anchor {
                TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
        }

        public enum WarningSound {
                ANVIL, EXPERIENCE, NOTE, NONE
        }

        // ==================== CONFIG VALUES ====================

        // Positions (Absolute Top-Left)
        public static int x = 10;
        public static int y = 10;

        // Scaling
        public static float scale = 1.0f;

        // Style
        public static String textColor = "#FFFFFF"; // White
        public static boolean dynamicColor = false;
        public static DisplayType damageDisplayType = DisplayType.VALUE;
        public static Align textAlignment = Align.RIGHT;
        public static ListMode listMode = ListMode.HORIZONTAL;
        public static boolean showBackground = false;

        // Spacing
        public static int textSpace = 2; // Spacing between Item and Text
        public static int itemSpace = 2; // Spacing between separate Items

        // Display Options
        public static boolean showDurability = true;
        public static boolean showMaxDurability = false;
        public static boolean showDamageBar = true; // "Damage Overlay"
        public static boolean showItemName = false;

        // Individual Armor Slots
        public static boolean showHelmet = true;
        public static boolean showChestplate = true;
        public static boolean showLeggings = true;
        public static boolean showBoots = true;

        // Hand Items
        public static boolean showMainHand = false;
        public static boolean showOffHand = false;
        public static boolean requireDamageable = true;

        // Advanced Options
        public static boolean hideWhenFull = false; // Hide items at full durability
        public static int warningThreshold = 20; // % durability to trigger warning color
        public static String warningColor = "#FF5555"; // Red warning color
        public static boolean reverseOrder = false; // Reverse display order
        public static boolean textShadow = true; // Draw text with shadow
        public static boolean hideInCreative = false; // Hide HUD in creative mode
        public static String backgroundColor = "#80000000"; // Semi-transparent black
        public static Anchor anchor = Anchor.TOP_LEFT; // Screen anchor position
        public static boolean enabled = true; // Master toggle

        // Sound Warning
        public static boolean enableSoundWarning = true; // Play sound when durability is low
        public static int soundWarningThreshold = 100; // Durability value to trigger sound
        public static WarningSound warningSound = WarningSound.ANVIL; // Sound type

        // Extra Options
        public static boolean showEnchantGlint = true; // Show enchantment glint on items
        public static boolean onlyWhenShift = false; // Only show HUD when holding shift

        // ==================== HEX COLOR PARSING ====================

        public static int parseHexColor(String hex) {
                try {
                        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
                        if (clean.length() == 6) {
                                // RGB format - add full alpha
                                return 0xFF000000 | Integer.parseInt(clean, 16);
                        } else if (clean.length() == 8) {
                                // ARGB format
                                return (int) Long.parseLong(clean, 16);
                        }
                } catch (NumberFormatException e) {
                        System.err.println("[ArmorHUD] Invalid hex color: " + hex);
                }
                return 0xFFFFFFFF; // Default white
        }

        public static int getTextColor() {
                return parseHexColor(textColor);
        }

        public static int getWarningColor() {
                return parseHexColor(warningColor);
        }

        public static int getBackgroundColor() {
                return parseHexColor(backgroundColor);
        }

        // ==================== SAVE / LOAD ====================

        public static void save() {
                ConfigData data = new ConfigData();
                data.x = x;
                data.y = y;
                data.scale = scale;
                data.textColor = textColor;
                data.dynamicColor = dynamicColor;
                data.damageDisplayType = damageDisplayType.name();
                data.textAlignment = textAlignment.name();
                data.listMode = listMode.name();
                data.showBackground = showBackground;
                data.textSpace = textSpace;
                data.itemSpace = itemSpace;
                data.showDurability = showDurability;
                data.showMaxDurability = showMaxDurability;
                data.showDamageBar = showDamageBar;
                data.showItemName = showItemName;
                data.showHelmet = showHelmet;
                data.showChestplate = showChestplate;
                data.showLeggings = showLeggings;
                data.showBoots = showBoots;
                data.showMainHand = showMainHand;
                data.showOffHand = showOffHand;
                data.requireDamageable = requireDamageable;
                data.hideWhenFull = hideWhenFull;
                data.warningThreshold = warningThreshold;
                data.warningColor = warningColor;
                data.reverseOrder = reverseOrder;
                data.textShadow = textShadow;
                data.hideInCreative = hideInCreative;
                data.backgroundColor = backgroundColor;
                data.anchor = anchor.name();
                data.enabled = enabled;
                data.enableSoundWarning = enableSoundWarning;
                data.soundWarningThreshold = soundWarningThreshold;
                data.warningSound = warningSound.name();
                data.showEnchantGlint = showEnchantGlint;
                data.onlyWhenShift = onlyWhenShift;

                try {
                        Files.writeString(CONFIG_PATH, GSON.toJson(data));
                        System.out.println("[ArmorHUD] Config saved to " + CONFIG_PATH);
                } catch (IOException e) {
                        System.err.println("[ArmorHUD] Failed to save config: " + e.getMessage());
                }
        }

        public static void load() {
                if (!Files.exists(CONFIG_PATH)) {
                        save(); // Create default config
                        return;
                }

                try {
                        String json = Files.readString(CONFIG_PATH);
                        ConfigData data = GSON.fromJson(json, ConfigData.class);
                        if (data == null)
                                return;

                        x = data.x;
                        y = data.y;
                        scale = data.scale;
                        textColor = data.textColor;
                        dynamicColor = data.dynamicColor;
                        damageDisplayType = parseEnum(DisplayType.class, data.damageDisplayType, DisplayType.VALUE);
                        textAlignment = parseEnum(Align.class, data.textAlignment, Align.RIGHT);
                        listMode = parseEnum(ListMode.class, data.listMode, ListMode.HORIZONTAL);
                        showBackground = data.showBackground;
                        textSpace = data.textSpace;
                        itemSpace = data.itemSpace;
                        showDurability = data.showDurability;
                        showMaxDurability = data.showMaxDurability;
                        showDamageBar = data.showDamageBar;
                        showItemName = data.showItemName;
                        showHelmet = data.showHelmet;
                        showChestplate = data.showChestplate;
                        showLeggings = data.showLeggings;
                        showBoots = data.showBoots;
                        showMainHand = data.showMainHand;
                        showOffHand = data.showOffHand;
                        requireDamageable = data.requireDamageable;
                        hideWhenFull = data.hideWhenFull;
                        warningThreshold = data.warningThreshold;
                        warningColor = data.warningColor;
                        reverseOrder = data.reverseOrder;
                        textShadow = data.textShadow;
                        hideInCreative = data.hideInCreative;
                        backgroundColor = data.backgroundColor;
                        anchor = parseEnum(Anchor.class, data.anchor, Anchor.TOP_LEFT);
                        enabled = data.enabled;
                        enableSoundWarning = data.enableSoundWarning;
                        soundWarningThreshold = data.soundWarningThreshold;
                        warningSound = parseEnum(WarningSound.class, data.warningSound, WarningSound.ANVIL);
                        showEnchantGlint = data.showEnchantGlint;
                        onlyWhenShift = data.onlyWhenShift;

                        System.out.println("[ArmorHUD] Config loaded from " + CONFIG_PATH);
                } catch (IOException e) {
                        System.err.println("[ArmorHUD] Failed to load config: " + e.getMessage());
                }
        }

        private static <T extends Enum<T>> T parseEnum(Class<T> clazz, String name, T defaultValue) {
                try {
                        return Enum.valueOf(clazz, name);
                } catch (Exception e) {
                        return defaultValue;
                }
        }

        // ==================== CONFIG SCREEN ====================

        public static Screen createScreen(Screen parent) {
                ConfigBuilder builder = ConfigBuilder.create()
                                .setParentScreen(parent)
                                .setTitle(Text.literal("oneaura's Armor HUD Config"));

                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                // ==================== POSITION TAB ====================
                ConfigCategory positionTab = builder.getOrCreateCategory(Text.literal("Position"));

                positionTab.addEntry(
                                entryBuilder.startTextDescription(
                                                Text.literal("Drag the HUD or use manual coordinates."))
                                                .build());

                positionTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Move HUD"), false)
                                .setDefaultValue(false)
                                .setSaveConsumer(b -> {
                                })
                                .setErrorSupplier(b -> {
                                        if (b) {
                                                MinecraftClient.getInstance().setScreen(new HudMoveScreen(parent));
                                                return Optional.empty();
                                        }
                                        return Optional.empty();
                                })
                                .setYesNoTextSupplier(b -> Text.literal("Click to Move"))
                                .build());

                positionTab.addEntry(entryBuilder.startIntField(Text.literal("X Position"), x)
                                .setDefaultValue(10)
                                .setSaveConsumer(newValue -> x = newValue)
                                .build());

                positionTab.addEntry(entryBuilder.startIntField(Text.literal("Y Position"), y)
                                .setDefaultValue(10)
                                .setSaveConsumer(newValue -> y = newValue)
                                .build());

                positionTab.addEntry(entryBuilder.startFloatField(Text.literal("Scale"), scale)
                                .setDefaultValue(1.0f)
                                .setSaveConsumer(newValue -> scale = newValue)
                                .build());

                positionTab.addEntry(entryBuilder
                                .startEnumSelector(Text.literal("Screen Anchor"), Anchor.class, anchor)
                                .setDefaultValue(Anchor.TOP_LEFT)
                                .setTooltip(Text.literal("Corner of screen to anchor HUD position"))
                                .setSaveConsumer(newValue -> anchor = newValue)
                                .build());

                positionTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("HUD Enabled"), enabled)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Master toggle to show/hide the entire HUD"))
                                .setSaveConsumer(newValue -> enabled = newValue)
                                .build());

                // ==================== APPEARANCE TAB ====================
                ConfigCategory appearanceTab = builder.getOrCreateCategory(Text.literal("Appearance"));

                appearanceTab.addEntry(entryBuilder.startStrField(Text.literal("Text Color"), textColor)
                                .setDefaultValue("#FFFFFF")
                                .setTooltip(Text.literal("Hex color like #FFFFFF or #80FFFFFF (with alpha)"))
                                .setSaveConsumer(newValue -> textColor = newValue)
                                .build());

                appearanceTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dynamic Color"), dynamicColor)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Color changes from green to red based on durability"))
                                .setSaveConsumer(newValue -> dynamicColor = newValue)
                                .build());

                appearanceTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Background"), showBackground)
                                .setDefaultValue(false)
                                .setSaveConsumer(newValue -> showBackground = newValue)
                                .build());

                appearanceTab.addEntry(entryBuilder.startIntSlider(Text.literal("Item Spacing"), itemSpace, 0, 50)
                                .setDefaultValue(4)
                                .setSaveConsumer(newValue -> itemSpace = newValue)
                                .build());

                // ==================== DISPLAY TAB ====================
                ConfigCategory displayTab = builder.getOrCreateCategory(Text.literal("Display"));

                displayTab.addEntry(entryBuilder
                                .startEnumSelector(Text.literal("Damage Display Type"), DisplayType.class,
                                                damageDisplayType)
                                .setDefaultValue(DisplayType.VALUE)
                                .setTooltip(Text.literal("VALUE = 156, PERCENT = 50%"))
                                .setSaveConsumer(newValue -> damageDisplayType = newValue)
                                .build());

                displayTab.addEntry(entryBuilder
                                .startEnumSelector(Text.literal("Text Alignment"), Align.class, textAlignment)
                                .setDefaultValue(Align.RIGHT)
                                .setTooltip(Text.literal("Position of durability text relative to item"))
                                .setSaveConsumer(newValue -> textAlignment = newValue)
                                .build());

                displayTab.addEntry(entryBuilder.startEnumSelector(Text.literal("List Mode"), ListMode.class, listMode)
                                .setDefaultValue(ListMode.HORIZONTAL)
                                .setTooltip(Text.literal("Horizontal = side by side, Vertical = stacked"))
                                .setSaveConsumer(newValue -> listMode = newValue)
                                .build());

                displayTab.addEntry(
                                entryBuilder.startBooleanToggle(Text.literal("Show Durability Numbers"), showDurability)
                                                .setDefaultValue(true)
                                                .setSaveConsumer(newValue -> showDurability = newValue)
                                                .build());

                displayTab.addEntry(
                                entryBuilder.startBooleanToggle(Text.literal("Show Max Durability"), showMaxDurability)
                                                .setDefaultValue(false)
                                                .setTooltip(Text.literal("Display as current/max (e.g. 156/250)"))
                                                .setSaveConsumer(newValue -> showMaxDurability = newValue)
                                                .build());

                displayTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Damage Overlay"), showDamageBar)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Vanilla durability bar on items"))
                                .setSaveConsumer(newValue -> showDamageBar = newValue)
                                .build());

                displayTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Item Name"), showItemName)
                                .setDefaultValue(false)
                                .setSaveConsumer(newValue -> showItemName = newValue)
                                .build());

                // ==================== ITEMS TAB ====================
                ConfigCategory itemsTab = builder.getOrCreateCategory(Text.literal("Items"));

                itemsTab.addEntry(entryBuilder.startTextDescription(Text.literal("§6Armor Slots")).build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Helmet"), showHelmet)
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> showHelmet = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Chestplate"), showChestplate)
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> showChestplate = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Leggings"), showLeggings)
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> showLeggings = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Boots"), showBoots)
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> showBoots = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder.startTextDescription(Text.literal("§6Hand Items")).build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Main Hand"), showMainHand)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Include held item in HUD"))
                                .setSaveConsumer(newValue -> showMainHand = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Offhand"), showOffHand)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Include offhand item in HUD"))
                                .setSaveConsumer(newValue -> showOffHand = newValue)
                                .build());

                itemsTab.addEntry(entryBuilder
                                .startBooleanToggle(Text.literal("Require Damageable (Hand)"), requireDamageable)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Only show hand items that have durability"))
                                .setSaveConsumer(newValue -> requireDamageable = newValue)
                                .build());

                // ==================== ADVANCED TAB ====================
                ConfigCategory advancedTab = builder.getOrCreateCategory(Text.literal("Advanced"));

                advancedTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Hide When Full"), hideWhenFull)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Only show items with damage (hide full durability)"))
                                .setSaveConsumer(newValue -> hideWhenFull = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder
                                .startIntSlider(Text.literal("Warning Threshold %"), warningThreshold, 0, 100)
                                .setDefaultValue(20)
                                .setTooltip(Text.literal("Items below this % will use warning color"))
                                .setSaveConsumer(newValue -> warningThreshold = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startStrField(Text.literal("Warning Color"), warningColor)
                                .setDefaultValue("#FF5555")
                                .setTooltip(Text.literal("Color for low durability warning (default: red)"))
                                .setSaveConsumer(newValue -> warningColor = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Reverse Order"), reverseOrder)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Reverse the display order of items"))
                                .setSaveConsumer(newValue -> reverseOrder = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Text Shadow"), textShadow)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Draw text with shadow for better visibility"))
                                .setSaveConsumer(newValue -> textShadow = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Hide in Creative"), hideInCreative)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Hide the HUD when in creative mode"))
                                .setSaveConsumer(newValue -> hideInCreative = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startStrField(Text.literal("Background Color"), backgroundColor)
                                .setDefaultValue("#80000000")
                                .setTooltip(Text.literal("Use #AARRGGBB format for transparency"))
                                .setSaveConsumer(newValue -> backgroundColor = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startTextDescription(Text.literal("§6Sound Warning")).build());

                advancedTab.addEntry(entryBuilder
                                .startBooleanToggle(Text.literal("Enable Sound Warning"), enableSoundWarning)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Play a sound when item durability is low"))
                                .setSaveConsumer(newValue -> enableSoundWarning = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder
                                .startIntSlider(Text.literal("Sound Warning Threshold"), soundWarningThreshold, 1, 500)
                                .setDefaultValue(100)
                                .setTooltip(Text.literal("Play sound when durability drops below this value"))
                                .setSaveConsumer(newValue -> soundWarningThreshold = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder
                                .startEnumSelector(Text.literal("Warning Sound"), WarningSound.class, warningSound)
                                .setDefaultValue(WarningSound.ANVIL)
                                .setTooltip(Text.literal("Sound to play for low durability warning"))
                                .setSaveConsumer(newValue -> warningSound = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder.startTextDescription(Text.literal("§6Extra Options")).build());

                advancedTab.addEntry(entryBuilder
                                .startBooleanToggle(Text.literal("Show Enchant Glint"), showEnchantGlint)
                                .setDefaultValue(true)
                                .setTooltip(Text.literal("Show enchantment shimmer effect on enchanted items"))
                                .setSaveConsumer(newValue -> showEnchantGlint = newValue)
                                .build());

                advancedTab.addEntry(entryBuilder
                                .startBooleanToggle(Text.literal("Only When Holding Shift"), onlyWhenShift)
                                .setDefaultValue(false)
                                .setTooltip(Text.literal("Only show HUD when holding the shift key"))
                                .setSaveConsumer(newValue -> onlyWhenShift = newValue)
                                .build());

                builder.setSavingRunnable(ArmorHudConfig::save);

                return builder.build();
        }

        // ==================== CONFIG DATA CLASS ====================

        private static class ConfigData {
                int x = 10;
                int y = 10;
                float scale = 1.0f;
                String textColor = "#FFFFFF";
                boolean dynamicColor = false;
                String damageDisplayType = "VALUE";
                String textAlignment = "RIGHT";
                String listMode = "HORIZONTAL";
                boolean showBackground = false;
                int textSpace = 2;
                int itemSpace = 4;
                boolean showDurability = true;
                boolean showMaxDurability = false;
                boolean showDamageBar = true;
                boolean showItemName = false;
                boolean showHelmet = true;
                boolean showChestplate = true;
                boolean showLeggings = true;
                boolean showBoots = true;
                boolean showMainHand = false;
                boolean showOffHand = false;
                boolean requireDamageable = true;
                boolean hideWhenFull = false;
                int warningThreshold = 20;
                String warningColor = "#FF5555";
                boolean reverseOrder = false;
                boolean textShadow = true;
                boolean hideInCreative = false;
                String backgroundColor = "#80000000";
                String anchor = "TOP_LEFT";
                boolean enabled = true;
                boolean enableSoundWarning = true;
                int soundWarningThreshold = 100;
                String warningSound = "ANVIL";
                boolean showEnchantGlint = true;
                boolean onlyWhenShift = false;
        }
}